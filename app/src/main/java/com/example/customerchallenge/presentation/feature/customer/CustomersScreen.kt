package com.example.customerchallenge.presentation.feature.customer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
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
    viewModel: CustomersViewModel = koinViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(viewModel) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                is CustomersUISideEffect.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = sideEffect.message
                    )
                }

                is CustomersUISideEffect.OpenCustomerProfile -> {
                    runCatching {
                        uriHandler.openUri(sideEffect.profileLink)
                    }.onFailure {
                        snackbarHostState.showSnackbar(
                            message = "Unable to open customer profile"
                        )
                    }
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

@Composable
fun CustomersScreen(
    uiState: CustomersUIState,
    snackbarHostState: SnackbarHostState,
    onAction: (CustomersUIAction) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
                        top = paddingValues.calculateTopPadding() + 16.dp,
                        end = 16.dp,
                        bottom = paddingValues.calculateBottomPadding() + 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.customers,
                        key = { customer -> customer.id }
                    ) { customer ->
                        CustomerItem(
                            customer = customer,
                            onClick = {
                                onAction(CustomersUIAction.CustomerClicked(customer.id))
                            }
                        )
                    }
                }
            }
        }
    }
}