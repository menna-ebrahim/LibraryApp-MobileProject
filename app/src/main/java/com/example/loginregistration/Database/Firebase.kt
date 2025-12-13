package com.example.loginregistration.Database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class Firebase : Auth {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun createUser(email: String, pass: String, onResult: (Boolean) -> Unit) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                onResult(task.isSuccessful)
            }
    }

    override fun saveUserData(userId: String, email: String, onResult: (Boolean) -> Unit) {
        val userData = hashMapOf("email" to email, "role" to "user")
        val currentUid = auth.currentUser?.uid ?: userId

        db.collection("users").document(currentUid).set(userData)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
    override fun loginUser(email: String, pass: String, onResult: (String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(auth.currentUser?.uid)
                } else {
                    onResult(null)
                }
            }
    }

    override fun getUserRole(userId: String, onResult: (String?) -> Unit) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                onResult(document.getString("role"))
            }
            .addOnFailureListener {
                onResult(null)
            }
    }
}