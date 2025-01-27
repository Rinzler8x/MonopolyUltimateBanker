package com.example.monopolyultimatebanker.data.firebase

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseAuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : FirebaseAuthRepository {
    override suspend fun signInUser(email: String, password: String): Boolean {
        return withContext(Dispatchers.IO){
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                true
            } catch (e: Exception) {
                Log.w(TAG, "createUserWithEmail:failure", e)
                false
            }
        }
    }

    override suspend fun logInUser(email: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            try{
                auth.signInWithEmailAndPassword(email, password).await()
                true
            } catch (e: Exception) {
                Log.w(TAG, "signInWithEmail:failure", e)
                false
            }
        }
    }

    override suspend fun logOutUser(): Boolean {
        return withContext(Dispatchers.IO) {
          try{
              if(auth.currentUser != null){
                  auth.signOut()
                  true
              }
              false
          } catch (e: Exception) {
              Log.w(TAG, "logOutUser:failure", e)
              false
          }
        }
    }
}