package com.example.customerchallenge.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface AppRoute {

    @Serializable
    data object Customers : AppRoute
    @Serializable
    data class CustomerProfile(
        val profileLink: String
    ) : AppRoute

    @Serializable
    data class CustomerImage(
        val imageUrl: String
    ) : AppRoute
}