package com.example.monopolyultimatebanker.ui.screens.collect200

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monopolyultimatebanker.data.firebase.database.FirestoreGameLogicImpl
import com.example.monopolyultimatebanker.data.preferences.GamePreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ResultDialogState(
    val resultDialogState: Boolean = false
)

@HiltViewModel
class Collect200ViewModel @Inject constructor(
    private val firestoreGameLogicImpl: FirestoreGameLogicImpl,
    private val gamePreferencesRepository: GamePreferencesRepository,
): ViewModel() {

    private val _uiResultDialog = MutableStateFlow(ResultDialogState())
    val uiResultDialog: StateFlow<ResultDialogState> = _uiResultDialog.asStateFlow()

    fun onClickResultDialog() {
        _uiResultDialog.update { currentState ->
            currentState.copy(
                resultDialogState = !_uiResultDialog.value.resultDialogState
            )
        }
    }

    fun onClickCollect() {
        viewModelScope.launch {
            firestoreGameLogicImpl.collect200(
                playerId = gamePreferencesRepository.gameState.first().playerId
            )
        }
    }
}
