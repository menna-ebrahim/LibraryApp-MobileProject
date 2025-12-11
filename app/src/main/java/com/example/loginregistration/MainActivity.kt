package com.example.loginregistration

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.loginregistration.screens.HomeActivity
import com.example.loginregistration.screens.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        if (user != null) {
            // User is logged in -> Go to Home
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        } else {
            // User is NOT logged in -> Go to Login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Close MainActivity so user can't go back to it
        finish()
    }
}