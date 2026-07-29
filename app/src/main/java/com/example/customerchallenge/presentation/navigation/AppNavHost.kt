package com.example.customerchallenge.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.customerchallenge.presentation.feature.customer.CustomersScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoute.Customers,
        modifier = modifier
    ) {
        composable<AppRoute.Customers> {
            CustomersScreen()
        }
    }
}