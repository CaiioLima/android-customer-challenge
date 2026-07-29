package com.example.customerchallenge.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.customerchallenge.presentation.feature.customer.CustomersScreen
import com.example.customerchallenge.presentation.feature.customer.image.CustomerImageScreen
import com.example.customerchallenge.presentation.feature.customer.profile.CustomerProfileScreen

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
            CustomersScreen(
                onOpenImage = { imageUrl ->
                    navController.navigate(
                        AppRoute.CustomerImage(
                            imageUrl = imageUrl
                        )
                    )

                },
                onOpenProfile = {
                    navController.navigate(
                        AppRoute.CustomerProfile(
                            profileLink = it
                        )
                    )
                }
            )
        }
        composable<AppRoute.CustomerProfile> { backStackEntry ->
            val route = backStackEntry.toRoute<AppRoute.CustomerProfile>()

            CustomerProfileScreen(
                profileLink = route.profileLink,
                onBackClick = navController::navigateUp
            )
        }

        composable<AppRoute.CustomerImage> { backStackEntry ->
            val route = backStackEntry.toRoute<AppRoute.CustomerImage>()

            CustomerImageScreen(
                imageUrl = route.imageUrl,
                onBackClick = navController::navigateUp
            )
        }
    }
}