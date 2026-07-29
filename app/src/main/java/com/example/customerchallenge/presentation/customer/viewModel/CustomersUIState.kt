package com.example.customerchallenge.presentation

import com.example.customerchallenge.domain.model.Customer

sealed interface CustomersUIState {
    data object Loading : CustomersUIState
    data class Success(val customers: List<Customer>) : CustomersUIState
    data class Error(val message: String) : CustomersUIState
}