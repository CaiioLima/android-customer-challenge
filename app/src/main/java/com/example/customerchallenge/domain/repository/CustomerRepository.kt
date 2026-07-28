package com.example.customerchallenge.domain.repository

import com.example.customerchallenge.domain.model.Customer

interface CustomerRepository {

    suspend fun getCustomers(): Result<List<Customer>>
}