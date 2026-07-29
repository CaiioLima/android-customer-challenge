package com.example.customerchallenge.data.repository

import com.example.customerchallenge.data.remote.customer.datasource.CustomerRemoteDataSource
import com.example.customerchallenge.data.remote.customer.mapper.toDomain
import com.example.customerchallenge.domain.model.Customer
import com.example.customerchallenge.domain.repository.CustomerRepository

class CustomerRepositoryImpl(
    private val remoteDataSource: CustomerRemoteDataSource
) : CustomerRepository {
    override suspend fun getCustomers(): Result<List<Customer>> {
        return remoteDataSource
            .getCustomers()
            .map { response ->
                response.toDomain()
            }
    }
}