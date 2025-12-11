package com.example.loginregistration.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginregistration.screens.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay

@Composable
fun RegisterScreen() {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val contextIntent = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Create Account", fontSize = 30.sp)

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = email,
            onValueChange = {
                email = it
                showError = false
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = password,
            onValueChange = {
                password = it
                showError = false
            },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            enabled = !isLoading,
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    isLoading = true

                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            isLoading = false

                            if (task.isSuccessful) {

                                val userId = auth.currentUser?.uid
                                val db = Firebase.firestore

                                val userData = hashMapOf(
                                    "email" to email,
                                    "role" to "user"
                                )
                                email = ""
                                password = ""
                                if (userId != null) {
                                    db.collection("users").document(userId).set(userData)
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                context,
                                                "Account Created ",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(
                                                context,
                                                "Saved failed: ${it.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }


                            } else {
                                showError = true
                            }
                        }
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            Text(text = if (isLoading) "Loading..." else "Register")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = {
            val intent = Intent(contextIntent, LoginActivity::class.java)

            contextIntent.startActivity(intent)
        }) {
            Text("Back to Login")
        }
        Spacer(modifier = Modifier.height(10.dp))

        if (showError) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Red.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp))
                    .border(1.dp, Color.Red, RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "Format Email is not correct\nor Password must be at least 6 chars\nor Try with other email",
                    color = Color.Red,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LaunchedEffect(Unit) {
                delay(30000)
                showError = false
            }
        }
    }
}