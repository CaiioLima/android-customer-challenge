package com.example.customerchallenge.presentation.feature.customer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.customerchallenge.presentation.CustomersUIAction
import com.example.customerchallenge.presentation.CustomersUIState
import com.example.customerchallenge.presentation.customer.CustomersUISideEffect
import com.example.customerchallenge.presentation.feature.customer.components.CustomerItem
import com.example.customerchallenge.presentation.feature.customer.components.CustomersEmptyState
import com.example.customerchallenge.presentation.feature.customer.components.CustomersErrorState
import com.example.customerchallenge.presentation.feature.customer.components.CustomersLoadingState
import com.example.customerchallenge.presentation.feature.customer.viewModel.CustomersViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CustomersScreen(
    viewModel: CustomersViewModel = koinViewModel(),
    onOpenProfile: (profileLink: String) -> Unit,
    onOpenImage: (imageUrl: String) -> Unit

) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                is CustomersUISideEffect.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = sideEffect.message
                    )
                }

                is CustomersUISideEffect.OpenCustomerProfile -> {
                    onOpenProfile(sideEffect.profileLink)
                }

                is CustomersUISideEffect.OpenCustomerImage -> {
                    onOpenImage(sideEffect.imageUrl)
                }
            }
        }
    }

    CustomersScreen(
        uiState = uiState.value,
        snackbarHostState = snackbarHostState,
        onAction = viewModel::onAction
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomersScreen(
    uiState: CustomersUIState,
    snackbarHostState: SnackbarHostState,
    onAction: (CustomersUIAction) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Customers List",
                        style = typography.titleLarge
                    )
                }
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        }
    ) { paddingValues ->
        when (uiState) {
            CustomersUIState.Loading -> {
                CustomersLoadingState(
                    modifier = Modifier.fillMaxSize()
                )
            }

            CustomersUIState.Empty -> {
                CustomersEmptyState(
                    modifier = Modifier.fillMaxSize(),
                    onRetry = {
                        onAction(CustomersUIAction.Retry)
                    }
                )
            }

            is CustomersUIState.Error -> {
                CustomersErrorState(
                    modifier = Modifier.fillMaxSize(),
                    message = uiState.message,
                    onRetry = {
                        onAction(CustomersUIAction.Retry)
                    }
                )
            }

            is CustomersUIState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        top = paddingValues.calculateTopPadding(),
                        end = 16.dp,
                        bottom = paddingValues.calculateBottomPadding()
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.customers,
                        key = { customer -> customer.id }
                    ) { customer ->
                        CustomerItem(
                            customer = customer,
                            onImageClick = {
                                onAction(CustomersUIAction.OpenImageClicked(customer.profileImage.orEmpty()))
                            },
                            onProfileClick = {
                                onAction(CustomersUIAction.OpenProfileClicked(customer.profileLink.orEmpty()))
                            })
                    }
                }
            }
        }
    }
}