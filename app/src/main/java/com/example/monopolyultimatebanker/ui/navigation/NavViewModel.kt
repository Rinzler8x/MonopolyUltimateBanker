package com.example.monopolyultimatebanker.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monopolyultimatebanker.data.preferences.UserLoginPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class NavUiState(
    val isLoggedIn: Boolean? = null
)

@HiltViewModel
class NavViewModel @Inject constructor(
    private val userLoginPreferencesRepository: UserLoginPreferencesRepository
): ViewModel() {

    val navUiState: StateFlow<NavUiState> =
        userLoginPreferencesRepository.userLogin.map { userLogin ->
            NavUiState(isLoggedIn = userLogin.isLoggedIn)
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = NavUiState()
            )
}