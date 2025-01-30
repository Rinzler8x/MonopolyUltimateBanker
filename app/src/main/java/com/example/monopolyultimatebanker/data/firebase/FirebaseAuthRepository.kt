package com.example.monopolyultimatebanker.data.firebase

data class AuthResponse(
    val errorMessage: String? = null,
    val result: Boolean
)

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