package com.example.loginregistration.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            RegisterScreen()
        }

    }
}