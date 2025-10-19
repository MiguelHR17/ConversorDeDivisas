package dev.miguelehr.conversordedivisas.data.repo

import com.google.firebase.auth.FirebaseAuth

class AuthRepo(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    val currentUser get() = auth.currentUser
    fun isLoggedIn() = auth.currentUser != null

    fun signIn(
        email: String,
        pass: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e) }
    }

    fun signOut() = auth.signOut()
}