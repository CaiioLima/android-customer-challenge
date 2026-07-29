package com.example.customerchallenge.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface AppRoute {

    @Serializable
    data object Customers : AppRoute
}