package com.example.customerchallenge.presentation.feature.customer.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.customerchallenge.R

@Composable
fun CustomersLoadingState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun CustomersEmptyState(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    CustomersMessageState(
        modifier = modifier,
        title = stringResource(id = R.string.customers_empty_title),
        message = stringResource(id = R.string.customers_empty_message),
        onRetry = onRetry
    )
}

@Composable
fun CustomersErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    CustomersMessageState(
        modifier = modifier,
        title = stringResource(id = R.string.customers_error_title),
        message = message,
        onRetry = onRetry
    )
}

@Composable
private fun CustomersMessageState(
    title: String,
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Button(
            onClick = onRetry
        ) {
            Text(text = stringResource(id = R.string.txt_btn_try_again))
        }
    }
}