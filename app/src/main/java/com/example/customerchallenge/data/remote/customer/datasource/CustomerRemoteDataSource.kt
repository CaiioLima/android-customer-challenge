package com.example.customerchallenge.data.remote.datasource

import com.example.customerchallenge.data.remote.api.CustomerApi
import com.example.customerchallenge.data.remote.dto.CustomersResponseDTO
import com.example.customerchallenge.data.remote.error.NetworkErrorMapper
import com.example.customerchallenge.data.remote.error.mapException

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