package com.example.customerchallenge.data.repository

import com.example.customerchallenge.data.remote.datasource.CustomerRemoteDataSource
import com.example.customerchallenge.data.remote.dto.CustomerDTO
import com.example.customerchallenge.data.remote.dto.CustomersResponseDTO
import com.example.customerchallenge.data.remote.error.NetworkException
import com.example.customerchallenge.domain.model.Customer
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertSame
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test


class CustomerRepositoryImplTest {

    private lateinit var remoteDataSource: CustomerRemoteDataSource
    private lateinit var repository: CustomerRepositoryImpl

    @Before
    fun setUp() {
        remoteDataSource = mockk()
        repository = CustomerRepositoryImpl(
            remoteDataSource = remoteDataSource
        )
    }

    @Test
    fun `getCustomers should map response to domain customers`() = runTest {
        val response = CustomersResponseDTO(
            customers = listOf(
                CustomerDTO(
                    id = "1",
                    name = "Caio Lima",
                    status = "Active",
                    email = "caio@example.com",
                    phone = "+55 91 99999-9999",
                    profileImage = null,
                    profileLink = "https://github.com/caio"
                )
            )
        )

        val expectedCustomers = listOf(
            Customer(
                id = "1",
                name = "Caio Lima",
                status = "Active",
                email = "caio@example.com",
                phone = "+55 91 99999-9999",
                profileImage = null,
                profileLink = "https://github.com/caio"
            )
        )

        coEvery {
            remoteDataSource.getCustomers()
        } returns Result.success(response)

        val result = repository.getCustomers()

        assertTrue(result.isSuccess)
        assertEquals(expectedCustomers, result.getOrNull())

        coVerify(exactly = 1) {
            remoteDataSource.getCustomers()
        }
    }

    @Test
    fun `getCustomers should preserve remote data source failure`() = runTest {
        val expectedException = NetworkException.NoConnection()

        coEvery {
            remoteDataSource.getCustomers()
        } returns Result.failure(expectedException)

        val result = repository.getCustomers()

        assertTrue(result.isFailure)
        assertSame(
            expectedException,
            result.exceptionOrNull()
        )

        coVerify(exactly = 1) {
            remoteDataSource.getCustomers()
        }
    }

    @Test
    fun `getCustomers should return empty list when response has no customers`() = runTest {
        val response = CustomersResponseDTO(
            customers = emptyList()
        )

        coEvery {
            remoteDataSource.getCustomers()
        } returns Result.success(response)

        val result = repository.getCustomers()

        assertTrue(result.isSuccess)
        assertTrue(
            result.getOrNull().orEmpty().isEmpty()
        )

        coVerify(exactly = 1) {
            remoteDataSource.getCustomers()
        }
    }
}