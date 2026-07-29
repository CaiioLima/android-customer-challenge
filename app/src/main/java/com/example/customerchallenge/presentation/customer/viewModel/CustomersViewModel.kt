package com.example.customerchallenge.presentation.customer.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.customerchallenge.domain.usecase.GetCustomersUseCase
import com.example.customerchallenge.presentation.CustomersUIAction
import com.example.customerchallenge.presentation.CustomersUIState
import com.example.customerchallenge.presentation.customer.CustomersUISideEffect
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CustomersViewModel(
    private val getCustomersUseCase: GetCustomersUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CustomersUIState>(CustomersUIState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<CustomersUISideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    init {
        onAction(CustomersUIAction.LoadCustomers)
    }

    fun onAction(action: CustomersUIAction) {
        when (action) {
            is CustomersUIAction.CustomerClicked -> TODO()
            CustomersUIAction.LoadCustomers,
            CustomersUIAction.Retry -> TODO()
        }
    }

    private fun loadCustomers() = viewModelScope.launch {
        _uiState.update { CustomersUIState.Loading }
        getCustomersUseCase().onSuccess { customers ->
            _uiState.update { CustomersUIState.Success(customers) }
        }.onFailure { error ->
            Log.e(TAG,
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
    }


}

