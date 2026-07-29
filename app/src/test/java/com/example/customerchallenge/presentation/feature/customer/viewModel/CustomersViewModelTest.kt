package com.example.customerchallenge.presentation.feature.customer.viewModel

import android.util.Log
import com.example.customerchallenge.domain.model.Customer
import com.example.customerchallenge.domain.usecase.GetCustomersUseCase
import com.example.customerchallenge.presentation.CustomersUIAction
import com.example.customerchallenge.presentation.CustomersUIState
import com.example.customerchallenge.presentation.customer.CustomersUISideEffect
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CustomersViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getCustomersUseCase: GetCustomersUseCase

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        getCustomersUseCase = mockk()
        mockkStatic(Log::class)

        every { Log.e(any(), any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
        Dispatchers.resetMain()
    }

    @Test
    fun `init should update state to success when customers are loaded`() = runTest {
        val customers = listOf(createCustomer())

        coEvery {
            getCustomersUseCase()
        } returns Result.success(customers)

        val viewModel = createViewModel()

        advanceUntilIdle()

        assertEquals(
            CustomersUIState.Success(customers),
            viewModel.uiState.value
        )

        coVerify(exactly = 1) {
            getCustomersUseCase()
        }
    }

    @Test
    fun `init should update state to empty when repository returns empty list`() = runTest {
        coEvery {
            getCustomersUseCase()
        } returns Result.success(emptyList())

        val viewModel = createViewModel()

        advanceUntilIdle()

        assertEquals(
            CustomersUIState.Empty,
            viewModel.uiState.value
        )

        coVerify(exactly = 1) {
            getCustomersUseCase()
        }
    }

    @Test
    fun `init should update state to error and emit show error when loading fails`() = runTest {
        val expectedMessage = "Network unavailable"

        coEvery {
            getCustomersUseCase()
        } returns Result.failure(
            IllegalStateException(expectedMessage)
        )

        val viewModel = createViewModel()

        val sideEffectDeferred = backgroundScope.launch {
            val sideEffect = viewModel.sideEffect.first()

            assertEquals(
                CustomersUISideEffect.ShowError(expectedMessage),
                sideEffect
            )
        }

        advanceUntilIdle()

        assertEquals(
            CustomersUIState.Error(expectedMessage),
            viewModel.uiState.value
        )

        sideEffectDeferred.cancel()
    }

    @Test
    fun `retry should load customers again`() = runTest {
        val customers = listOf(createCustomer())

        coEvery {
            getCustomersUseCase()
        } returnsMany listOf(
            Result.failure(
                IllegalStateException("First request failed")
            ),
            Result.success(customers)
        )

        val viewModel = createViewModel()

        advanceUntilIdle()

        assertTrue(
            viewModel.uiState.value is CustomersUIState.Error
        )

        viewModel.onAction(
            CustomersUIAction.Retry
        )

        advanceUntilIdle()

        assertEquals(
            CustomersUIState.Success(customers),
            viewModel.uiState.value
        )

        coVerify(exactly = 2) {
            getCustomersUseCase()
        }
    }

    @Test
    fun `open profile action should emit open customer profile effect`() =
        runTest(testDispatcher) {
            val profileLink = "https://example.com/profile"

            coEvery {
                getCustomersUseCase()
            } returns Result.success(emptyList())

            val viewModel = createViewModel()

            advanceUntilIdle()

            val effectDeferred = async(
                start = CoroutineStart.UNDISPATCHED
            ) {
                viewModel.sideEffect.first()
            }

            viewModel.onAction(
                CustomersUIAction.OpenProfileClicked(
                    profileLink = profileLink
                )
            )

            advanceUntilIdle()

            assertEquals(
                CustomersUISideEffect.OpenCustomerProfile(
                    profileLink = profileLink
                ),
                effectDeferred.await()
            )
        }

    @Test
    fun `open profile action should not emit effect when link is blank`() = runTest {
        coEvery {
            getCustomersUseCase()
        } returns Result.success(emptyList())

        val viewModel = createViewModel()

        advanceUntilIdle()

        var emittedEffect: CustomersUISideEffect? = null

        val collector = backgroundScope.launch {
            emittedEffect = viewModel.sideEffect.first()
        }

        viewModel.onAction(
            CustomersUIAction.OpenProfileClicked(
                profileLink = "   "
            )
        )

        advanceUntilIdle()

        assertEquals(null, emittedEffect)

        collector.cancel()
    }

    @Test
    fun `open image action should emit open customer image effect`() =
        runTest(testDispatcher) {
            val imageUrl = "https://example.com/customer.jpg"

            coEvery {
                getCustomersUseCase()
            } returns Result.success(emptyList())

            val viewModel = createViewModel()

            advanceUntilIdle()

            val effectDeferred = async(
                start = CoroutineStart.UNDISPATCHED
            ) {
                viewModel.sideEffect.first()
            }

            viewModel.onAction(
                CustomersUIAction.OpenImageClicked(
                    imageUrl = imageUrl
                )
            )

            advanceUntilIdle()

            assertEquals(
                CustomersUISideEffect.OpenCustomerImage(
                    imageUrl = imageUrl
                ),
                effectDeferred.await()
            )
        }

    private fun createViewModel(): CustomersViewModel {
        return CustomersViewModel(
            getCustomersUseCase = getCustomersUseCase
        )
    }

    private fun createCustomer(): Customer {
        return Customer(
            id = "1",
            name = "Caio Lima",
            status = "Active",
            email = "caio@example.com",
            phone = "+55 91 99999-9999",
            profileImage = "https://example.com/customer.jpg",
            profileLink = "https://example.com/profile"
        )
    }
}