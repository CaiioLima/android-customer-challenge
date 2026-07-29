package com.example.customerchallenge.domain.usecase

import com.example.customerchallenge.domain.model.Customer
import com.example.customerchallenge.domain.repository.CustomerRepository

class GetCustomersUseCase(
    private val repository: CustomerRepository
) {
    /**
     * Retrieves the available customers from the repository.
     *
     * @return A [Result] containing the customer list on success,
     * or the mapped failure when the operation cannot be completed.
     */
    suspend operator fun invoke(): Result<List<Customer>> {
        return repository.getCustomers()
    }
}