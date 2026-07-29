package com.example.customerchallenge.data.remote.customer.datasource

import com.example.customerchallenge.data.remote.customer.api.CustomerApi
import com.example.customerchallenge.data.remote.customer.dto.CustomersResponseDTO
import com.example.customerchallenge.data.remote.customer.error.NetworkErrorMapper
import com.example.customerchallenge.data.remote.customer.error.mapException

class CustomerRemoteDataSource(
    private val api: com.example.customerchallenge.data.remote.customer.api.CustomerApi
) {
    suspend fun getCustomers(): Result<com.example.customerchallenge.data.remote.customer.dto.CustomersResponseDTO> =
        runCatching {
            api.getCustomers()
        }.mapException { throwable ->
            _root_ide_package_.com.example.customerchallenge.data.remote.customer.error.NetworkErrorMapper.map(throwable)
        }
}