package com.example.monopolyultimatebanker.ui.screens.signupandlogin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monopolyultimatebanker.data.firebase.FirebaseAuthRepositoryImpl
import com.example.monopolyultimatebanker.data.preferences.UserLoginPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UiState(
    val userName: String = "",
    val email: String = "",
    val password: String = "",
    val notEmpty: Boolean = false
)

@HiltViewModel
class SignInAndLogInViewModel @Inject constructor(
    private val firebaseAuthRepositoryImpl: FirebaseAuthRepositoryImpl,
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

    fun isNotEmpty(): Boolean {
        uiState = uiState.copy(notEmpty =
            uiState.let {
                it.userName.isNotBlank()
                        && it.email.isNotBlank()
                        && it.password.isNotBlank()
            }
        )
        return uiState.notEmpty
    }

    fun onClickSignIn() {
        if(uiState.notEmpty){
            viewModelScope.launch {
                userLoginPreferencesRepository.saveUserLoginPreference(
                    isLoggedIn = true,
                    userName = uiState.userName,
                    email = uiState.email,
                    password = uiState.password
                )

                uiState = UiState()
                delay(1000L)

                uiState = userLoginPreferencesRepository.userLogin.first()
            }
        }
    }
}