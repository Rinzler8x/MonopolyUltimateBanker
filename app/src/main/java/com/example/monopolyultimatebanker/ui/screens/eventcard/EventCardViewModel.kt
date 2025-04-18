package com.example.monopolyultimatebanker.ui.screens.eventcard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monopolyultimatebanker.data.eventtable.Event
import com.example.monopolyultimatebanker.data.eventtable.EventRepositoryImpl
import com.example.monopolyultimatebanker.data.firebase.database.FirestoreGameLogicImpl
import com.example.monopolyultimatebanker.data.gametable.GameRepositoryImpl
import com.example.monopolyultimatebanker.data.preferences.GamePreferencesRepository
import com.example.monopolyultimatebanker.data.preferences.QrPreferencesRepository
import com.example.monopolyultimatebanker.data.preferences.QrType
import com.example.monopolyultimatebanker.ui.screens.home.GameState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerBottomSheetState(
    val showBottomSheet: Boolean = false
)

data class ActionUserInput(
    val playerId: String = "",
    val propertyNo1: String = "",
    val propertyNo2: String = "",
)

data class DialogState(
    val propertyDialogState1: Boolean = false,
    val propertyDialogState2: Boolean = false,
    val doubleInput: Boolean = false,
)

@HiltViewModel
class EventCardViewModel @Inject constructor(
    private val qrPreferencesRepository: QrPreferencesRepository,
    private val gamePreferencesRepository: GamePreferencesRepository,
    private val gameRepositoryImpl: GameRepositoryImpl,
    private val eventRepositoryImpl: EventRepositoryImpl,
    private val firestoreGameLogicImpl: FirestoreGameLogicImpl
): ViewModel() {

    val qrPrefState: StateFlow<QrType> =
        qrPreferencesRepository.qrState.map {
            QrType(property = it.property, event = it.event)
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = QrType()
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
    val eventState: StateFlow<Event> = qrPrefState
        .flatMapLatest {
            eventRepositoryImpl.getEventStream(it.event)
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = Event()
            )

    /**Player Bottom Sheet*/
    var playerBottomSheetState by mutableStateOf(PlayerBottomSheetState())

    fun onCLickPlayerBottomSheet() {
        playerBottomSheetState = playerBottomSheetState.copy(showBottomSheet = !playerBottomSheetState.showBottomSheet)
    }

    var propertyDialogState by mutableStateOf(DialogState())

    fun onClickPropertyDialog1() {
        propertyDialogState = propertyDialogState.copy(propertyDialogState1 = !propertyDialogState.propertyDialogState1)
    }

    fun onClickPropertyDialog2() {
        propertyDialogState = propertyDialogState.copy(propertyDialogState2 = !propertyDialogState.propertyDialogState2)
    }

    fun onClickDoubleInput() {
        propertyDialogState = propertyDialogState.copy(doubleInput = !propertyDialogState.doubleInput)
    }

    var actionUserInput by mutableStateOf(ActionUserInput())

    fun updatePlayerName(input: String) {
        actionUserInput = actionUserInput.copy(playerId = input.trim())
    }

    fun updatePropertyNo1(input: String) {
        actionUserInput = actionUserInput.copy(propertyNo1 = input.trim())
    }

    fun updatePropertyNo2(input: String) {
        actionUserInput = actionUserInput.copy(propertyNo2 = input.trim())
    }

    private fun swapProperty() {
        viewModelScope.launch {

        }
    }

    private fun rentLevelResetTo1() {
        viewModelScope.launch {

        }
    }

    private fun rentLevelJumpsTo5() {
        viewModelScope.launch {

        }
    }

    private fun rentLevelIncreaseForYouAndDecreaseForNeighbours() {
        viewModelScope.launch {

        }
    }

    private fun rentLevelIncreaseForBoardSide() {
        viewModelScope.launch {

        }
    }

    private fun rentLevelDecreaseForBoardSide() {
        viewModelScope.launch {

        }
    }

    private fun rentLevelIncreaseForColorSet() {
        viewModelScope.launch {

        }
    }

    private fun rentLevelDecreaseForColorSet() {
        viewModelScope.launch {

        }
    }

    private fun pay50PerPropertyOwned() {
        viewModelScope.launch {

        }
    }

    private fun rentLevel1For2RentPayments() {
        viewModelScope.launch {

        }
    }

    fun onClickActionCheckUserInputRequired() {
        when(qrPrefState.value.event) {
            "monoeve_1", "monoeve_9",  -> {
                onClickDoubleInput()
                onCLickPlayerBottomSheet()
            }
            "monoeve_2", "monoeve_13", "monoeve_18", "monoeve_4",
            "monoeve_22", "monoeve_5", "monoeve_17", "monoeve_11",
            "monoeve_14", "monoeve_16", "monoeve_8" -> {
                onClickPropertyDialog1()
            }
            "monoeve_15", "monoeve_21" -> {

            }
            else -> {
                onClickAction()
            }
        }
    }

    fun onClickAction() {
        when(qrPrefState.value.event) {
            "monoeve_1", "monoeve_9" -> {
                swapProperty()
            }
            "monoeve_2", "monoeve_13", "monoeve_18" -> {
                rentLevelResetTo1()
            }
            "monoeve_20" -> {
                rentLevelJumpsTo5()
            }
            "monoeve_3", "monoeve_6", "monoeve_7", "monoeve_12", "monoeve_19" -> {
                //TODO: navigate back to home screen
            }
            "monoeve_4", "monoeve_22" -> {
                rentLevelIncreaseForYouAndDecreaseForNeighbours()
            }
            "monoeve_5" -> {
                rentLevelIncreaseForBoardSide()
            }
            "monoeve_17" -> {
                rentLevelDecreaseForBoardSide()
            }
            "monoeve_11", "monoeve_14" -> {
                rentLevelIncreaseForColorSet()
            }
            "monoeve_16" -> {
                rentLevelDecreaseForColorSet()
            }
            "monoeve_8" -> {
                pay50PerPropertyOwned()
            }
            "monoeve_10" -> {
                rentLevel1For2RentPayments()
            }
            "monoeve_15", "monoeve_21" -> {
                //TODO: Plans to just makes the players collect the 200 manually
            }
        }
    }
}