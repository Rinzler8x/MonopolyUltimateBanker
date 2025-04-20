package com.example.monopolyultimatebanker.ui.screens.eventcard

import androidx.compose.runtime.currentCompositionErrors
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monopolyultimatebanker.data.eventtable.Event
import com.example.monopolyultimatebanker.data.eventtable.EventRepositoryImpl
import com.example.monopolyultimatebanker.data.firebase.database.FirestoreGameLogicImpl
import com.example.monopolyultimatebanker.data.firebase.database.UpdatedProperty
import com.example.monopolyultimatebanker.data.gametable.GameRepositoryImpl
import com.example.monopolyultimatebanker.data.playerpropertytable.PlayerPropertyRepositoryImpl
import com.example.monopolyultimatebanker.data.preferences.GamePreferencesRepository
import com.example.monopolyultimatebanker.data.preferences.QrPreferencesRepository
import com.example.monopolyultimatebanker.data.preferences.QrType
import com.example.monopolyultimatebanker.ui.screens.home.GameState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    val resultDialogState: Boolean = false,
    val wrongPropertyInputDialogState: Boolean = false
)

data class ResultsUiState(
    val updatedProperties: List<UpdatedProperty> = emptyList(),
    val noPropertiesUpdated: String = "No properties were updated."
)

@HiltViewModel
class EventCardViewModel @Inject constructor(
    private val qrPreferencesRepository: QrPreferencesRepository,
    private val gamePreferencesRepository: GamePreferencesRepository,
    private val gameRepositoryImpl: GameRepositoryImpl,
    private val eventRepositoryImpl: EventRepositoryImpl,
    private val playerPropertyRepositoryImpl: PlayerPropertyRepositoryImpl,
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

    private val _uiResults = MutableStateFlow(ResultsUiState())
    val uiResults: StateFlow<ResultsUiState> = _uiResults.asStateFlow()

    private fun updateResults(results: List<UpdatedProperty>) {
        _uiResults.update { currentState ->
            currentState.copy(
                updatedProperties = results.ifEmpty { emptyList() }
            )
        }
    }

    fun clearResults() {
        updateResults(results = emptyList())
    }

    /**Player Bottom Sheet*/
    private val _uiPlayerBottomSheet = MutableStateFlow(PlayerBottomSheetState())
    val uiPlayerBottomSheet: StateFlow<PlayerBottomSheetState> = _uiPlayerBottomSheet.asStateFlow()

    fun onCLickPlayerBottomSheet() {
        _uiPlayerBottomSheet.update { currentState ->
            currentState.copy(
                showBottomSheet = !_uiPlayerBottomSheet.value.showBottomSheet
            )
        }
    }

    /**Dialog State */
    private val _uiPropertyDialog = MutableStateFlow(DialogState())
    val uiPropertyDialog: StateFlow<DialogState> = _uiPropertyDialog.asStateFlow()

    fun onClickPropertyDialog1() {
        _uiPropertyDialog.update { currentState ->
            currentState.copy(
                propertyDialogState1 = !_uiPropertyDialog.value.propertyDialogState1
            )
        }
    }

    fun onClickPropertyDialog2() {
        _uiPropertyDialog.update { currentState ->
            currentState.copy(
                propertyDialogState2 = !_uiPropertyDialog.value.propertyDialogState2
            )
        }
    }

    fun onClickDoubleInput() {
        _uiPropertyDialog.update { currentState ->
            currentState.copy(
                doubleInput = !_uiPropertyDialog.value.doubleInput
            )
        }
    }

    fun onClickResultDialog() {
        _uiPropertyDialog.update { currentState ->
            currentState.copy(
                resultDialogState = !_uiPropertyDialog.value.resultDialogState
            )
        }
    }

    fun onClickWrongPropertyInputDialog() {
        _uiPropertyDialog.update { currentState ->
            currentState.copy(
                wrongPropertyInputDialogState = !_uiPropertyDialog.value.wrongPropertyInputDialogState
            )
        }
    }

    /**User Input State*/
    var actionUserInput by mutableStateOf(ActionUserInput())

    fun updatePlayerId(input: String) {
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
            firestoreGameLogicImpl.propertySwap(
                propertyNo1 = actionUserInput.propertyNo1.toInt(),
                propertyNo2 = actionUserInput.propertyNo2.toInt(),
                playerId1 = gamePreferencesRepository.gameState.first().playerId,
                playerId2 = actionUserInput.playerId
            )
        }
    }

    private fun rentLevelResetTo1() {
        viewModelScope.launch {
            updateResults(
                results = firestoreGameLogicImpl.rentLevelReset1(
                    propertyNo = actionUserInput.propertyNo1.toInt()
                )
            )
        }
    }

    private fun rentLevelJumpsTo5() {
        viewModelScope.launch {
            updateResults(
                results = firestoreGameLogicImpl.rentLevelJumpTo5(
                    propertyNo = actionUserInput.propertyNo1.toInt()
                )
            )
        }
    }

    private fun rentLevelIncreaseForYouAndDecreaseForNeighbours() {
        viewModelScope.launch {
            updateResults(
                results = firestoreGameLogicImpl.eventRentDecreaseForNeighbors(
                    propertyNo = actionUserInput.propertyNo1.toInt()
                )
            )
        }
    }

    private fun rentLevelIncreaseForBoardSide() {
        viewModelScope.launch {
            updateResults(
                results = firestoreGameLogicImpl.eventRentLevelIncreaseBoardSide(
                    propertyNo = actionUserInput.propertyNo1.toInt()
                )
            )
        }
    }

    private fun rentLevelDecreaseForBoardSide() {
        viewModelScope.launch {
            updateResults(
                results = firestoreGameLogicImpl.eventRentLevelDecreaseBoardSide(
                    propertyNo = actionUserInput.propertyNo1.toInt()
                )
            )
        }
    }

    private fun rentLevelIncreaseForColorSet() {
        viewModelScope.launch {
            updateResults(
                results = firestoreGameLogicImpl.eventRentLevelIncreaseColorSet(
                    propertyNo = actionUserInput.propertyNo1.toInt()
                )
            )
        }
    }

    private fun rentLevelDecreaseForColorSet() {
        viewModelScope.launch {
            updateResults(
                results = firestoreGameLogicImpl.eventRentLevelDecreaseColorSet(
                    propertyNo = actionUserInput.propertyNo1.toInt()
                )
            )
        }
    }

    private fun pay50PerPropertyOwned() {
        viewModelScope.launch {
            firestoreGameLogicImpl.eventDeduct50PerProperty(
                playerId = gamePreferencesRepository.gameState.first().playerId,
            )
        }
    }

//    private fun rentLevel1For2RentPayments() {
//        viewModelScope.launch {
//
//        }
//    }

    fun onClickActionCheckUserInputRequired(navigateToHome: () -> Unit) {
        when(qrPrefState.value.event) {
            "monoeve_1", "monoeve_9" -> {
                onClickDoubleInput()
                onCLickPlayerBottomSheet()
            }
            "monoeve_2", "monoeve_13", "monoeve_18", "monoeve_4",
            "monoeve_22", "monoeve_5", "monoeve_17", "monoeve_11",
            "monoeve_14", "monoeve_16", "monoeve_20" -> {
                onClickPropertyDialog1()
            }
            "monoeve_8" -> {
                onClickAction()
            }
            "monoeve_3", "monoeve_6", "monoeve_7", "monoeve_12", "monoeve_19", "monoeve_15", "monoeve_21" -> {
                navigateToHome()
            }
        }
    }

    fun onClickAction() {
        var propertyOwnerCheck = false
        viewModelScope.launch {
            val playerId = gamePreferencesRepository.gameState.first().playerId
            withContext(Dispatchers.IO) {
                when(qrPrefState.value.event) {
                    "monoeve_1", "monoeve_9" -> {
                        propertyOwnerCheck = playerPropertyRepositoryImpl
                            .playerPropertyCheckIfPropertyBelongsToPlayer(
                                propertyNo = actionUserInput.propertyNo1.toInt(),
                                playerId = playerId
                            )
                        propertyOwnerCheck = playerPropertyRepositoryImpl
                            .playerPropertyCheckIfPropertyBelongsToPlayer(
                                propertyNo = actionUserInput.propertyNo2.toInt(),
                                playerId = actionUserInput.playerId
                            )
                    }
                    "monoeve_2", "monoeve_13", "monoeve_18", "monoeve_4",
                    "monoeve_22", "monoeve_5", "monoeve_17", "monoeve_11",
                    "monoeve_14", "monoeve_16", "monoeve_20" -> {
                        propertyOwnerCheck = playerPropertyRepositoryImpl
                            .playerPropertyCheckIfPropertyBelongsToPlayer(
                                propertyNo = actionUserInput.propertyNo1.toInt(),
                                playerId = playerId
                            )
                    }
                }
            }
            if(propertyOwnerCheck) {
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
//                    "monoeve_10" -> {
//                        rentLevel1For2RentPayments()
//                    }
                }
                onClickResultDialog()
            } else {
                onClickWrongPropertyInputDialog()
            }
            updatePropertyNo1("")
            updatePropertyNo2("")
            updatePlayerId("")
        }

    }
}