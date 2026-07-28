package com.example.customerchallenge.data.repository

import com.example.customerchallenge.data.remote.datasource.CustomerRemoteDataSource
import com.example.customerchallenge.data.remote.mapper.toDomain
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