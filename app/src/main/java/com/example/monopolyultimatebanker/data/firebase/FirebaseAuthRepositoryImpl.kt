package com.example.monopolyultimatebanker.data.firebase

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class AuthResponse(
    val errorMessage: String? = null,
    val result: Boolean
)

class FirebaseAuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : FirebaseAuthRepository {
    override suspend fun signInUser(email: String, password: String): AuthResponse {
        return withContext(Dispatchers.IO){
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                AuthResponse(result = true)
            } catch (e: FirebaseAuthEmailException) {
                AuthResponse("Email format invalid.", false)
            } catch (e: FirebaseAuthWeakPasswordException) {
                AuthResponse("Invalid Password. Min 6 characters", false)
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                AuthResponse("Invalid Credentials", false)
            } catch (e: FirebaseAuthException) {
                AuthResponse(e.message, false)
            }
            catch (e: Exception) {
                Log.e(TAG, "General Error", e)
                AuthResponse(errorMessage = "An error occurred. Please try again later.", result = false)
            }
        }
    }

    override suspend fun logInUser(email: String, password: String): AuthResponse {
        return withContext(Dispatchers.IO) {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                AuthResponse(result = true)
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                AuthResponse("Invalid Credentials", false)
            } catch (e: FirebaseAuthException) {
                AuthResponse(e.message, false)
            }
            catch (e: Exception) {
                Log.e(TAG, "General Error", e)
                AuthResponse(errorMessage = "An error occurred. Please try again later.", result = false)
            }
        }
    }

    override suspend fun logOutUser(): AuthResponse {
        return withContext(Dispatchers.IO) {
          try{
              if(auth.currentUser != null){
                  auth.signOut()
                  AuthResponse(result = true)
              }
              AuthResponse(result = false)
          } catch (e: Exception) {
              Log.w(TAG, "logOutUser:failure", e)
              AuthResponse(result = false)
          }
        }
    }
}