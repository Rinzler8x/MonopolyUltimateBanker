package com.example.monopolyultimatebanker.ui.screens.home

import android.content.ContentValues.TAG
import android.util.Log
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
import com.example.monopolyultimatebanker.data.preferences.GamePreferencesRepository
import com.example.monopolyultimatebanker.data.preferences.GamePrefState
import com.example.monopolyultimatebanker.data.preferences.UserLogin
import com.example.monopolyultimatebanker.data.preferences.UserLoginPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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
    val createGameDialog: Boolean = false,
    val joinGameDialog: Boolean = false,
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

    private val gameIdState = MutableStateFlow("")

    private fun setGameId(gameId: String) {
        gameIdState.value = gameId
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val firestoreGameState: StateFlow<FirestoreGameState> =
        gameIdState.flatMapLatest { gameId ->
            firestoreRepositoryImpl.getGame(gameId).map {
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

    fun onClickCreateGameDialog() {
        dialogState = dialogState.copy(createGameDialog = !dialogState.createGameDialog)
    }

    fun onClickJoinGameDialog() {
        dialogState = dialogState.copy(joinGameDialog = !dialogState.joinGameDialog)
    }

    fun newGame() {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val (count, id) = gameRepositoryImpl.gamePlayerExists(userLoginPreferenceState.value.userName)
                if(count == 0){
                    val playerId = firestoreRepositoryImpl.insertGamePlayer(dialogState.gameId, userLoginPreferenceState.value.userName)
                    gamePreferencesRepository.saveGamePreference(
                        gameId = dialogState.gameId,
                        playerId = playerId,
                        isGameActive = true
                    )
                    setGameId(dialogState.gameId)
                } else {
                    gamePreferencesRepository.saveGamePreference(
                        gameId = dialogState.gameId,
                        playerId = id!!,
                        isGameActive = true
                    )
                    setGameId(dialogState.gameId)
                    //TODO: Maybe display snackbar message, "you are already in match"
                }
            }
            updateGameId("")
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
}