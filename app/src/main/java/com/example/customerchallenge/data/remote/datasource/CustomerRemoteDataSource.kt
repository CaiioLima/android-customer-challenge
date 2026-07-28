package com.example.customerchallenge.data.remote.datasource

import android.net.http.NetworkException
import com.example.customerchallenge.data.remote.api.CustomerApi
import com.example.customerchallenge.data.remote.dto.CustomersResponseDTO
import com.example.customerchallenge.data.remote.error.NetworkErrorMapper
import com.example.customerchallenge.data.remote.error.mapException
import retrofit2.HttpException

class CustomerRemoteDataSource(
    private val api: CustomerApi
) {
    suspend fun getCustomers(): Result<CustomersResponseDTO> =
        runCatching {
            api.getCustomers()
        }.mapException { throwable ->
            NetworkErrorMapper.map(throwable)
        }
}