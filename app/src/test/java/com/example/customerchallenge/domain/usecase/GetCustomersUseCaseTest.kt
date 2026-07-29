package com.example.customerchallenge.domain.usecase

import com.example.customerchallenge.domain.model.Customer
import com.example.customerchallenge.domain.repository.CustomerRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertSame
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetCustomersUseCaseTest {

    private lateinit var repository: CustomerRepository
    private lateinit var useCase: GetCustomersUseCase

    @Before
    fun setUp() {
        repository = mockk()

        useCase = GetCustomersUseCase(
            repository = repository
        )
    }

    @Test
    fun `invoke should return customers when repository succeeds`() = runTest {
        val expectedCustomers = listOf(
            Customer(
                id = "1",
                name = "Caio Lima",
                status = "Active",
                email = "caio@example.com",
                phone = "+55 91 99999-9999",
                profileImage = null,
                profileLink = "https://example.com/profile"
            )
        )

        coEvery {
            repository.getCustomers()
        } returns Result.success(expectedCustomers)

        val result = useCase()

        assertTrue(result.isSuccess)
        assertEquals(expectedCustomers, result.getOrNull())

        coVerify(exactly = 1) {
            repository.getCustomers()
        }
    }

    @Test
    fun `invoke should preserve repository failure`() = runTest {
        val expectedException = IllegalStateException(
            "Unable to load customers"
        )

        coEvery {
            repository.getCustomers()
        } returns Result.failure(expectedException)

        val result = useCase()

        assertTrue(result.isFailure)
        assertSame(
            expectedException,
            result.exceptionOrNull()
        )

        coVerify(exactly = 1) {
            repository.getCustomers()
        }
    }
}