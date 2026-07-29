package com.example.customerchallenge.data.remote.customer.datasource

import com.example.customerchallenge.data.remote.customer.api.CustomerApi
import com.example.customerchallenge.data.remote.customer.dto.CustomersResponseDTO
import com.example.customerchallenge.data.remote.customer.error.NetworkErrorMapper
import com.example.customerchallenge.data.remote.customer.error.mapException

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