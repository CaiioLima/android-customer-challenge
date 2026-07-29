package com.example.customerchallenge.presentation.customer

sealed interface CustomersUISideEffect {
    data class ShowError(val message: String) : CustomersUISideEffect
    data class OpenCustomerProfile(val profileLink: String) : CustomersUISideEffect
    data class OpenCustomerImage(val imageUrl: String) : CustomersUISideEffect
}