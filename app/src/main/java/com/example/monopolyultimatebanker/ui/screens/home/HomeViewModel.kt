package com.example.monopolyultimatebanker.ui.screens.home

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monopolyultimatebanker.data.firebase.database.FirestoreGame
import com.example.monopolyultimatebanker.data.firebase.database.FirestoreRepositoryImpl
import com.example.monopolyultimatebanker.data.gametable.Game
import com.example.monopolyultimatebanker.data.gametable.GameRepositoryImpl
import com.example.monopolyultimatebanker.data.propertytable.Property
import com.example.monopolyultimatebanker.data.propertytable.PropertyRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

data class PropertyState(
    val propertyList: List<Property> = listOf()
)

data class FirestoreState(
    val gameList: List<FirestoreGame> = listOf()
)

data class DialogState(
    val createGameDialog: Boolean = false,
    val joinGameDialog: Boolean = false,
    val gameId: String = ""
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firestoreRepositoryImpl: FirestoreRepositoryImpl,
    private val propertyRepositoryImpl: PropertyRepositoryImpl,
    private val gameRepositoryImpl: GameRepositoryImpl
): ViewModel() {

    var dialogState by mutableStateOf(DialogState())
        private set

    fun updateGameId(input: String) {
        dialogState = dialogState.copy(gameId = input.trim())
    }

    fun onClickCreateGameDialog() {
        dialogState = dialogState.copy(createGameDialog = !dialogState.createGameDialog)
    }

    fun onClickJoinGameDialog() {
        dialogState = dialogState.copy(joinGameDialog = !dialogState.joinGameDialog)
    }

    var navDrawerState by mutableStateOf(DrawerState(initialValue = DrawerValue.Closed))
        private set

    fun onClickNavIcon(compositionCoroutineContext: CoroutineContext) {
        viewModelScope.launch {
            withContext(compositionCoroutineContext) {
                navDrawerState.apply {
                    if (isClosed) open() else close()
                }
            }
        }
    }

    private fun insert(
        id: String,
        name: String,
        balance: Int
    ) {
        Log.d(TAG, "$id, $name, $balance")
    }
}