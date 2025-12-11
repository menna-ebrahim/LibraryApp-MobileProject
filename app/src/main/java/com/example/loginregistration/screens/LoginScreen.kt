package com.example.loginregistration.screens

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginregistration.screens.RegisterActivity

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

import kotlin.jvm.java

@Composable

fun LoginScreen() {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val contextIntent = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Login", fontSize = 30.sp)

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(0.8f)        )

        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(0.8f)        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            enabled = !isLoading,
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    isLoading = true

                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        isLoading = false

                        if (task.isSuccessful) {

                            val userId = auth.currentUser!!.uid
                            val db = FirebaseFirestore.getInstance()
                            if (userId != null) {

                                db.collection("users").document(userId).get()
                                    .addOnSuccessListener { document ->
                                        if (document != null) {
                                            val role = document.getString("role")

                                            if (role == "Admin") {
                                                Toast.makeText(context, "YES Admin", Toast.LENGTH_SHORT).show()


                                                  val intent = Intent(context, HomeActivity::class.java)
                                                 context.startActivity(intent)

                                                // 2. PASS THE ADMIN FLAG
                                                intent.putExtra("IS_ADMIN", true)

                                                //new lines for admin panal
                                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                context.startActivity(intent)
                                                (context as? Activity)?.finish()

                                            } else {
                                                Toast.makeText(context, "Yes User", Toast.LENGTH_SHORT).show()

                                                 val intent = Intent(context, HomeActivity::class.java)
                                                 context.startActivity(intent)
                                            }
                                            //  (context as? Activity)?.finish()
                                        }
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(context, "Try again", Toast.LENGTH_SHORT).show()
                                    }
                            }
                            email = ""
                            password = ""

                        } else {
                            try {
                                throw task.exception!!
                            } catch (e: FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(
                                    context,
                                    "Account is not exist",
                                    Toast.LENGTH_SHORT
                                ).show()


                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            Text(text = if (isLoading) "Loading..." else "login")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = {
            val intent = Intent(contextIntent, RegisterActivity::class.java)

            contextIntent.startActivity(intent)
        }) {
            Text("Sign up")
        }
    }

}