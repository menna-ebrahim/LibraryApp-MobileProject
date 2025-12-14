package com.example.loginregistration.AuthManger

import com.example.loginregistration.Database.Auth

class RegistrationManager(private val authRepository: Auth) {

    fun validateAndRegister(email: String, pass: String, confirm: String, onResult: (String) -> Unit) {

        if (email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            onResult("Please fill all fields")
            return
        }

        if (pass != confirm) {

            onResult("Password and Confirm mismatch")
            return

        }

        authRepository.createUser(email, pass) { success ->
            if (success) {

                authRepository.saveUserData("mockUserId", email) { saved ->

                    if (saved) onResult("Success") else onResult("Save failed")

                }

            } else {

                onResult("Registration Failed")

            }
        }
    }
}