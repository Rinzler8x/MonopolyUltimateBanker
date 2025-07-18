package com.example.monopolyultimatebanker.ui.screens.home

import android.app.Activity
import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monopolyultimatebanker.data.firebase.authentication.FirebaseAuthRepositoryImpl
import com.example.monopolyultimatebanker.data.firebase.database.FirestoreGame
import com.example.monopolyultimatebanker.data.firebase.database.FirestoreGameLogicImpl
import com.example.monopolyultimatebanker.data.firebase.database.FirestorePlayerProperty
import com.example.monopolyultimatebanker.data.firebase.database.FirestoreRepositoryImpl
import com.example.monopolyultimatebanker.data.gametable.Game
import com.example.monopolyultimatebanker.data.gametable.GameRepositoryImpl
import com.example.monopolyultimatebanker.data.playerpropertytable.PlayerPropertiesList
import com.example.monopolyultimatebanker.data.playerpropertytable.PlayerPropertyRepositoryImpl
import com.example.monopolyultimatebanker.data.preferences.GamePreferencesRepository
import com.example.monopolyultimatebanker.data.preferences.UserLogin
import com.example.monopolyultimatebanker.data.preferences.UserLoginPreferencesRepository
import com.example.monopolyultimatebanker.utils.SnackbarController
import com.example.monopolyultimatebanker.utils.SnackbarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

data class NewGameDialogState(
    val createOrJoinGameDialog: Boolean = false,
    val gameId: String = ""
)

data class MultiPurposeDialogState(
    val leaveGameDialog: Boolean = false,
    val logoutDialog: Boolean = false,
    val navigateToNewLocationDialog: Boolean = false,
    val playerPropertiesListDialog: Boolean = false,
    val gameOverDialog: Boolean = false,
    val isLoading: Boolean = false,
)

data class FirestoreGameState(
    val firestoreGame: List<FirestoreGame> = listOf()
)

data class FirestorePlayerPropertyState(
    val playerProperties: List<FirestorePlayerProperty> = listOf()
)

data class GameState(
    val gameState: List<Game> = listOf()
)

data class PlayerPropertiesListState(
    val playerPropertiesListState: List<PlayerPropertiesList>? = listOf()
)

data class GamePrefUiState(
    val gameId: String = "",
    val playerId: String = "",
    val isGameActive: Boolean? = null,
    val gameOverCount: Int = 0,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firestoreRepositoryImpl: FirestoreRepositoryImpl,
    private val firestoreGameLogicImpl: FirestoreGameLogicImpl,
    private val firebaseAuthRepositoryImpl: FirebaseAuthRepositoryImpl,
    private val gameRepositoryImpl: GameRepositoryImpl,
    private val playerPropertyRepositoryImpl: PlayerPropertyRepositoryImpl,
    private val gamePreferencesRepository: GamePreferencesRepository,
    private val userLoginPreferencesRepository: UserLoginPreferencesRepository
): ViewModel() {

    val gamePreferenceState: StateFlow<GamePrefUiState> =
        gamePreferencesRepository.gameState.map {
            GamePrefUiState(
                gameId = it.gameId,
                playerId = it.playerId,
                isGameActive = it.isGameActive,
                gameOverCount = it.gameOverCount
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = GamePrefUiState()
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

    @OptIn(ExperimentalCoroutinesApi::class)
    val firestorePlayerPropertyState: StateFlow<FirestorePlayerPropertyState> =
        gamePreferenceState.flatMapLatest { prefState ->
            firestoreRepositoryImpl.getPlayerProperty(prefState.gameId).map { firestorePPState ->
                FirestorePlayerPropertyState(firestorePPState)
            }
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = FirestorePlayerPropertyState()
            )

    @OptIn(ExperimentalCoroutinesApi::class)
    val playerPropertiesListState: StateFlow<PlayerPropertiesListState> =
        gamePreferenceState.flatMapLatest { prefState ->
            playerPropertyRepositoryImpl.getPlayerPropertiesList(prefState.playerId).map { ppList ->
                PlayerPropertiesListState(ppList ?: emptyList())
            }
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = PlayerPropertiesListState()
            )

    /**Dialog Boxes Code*/
    var newGameDialogState by mutableStateOf(NewGameDialogState())
        private set

    fun updateGameId(input: String) {
        newGameDialogState = newGameDialogState.copy(gameId = input.trim().lowercase())
    }

    fun onClickCreateOrJoinGameDialog() {
        newGameDialogState = newGameDialogState.copy(createOrJoinGameDialog = !newGameDialogState.createOrJoinGameDialog)
    }

    fun newGame() {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val count = firestoreRepositoryImpl.countGamePlayers(newGameDialogState.gameId)
                if(count < 4) {
                    val playerId = firestoreRepositoryImpl.insertGamePlayer(newGameDialogState.gameId, userLoginPreferenceState.value.userName)
                    gamePreferencesRepository.saveGamePreference(
                        gameId = newGameDialogState.gameId,
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

    fun gameOverComputeTotalPlayerBalance() {
        viewModelScope.launch {
            firestoreGameLogicImpl.computeTotalPlayerBalance(playerId = gamePreferenceState.value.playerId)
        }
    }

    fun gameOverCountUpdate() {
        viewModelScope.launch {
            gamePreferencesRepository.gameOverCountUpdate()
        }
    }

    /**Multipurpose Dialog Box*/
    private val _uiMultiPurposeDialog = MutableStateFlow(MultiPurposeDialogState())
    val uiMultiPurposeDialog: StateFlow<MultiPurposeDialogState> = _uiMultiPurposeDialog.asStateFlow()

    fun onClickLeaveGameDialog() {
        _uiMultiPurposeDialog.update { currentState ->
            currentState.copy(
                leaveGameDialog = !_uiMultiPurposeDialog.value.leaveGameDialog
            )
        }
    }

    fun onClickLogOutDialog() {
        _uiMultiPurposeDialog.update { currentState ->
            currentState.copy(
                logoutDialog = !_uiMultiPurposeDialog.value.logoutDialog
            )
        }
    }

    fun onClickNavigateToNewLocationDialog() {
        _uiMultiPurposeDialog.update { currentState ->
            currentState.copy(
                navigateToNewLocationDialog = !_uiMultiPurposeDialog.value.navigateToNewLocationDialog
            )
        }
    }

    fun onClickPlayerPropertiesListDialog() {
        _uiMultiPurposeDialog.update { currentState ->
            currentState.copy(
                playerPropertiesListDialog = !_uiMultiPurposeDialog.value.playerPropertiesListDialog
            )
        }
    }

    fun onClickGameOverDialog() {
        _uiMultiPurposeDialog.update { currentState ->
            currentState.copy(
                gameOverDialog = !_uiMultiPurposeDialog.value.gameOverDialog
            )
        }
    }

    private fun onClickIsLoading() {
        _uiMultiPurposeDialog.update { currentState ->
            currentState.copy(
                isLoading = !_uiMultiPurposeDialog.value.isLoading
            )
        }
    }

    /**Live Game Code*/
    fun leaveGame(activity: Activity, isLogOut: Boolean = false): Job {
        return viewModelScope.launch {
            onClickIsLoading()
            firestoreRepositoryImpl.updateGamePlayer(
                playerId = gamePreferenceState.value.playerId,
                playerBalance = -99999
            )
            firestoreGameLogicImpl.setRentLevelToDeleteConstant(gamePreferenceState.value.playerId)
            firestoreRepositoryImpl.deleteGame(gamePreferenceState.value.playerId)
            firestoreRepositoryImpl.deleteAllGamePlayerProperty(gamePreferenceState.value.playerId)
            gameRepositoryImpl.deleteGame()
            playerPropertyRepositoryImpl.playerPropertyDeleteAllProperties()
            gamePreferencesRepository.resetGamePreference()
            onClickIsLoading()
            if(!isLogOut) { activity.finish() }
        }
    }

    fun logOut(activity: Activity): Job {
        return viewModelScope.launch {
            leaveGame(activity, true).join()
            firebaseAuthRepositoryImpl.logOutUser()
            userLoginPreferencesRepository.resetUserLoginPreference()
            activity.finish()
        }
    }

    fun navigateToNewLocation(): Job {
        return viewModelScope.launch {
            firestoreGameLogicImpl.navigateToNewLocation(playerId = gamePreferenceState.value.playerId)
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