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
import com.example.loginregistration.Database.Firebase
import com.example.loginregistration.AuthManger.RegistrationManager
import com.example.loginregistration.screens.LoginActivity
import kotlinx.coroutines.delay

@Composable
fun RegisterScreen() {

    val context = LocalContext.current
    val contextIntent = LocalContext.current

    val registrationManager = remember {
        RegistrationManager(Firebase())
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
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
            onValueChange = { email = it; showError = false },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = password,
            onValueChange = { password = it; showError = false },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = confirm,
            onValueChange = { confirm = it; showError = false },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            enabled = !isLoading,
            onClick = {
                isLoading = true

                registrationManager.validateAndRegister(email, password, confirm) { resultMessage ->
                    isLoading = false

                    if (resultMessage == "Success") {

                        Toast.makeText(context, "Account Created Successfully", Toast.LENGTH_SHORT).show()

                        email = ""
                        password = ""
                        confirm = ""

                    } else {

                        showError = true
                        errorMessage = resultMessage
                        Toast.makeText(context, resultMessage, Toast.LENGTH_SHORT).show()

                    }
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
                    text = errorMessage.ifEmpty { "Registration Failed" },
                    color = Color.Red,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            LaunchedEffect(Unit) {

                delay(5000)
                showError = false

            }
        }
    }
}