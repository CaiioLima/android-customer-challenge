package com.example.customerchallenge.presentation

import com.example.customerchallenge.domain.model.Customer

sealed interface CustomersUIState {
    data object Loading : CustomersUIState
    data object Empty : CustomersUIState
    data class Success(
        val customers: List<Customer>,
        val filteredCustomers: List<Customer> = customers,
        val searchQuery: String = ""
    ) : CustomersUIState
    data class Error(val message: String) : CustomersUIState
}