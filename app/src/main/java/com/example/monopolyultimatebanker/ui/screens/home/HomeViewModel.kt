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
import com.example.monopolyultimatebanker.data.preferences.GamePreferencesRepository
import com.example.monopolyultimatebanker.data.preferences.GameState
import com.example.monopolyultimatebanker.data.preferences.UserLogin
import com.example.monopolyultimatebanker.data.preferences.UserLoginPreferencesRepository
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

data class DialogState(
    val createGameDialog: Boolean = false,
    val joinGameDialog: Boolean = false,
    val gameId: String = ""
)

data class FirestoreGameState(
    val game: List<FirestoreGame> = listOf()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firestoreRepositoryImpl: FirestoreRepositoryImpl,
    private val gameRepositoryImpl: GameRepositoryImpl,
    private val gamePreferencesRepository: GamePreferencesRepository,
    private val userLoginPreferencesRepository: UserLoginPreferencesRepository
): ViewModel() {


    val gamePreferenceState: StateFlow<GameState> =
        gamePreferencesRepository.gameState.map {
            GameState(
                gameId = it.gameId,
                playerId = it.playerId,
                isGameActive = it.isGameActive
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = GameState()
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

//    val gamePrefTest: String
//        get() = getValue()
//
//    private fun getValue(): String {
//        var data: String = ""
//        viewModelScope.launch {
//            data = gamePreferencesRepository.gameState.first().gameId
//        }
//        return data
//    }


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

    fun createNewGame() {
        viewModelScope.launch {
            if(false){
                //TODO: To check in firebase if player is already in a game, display error
            }
            val playerId = firestoreRepositoryImpl.insertGamePlayer(dialogState.gameId, userLoginPreferenceState.value.userName)
            withContext(Dispatchers.IO){
                if(gameRepositoryImpl.gamePlayerExists(userLoginPreferenceState.value.userName) == 0){
                    gameRepositoryImpl.gameInsert(Game(playerId, userLoginPreferenceState.value.userName, 1500))
                    gamePreferencesRepository.saveGamePreference(
                        gameId = dialogState.gameId,
                        playerId = playerId,
                        isGameActive = true
                    )
                } else {
                    gamePreferencesRepository.saveGamePreference(
                        gameId = dialogState.gameId,
                        playerId = playerId,
                        isGameActive = true
                    )
                    //TODO: Maybe display snackbar message, "you are already in match"
                }
            }
            updateGameId("")
        }
    }

    fun joinNewGame() {
        viewModelScope.launch {
            val playerId = firestoreRepositoryImpl.insertGamePlayer(dialogState.gameId, userLoginPreferenceState.value.userName)
            withContext(Dispatchers.IO){
                if(gameRepositoryImpl.gamePlayerExists(userLoginPreferenceState.value.userName) == 0) {
                    gameRepositoryImpl.gameInsert(Game(playerId, userLoginPreferenceState.value.userName, 1500))
                    gamePreferencesRepository.saveGamePreference(
                        gameId = dialogState.gameId,
                        playerId = playerId,
                        isGameActive = true
                    )
                } else {
                    gamePreferencesRepository.saveGamePreference(
                        gameId = dialogState.gameId,
                        playerId = playerId,
                        isGameActive = true
                    )
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