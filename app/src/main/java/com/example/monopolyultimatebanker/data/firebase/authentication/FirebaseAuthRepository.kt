package com.example.monopolyultimatebanker.data.firebase.authentication

interface FirebaseAuthRepository {

    suspend fun signInUser(
        email: String,
        password: String,
    ) : AuthResponse

    suspend fun logInUser(
        email: String,
        password: String,
    ) : AuthResponse

    suspend fun logOutUser() : AuthResponse
}