package com.example.customerchallenge.presentation.feature.profileview

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CustomerProfileScreen(
        profileLink: String,
        onBackClick: () -> Unit
    ) {
        val context = LocalContext.current

        val webView = remember(profileLink) {
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = WebViewClient()
                loadUrl(profileLink)
            }
        }

        DisposableEffect(webView) {
            onDispose {
                webView.stopLoading()
                webView.loadUrl("about:blank")
                webView.destroy()
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "Customer profile")
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                factory = {
                    webView
                }
            )
        }
    }
