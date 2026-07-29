package com.example.customerchallenge.presentation

sealed interface CustomersUIAction {
    data object LoadCustomers : CustomersUIAction
    data object Retry : CustomersUIAction
    data class OpenProfileClicked(val profileLink: String) : CustomersUIAction
    data class OpenImageClicked(val imageUrl: String) : CustomersUIAction
}