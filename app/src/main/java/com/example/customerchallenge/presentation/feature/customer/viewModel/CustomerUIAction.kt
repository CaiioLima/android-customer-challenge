package com.example.customerchallenge.presentation

sealed interface CustomersUIAction {
    data object LoadCustomers : CustomersUIAction
    data object Retry : CustomersUIAction
    data class CustomerClicked(val customerId: String) : CustomersUIAction
}