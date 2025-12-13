package com.example.loginregistration.Database

interface Auth {
    fun createUser(email: String, pass: String, onResult: (Boolean) -> Unit)
    fun saveUserData(userId: String, email: String, onResult: (Boolean) -> Unit)
    fun loginUser(email: String, pass: String, onResult: (String?) -> Unit)
    fun getUserRole(userId: String, onResult: (String?) -> Unit)


}