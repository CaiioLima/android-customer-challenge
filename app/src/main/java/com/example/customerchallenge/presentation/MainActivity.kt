package com.example.customerchallenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.customerchallenge.presentation.navigation.AppNavHost
import com.example.customerchallenge.ui.theme.CustomerChallengeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CustomerChallengeTheme {
                AppNavHost()
            }
        }
    }
}