package com.example.customerchallenge.domain.repository

import com.example.customerchallenge.domain.model.Customer

interface CustomerRepository {
    /**
     * Retrieves the available customers.
     *
     * @return A [Result] containing the customer list on success,
     * or a failure when the operation cannot be completed.
     */
    suspend fun getCustomers(): Result<List<Customer>>
}