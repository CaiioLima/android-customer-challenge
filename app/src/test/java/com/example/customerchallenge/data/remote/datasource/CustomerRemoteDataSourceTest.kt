package com.example.customerchallenge.data.remote.datasource



import com.example.customerchallenge.data.remote.customer.api.CustomerApi
import com.example.customerchallenge.data.remote.customer.datasource.CustomerRemoteDataSource
import com.example.customerchallenge.data.remote.customer.dto.CustomersResponseDTO
import com.example.customerchallenge.data.remote.customer.error.NetworkException

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertSame
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.net.UnknownHostException


class CustomerRemoteDataSourceTest {

    private lateinit var api: CustomerApi
    private lateinit var remoteDataSource: CustomerRemoteDataSource

    @Before
    fun setUp() {
        api = mockk()
        remoteDataSource = CustomerRemoteDataSource(api = api)
    }

    @Test
    fun `getCustomers should return success when api returns customers`() = runTest {
        val expectedResponse = mockk<CustomersResponseDTO>()

        coEvery {
            api.getCustomers()
        } returns expectedResponse

        val result = remoteDataSource.getCustomers()

        assertTrue(result.isSuccess)
        assertSame(expectedResponse, result.getOrNull())

        coVerify(exactly = 1) {
            api.getCustomers()
        }
    }

    @Test
    fun `getCustomers should map unknown host exception to no connection`() = runTest {
        coEvery {
            api.getCustomers()
        } throws UnknownHostException("Unable to resolve host")

        val result = remoteDataSource.getCustomers()

        assertTrue(result.isFailure)
        assertTrue(
            result.exceptionOrNull() is NetworkException.NoConnection
        )

        coVerify(exactly = 1) {
            api.getCustomers()
        }
    }

    @Test
    fun `getCustomers should map server error response`() = runTest {
        val httpException = createHttpException(statusCode = 500)

        coEvery {
            api.getCustomers()
        } throws httpException

        val result = remoteDataSource.getCustomers()
        val exception = result.exceptionOrNull()

        assertTrue(result.isFailure)
        assertTrue(exception is NetworkException.ServerError)

        val serverError = exception as NetworkException.ServerError

        assertEquals(500, serverError.statusCode)

        coVerify(exactly = 1) {
            api.getCustomers()
        }
    }

    private fun createHttpException(
        statusCode: Int
    ): HttpException {
        val errorBody = """
            {
              "message": "Unexpected server error"
            }
        """.trimIndent().toResponseBody(
            contentType = "application/json".toMediaType()
        )

        return HttpException(
            Response.error<Any>(
                statusCode,
                errorBody
            )
        )
    }
}