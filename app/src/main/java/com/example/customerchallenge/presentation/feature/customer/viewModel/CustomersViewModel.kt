package com.example.customerchallenge.presentation.feature.customer.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.customerchallenge.domain.usecase.GetCustomersUseCase
import com.example.customerchallenge.presentation.CustomersUIAction
import com.example.customerchallenge.presentation.CustomersUIState
import com.example.customerchallenge.presentation.customer.CustomersUISideEffect
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CustomersViewModel(
    private val getCustomersUseCase: GetCustomersUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CustomersUIState>(CustomersUIState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<CustomersUISideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    private val searchQuery = MutableStateFlow("")

    init {
        onAction(CustomersUIAction.LoadCustomers)
        observeSearchQuery()
    }

    fun onAction(action: CustomersUIAction) {
        when (action) {
            CustomersUIAction.LoadCustomers,
            CustomersUIAction.Retry -> loadCustomers()
            is CustomersUIAction.OpenImageClicked -> openCustomerImage(imageUrl = action.imageUrl)
            is CustomersUIAction.OpenProfileClicked -> openCustomerProfile(profileLink = action.profileLink)
            is CustomersUIAction.SearchQueryChanged -> { updateSearchQuery(query = action.query) }
        }
    }

    private fun openCustomerProfile(profileLink: String) {
        if (profileLink.isBlank()) return
        viewModelScope.launch {
            _sideEffect.emit(
                CustomersUISideEffect.OpenCustomerProfile(
                    profileLink = profileLink
                )
            )
        }
    }

    private fun updateSearchQuery(query: String) {
        searchQuery.value = query

        _uiState.update { currentState ->
            if (currentState is CustomersUIState.Success) {
                currentState.copy(
                    searchQuery = query
                )
            } else {
                currentState
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        viewModelScope.launch {
            searchQuery
                .debounce(SEARCH_DEBOUNCE_MILLIS)
                .distinctUntilChanged()
                .collect { query ->
                    filterCustomers(query = query)
                }
        }
    }

    private fun filterCustomers(query: String) {
        _uiState.update { currentState ->
            if (currentState !is CustomersUIState.Success) {
                return@update currentState
            }
            val normalizedQuery = query.trim()
            val filteredCustomers = if (normalizedQuery.isBlank()) {
                currentState.customers
            } else {
                currentState.customers.filter { customer ->
                    customer.name.contains(
                        other = normalizedQuery,
                        ignoreCase = true
                    ) ||
                            customer.email.contains(
                                other = normalizedQuery,
                                ignoreCase = true
                            ) ||
                            customer.phone.orEmpty().contains(
                                other = normalizedQuery,
                                ignoreCase = true
                            ) ||
                            customer.id.contains(
                                other = normalizedQuery,
                                ignoreCase = true
                            )
                }
            }

            currentState.copy(
                filteredCustomers = filteredCustomers
            )
        }
    }

    private fun openCustomerImage(imageUrl: String) {
        viewModelScope.launch {
            _sideEffect.emit(CustomersUISideEffect.OpenCustomerImage(imageUrl))
        }
    }

    private fun loadCustomers() = viewModelScope.launch {
        searchQuery.value = ""
        _uiState.update { CustomersUIState.Loading }
        getCustomersUseCase().onSuccess { customers ->
            _uiState.update {
                if (customers.isEmpty()) {
                    CustomersUIState.Empty
                } else {
                    CustomersUIState.Success(
                        customers = customers,
                        filteredCustomers = customers,
                        searchQuery = ""
                    )
                }
            }

        }.onFailure { error ->
            Log.e(
                TAG,
                "Failed to load customers",
                error
            )

            val message = error.message ?: DEFAULT_ERROR_MESSAGE
            _sideEffect.emit(CustomersUISideEffect.ShowError(message))
            _uiState.update { CustomersUIState.Error(message) }
        }
    }

    private companion object {
        const val TAG = "CustomersViewModel"
        const val DEFAULT_ERROR_MESSAGE = "Unable to load customers"
        const val SEARCH_DEBOUNCE_MILLIS = 300L
    }

}

