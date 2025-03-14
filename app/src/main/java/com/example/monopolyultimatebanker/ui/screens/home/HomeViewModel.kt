package com.example.monopolyultimatebanker.ui.screens.home

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monopolyultimatebanker.data.firebase.database.FirestoreGame
import com.example.monopolyultimatebanker.data.firebase.database.FirestoreRepositoryImpl
import com.example.monopolyultimatebanker.data.gametable.Game
import com.example.monopolyultimatebanker.data.gametable.GameRepositoryImpl
import com.example.monopolyultimatebanker.data.preferences.GamePrefState
import com.example.monopolyultimatebanker.data.preferences.GamePreferencesRepository
import com.example.monopolyultimatebanker.data.preferences.UserLogin
import com.example.monopolyultimatebanker.data.preferences.UserLoginPreferencesRepository
import com.example.monopolyultimatebanker.utils.SnackbarController
import com.example.monopolyultimatebanker.utils.SnackbarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

data class DialogState(
    val createOrJoinGameDialog: Boolean = false,
    val leaveGameDialog: Boolean = false,
    val gameId: String = ""
)

data class FirestoreGameState(
    val firestoreGame: List<FirestoreGame> = listOf()
)

data class GameState(
    val gameState: List<Game> = listOf()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firestoreRepositoryImpl: FirestoreRepositoryImpl,
    private val gameRepositoryImpl: GameRepositoryImpl,
    private val gamePreferencesRepository: GamePreferencesRepository,
    private val userLoginPreferencesRepository: UserLoginPreferencesRepository
): ViewModel() {

    val gamePreferenceState: StateFlow<GamePrefState> =
        gamePreferencesRepository.gameState.map {
            GamePrefState(
                gameId = it.gameId,
                playerId = it.playerId,
                isGameActive = it.isGameActive
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = GamePrefState()
            )

    val userLoginPreferenceState: StateFlow<UserLogin> =
        userLoginPreferencesRepository.userLogin.map {
            UserLogin(
                it.isLoggedIn,
                it.userName,
                it.email
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = UserLogin()
            )

    @OptIn(ExperimentalCoroutinesApi::class)
    val firestoreGameState: StateFlow<FirestoreGameState> =
        gamePreferenceState.flatMapLatest { gameId ->
            firestoreRepositoryImpl.getGame(gameId.gameId).map {
                FirestoreGameState(it)
            }
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = FirestoreGameState()
            )

    val gameState: StateFlow<GameState> =
        gameRepositoryImpl.getGameStream().map {
            GameState(it)
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = GameState()
            )

    /**Dialog Boxes Code*/
    var dialogState by mutableStateOf(DialogState())
        private set

    fun updateGameId(input: String) {
        dialogState = dialogState.copy(gameId = input.trim())
    }

    fun onClickCreateOrJoinGameDialog() {
        dialogState = dialogState.copy(createOrJoinGameDialog = !dialogState.createOrJoinGameDialog)
    }

    fun onClickLeaveGameDialog(){
        dialogState = dialogState.copy(leaveGameDialog = !dialogState.leaveGameDialog)
    }

    fun newGame() {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val count = firestoreRepositoryImpl.countGamePlayers(dialogState.gameId)
                if(count < 4) {
                    val playerId = firestoreRepositoryImpl.insertGamePlayer(dialogState.gameId, userLoginPreferenceState.value.userName)
                    gamePreferencesRepository.saveGamePreference(
                        gameId = dialogState.gameId,
                        playerId = playerId,
                        isGameActive = true
                    )
                } else {
                    gameFullSnackbar()
                }
            }
            updateGameId("")
        }
    }

    /**Live Game Code*/
    fun leaveGame() {
        viewModelScope.launch {
            firestoreRepositoryImpl.updateGamePlayer(
                playerId = gamePreferenceState.value.playerId,
                playerBalance = -99999
            )
            firestoreRepositoryImpl.deleteGame(gamePreferenceState.value.playerId)
            gameRepositoryImpl.deleteGame()
            gamePreferencesRepository.resetGamePreference()
        }
    }

    /**Navigation Drawer Code*/
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

    /**Snackbar Code*/

    fun gameFullSnackbar() {
        showSnackBar("Game is full.")
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