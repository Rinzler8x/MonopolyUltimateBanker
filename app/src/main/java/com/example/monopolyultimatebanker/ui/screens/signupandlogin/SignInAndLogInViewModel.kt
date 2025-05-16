package com.example.monopolyultimatebanker.ui.screens.signupandlogin

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monopolyultimatebanker.data.firebase.authentication.AuthResponse
import com.example.monopolyultimatebanker.data.firebase.authentication.FirebaseAuthRepositoryImpl
import com.example.monopolyultimatebanker.data.firebase.database.FirestoreRepositoryImpl
import com.example.monopolyultimatebanker.data.preferences.UserLoginPreferencesRepository
import com.example.monopolyultimatebanker.utils.SnackbarController
import com.example.monopolyultimatebanker.utils.SnackbarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UiState(
    val userName: String = "",
    val email: String = "",
    val password: String = "",
    val notEmpty: Boolean = false,
    val checked: Boolean = true,
)

@HiltViewModel
class SignUpAndLogInViewModel @Inject constructor(
    private val firebaseAuthRepositoryImpl: FirebaseAuthRepositoryImpl,
    private val firestoreRepositoryImpl: FirestoreRepositoryImpl,
    private val userLoginPreferencesRepository: UserLoginPreferencesRepository
): ViewModel() {

    var uiState by mutableStateOf(UiState())
        private set

    fun updateUsername(input: String) {
        uiState = uiState.copy(userName = input.trim())
    }

    fun updateEmail(input: String) {
        uiState = uiState.copy(email = input.trim())
    }

    fun updatePassword(input: String) {
        uiState = uiState.copy(password = input.trim())
    }

    fun isNotEmptyForSignUp(): Boolean {
        uiState = uiState.copy(notEmpty =
            uiState.let {
                it.userName.isNotBlank()
                        && it.email.isNotBlank()
                        && it.password.isNotBlank()
            }
        )
        return uiState.notEmpty
    }

    fun isNotEmptyForLogIn(): Boolean {
        uiState = uiState.copy(notEmpty =
        uiState.let {
            it.email.isNotBlank()
                    && it.password.isNotBlank()
        }
        )
        return uiState.notEmpty
    }

    fun onClickSignIn(
        navigateTo: () -> Unit
    ) {
        if(uiState.notEmpty){
            viewModelScope.launch {
                val response: AuthResponse = firebaseAuthRepositoryImpl.signInUser(uiState.email, uiState.password)
                if(response.result) {
                    firestoreRepositoryImpl.insertUsername(uiState.email, uiState.userName)
                    userLoginPreferencesRepository.saveUserLoginPreference(
                        isLoggedIn = true,
                        userName = uiState.userName,
                        email = uiState.email,
                    )
                    navigateTo()
                } else {
                    showSnackBar(response.errorMessage!!)
                }
            }
        }
    }

    fun onClickLogIn(
        navigateTo: () -> Unit
    ) {
        if(uiState.notEmpty){
            viewModelScope.launch {
                val response: AuthResponse = firebaseAuthRepositoryImpl.logInUser(uiState.email, uiState.password)
                if(response.result) {
                    val username = firestoreRepositoryImpl.getUsername(uiState.email)
                    Log.d(TAG, "VM MSG: $username")
                    uiState = uiState.copy(userName = username)
                    userLoginPreferencesRepository.saveUserLoginPreference(
                        isLoggedIn = true,
                        userName = uiState.userName,
                        email = uiState.email,
                    )
                    navigateTo()
                } else {
                    showSnackBar(response.errorMessage!!)
                }
            }
        }
    }

    fun onCheckedChange(value: Boolean) {
        uiState = uiState.copy(checked = value)
    }

    private fun showSnackBar(message: String) {
        viewModelScope.launch {
            SnackbarController.sendEvent(
                event = SnackbarEvent(
                    message = message
                )
            )
        }
    }
}