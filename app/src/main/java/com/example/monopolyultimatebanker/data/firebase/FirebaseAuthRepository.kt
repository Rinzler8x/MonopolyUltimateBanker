package com.example.monopolyultimatebanker.data.firebase

interface FirebaseAuthRepository {

    suspend fun signInUser(
        email: String,
        password: String,
    ) : Boolean

    suspend fun logInUser(
        email: String,
        password: String,
    ) : Boolean

    suspend fun logOutUser() : Boolean
}