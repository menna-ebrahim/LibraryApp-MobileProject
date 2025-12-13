package com.example.loginregistration.AuthManger


import com.example.loginregistration.Database.Auth



class LoginManager(private val authRepository: Auth) {
    fun validateAndLogin(email: String, pass: String, onResult: (String) -> Unit) {

        if (email.isEmpty() || pass.isEmpty()) {
            onResult("Please fill all fields")
            return
        }

        authRepository.loginUser(email, pass) { userId ->
            if (userId != null) {

                authRepository.getUserRole(userId) { role ->
                    if (role == "Admin") {

                        onResult("AdminSuccess")

                    } else {

                        onResult("UserSuccess")

                    }

                }

            } else {

                onResult("Login Failed")

            }
        }
    }
}