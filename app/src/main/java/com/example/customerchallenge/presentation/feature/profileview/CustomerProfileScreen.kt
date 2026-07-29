package com.example.customerchallenge.presentation.feature.profileview

import android.graphics.Bitmap
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.customerchallenge.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerProfileScreen(
    profileLink: String,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    var uiState by remember(profileLink) {
        mutableStateOf<CustomerProfileUIState>(
            CustomerProfileUIState.Loading
        )
    }

    val webView = remember(profileLink) {
        WebView(context).apply {
            settings.javaScriptEnabled = true

            webViewClient = object : WebViewClient() {

                private var hasMainFrameError = false

                override fun onPageStarted(
                    view: WebView?,
                    url: String?,
                    favicon: Bitmap?
                ) {
                    hasMainFrameError = false
                    uiState = CustomerProfileUIState.Loading
                }

                override fun onPageFinished(
                    view: WebView?,
                    url: String?
                ) {
                    if (!hasMainFrameError) {
                        uiState = CustomerProfileUIState.Success
                    }
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    if (request?.isForMainFrame == true) {
                        handleMainFrameError(view)
                    }
                }

                override fun onReceivedHttpError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    errorResponse: WebResourceResponse?
                ) {
                    if (request?.isForMainFrame == true) {
                        handleMainFrameError(view)
                    }
                }

                private fun handleMainFrameError(
                    view: WebView?
                ) {
                    hasMainFrameError = true
                    view?.stopLoading()
                    uiState = CustomerProfileUIState.Error
                }
            }

            loadUrl(profileLink)
        }
    }

    DisposableEffect(webView) {
        onDispose {
            webView.stopLoading()
            webView.loadUrl("about:blank")
            webView.removeAllViews()
            webView.destroy()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(
                            id = R.string.customers_profile_title
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(
                                id = R.string.back
                            )
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    webView
                }
            )

            when (uiState) {
                CustomerProfileUIState.Loading -> {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                CustomerProfileUIState.Error -> {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        CustomerProfileErrorState(
                            modifier = Modifier.fillMaxSize(),
                            onRetry = {
                                webView.stopLoading()
                                uiState = CustomerProfileUIState.Loading
                                webView.loadUrl(profileLink)
                            }
                        )
                    }
                }

                CustomerProfileUIState.Success -> Unit
            }
        }
    }
}

@Composable
private fun CustomerProfileErrorState(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.customer_profile_load_error),
            textAlign = TextAlign.Center
        )

        Button(
            modifier = Modifier.padding(top = 16.dp),
            onClick = onRetry
        ) {
            Text(
                text = stringResource(id = R.string.retry)
            )
        }
    }
}
