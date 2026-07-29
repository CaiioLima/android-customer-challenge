package com.example.customerchallenge.presentation.feature.profileview

sealed interface CustomerProfileUIState {

    data object Loading : CustomerProfileUIState

    data object Success : CustomerProfileUIState

    data object Error : CustomerProfileUIState
}