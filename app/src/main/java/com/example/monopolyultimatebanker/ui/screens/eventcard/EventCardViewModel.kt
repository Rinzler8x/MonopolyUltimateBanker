package com.example.monopolyultimatebanker.ui.screens.eventcard

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

data class PropertyDialogState(
    val propertyDialogState1: Boolean = false,
    val propertyDialogState2: Boolean = false,
    val doubleInput: Boolean = false,
    val resultDialogState: Boolean = false,
    val wrongPropertyInputDialogState: Boolean = false,
    val isLoading: Boolean = false
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
    private val _uiPropertyDialog = MutableStateFlow(PropertyDialogState())
    val uiPropertyDialog: StateFlow<PropertyDialogState> = _uiPropertyDialog.asStateFlow()

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

    private fun onClickIsLoading() {
        _uiPropertyDialog.update { currentState ->
            currentState.copy(
                isLoading = !_uiPropertyDialog.value.isLoading
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

    private fun swapProperty(playerId: String, otherPlayerId: String, propertyNo1: Int, propertyNo2: Int) {
        viewModelScope.launch {
            onClickIsLoading()
            firestoreGameLogicImpl.propertySwap(
                propertyNo1 = propertyNo1,
                propertyNo2 = propertyNo2,
                playerId1 = playerId,
                playerId2 = otherPlayerId
            )
            onClickIsLoading()
        }
    }

    private fun rentLevelResetTo1(propertyNo: Int) {
        viewModelScope.launch {
            onClickIsLoading()
            updateResults(
                results = firestoreGameLogicImpl.rentLevelReset1(
                    propertyNo = propertyNo
                )
            )
        }
        onClickIsLoading()
    }

    private fun rentLevelJumpsTo5(propertyNo: Int) {
        viewModelScope.launch {
            onClickIsLoading()
            updateResults(
                results = firestoreGameLogicImpl.rentLevelJumpTo5(
                    propertyNo = propertyNo
                )
            )
            onClickIsLoading()
        }
    }

    private fun rentLevelIncreaseForYouAndDecreaseForNeighbours(propertyNo: Int) {
        viewModelScope.launch {
            onClickIsLoading()
            updateResults(
                results = firestoreGameLogicImpl.eventRentDecreaseForNeighbors(
                    propertyNo = propertyNo
                )
            )
            onClickIsLoading()
        }
    }

    private fun rentLevelIncreaseForBoardSide(propertyNo: Int) {
        viewModelScope.launch {
            onClickIsLoading()
            updateResults(
                results = firestoreGameLogicImpl.eventRentLevelIncreaseBoardSide(
                    propertyNo = propertyNo
                )
            )
            onClickIsLoading()
        }
    }

    private fun rentLevelDecreaseForBoardSide(propertyNo: Int) {
        viewModelScope.launch {
            onClickIsLoading()
            updateResults(
                results = firestoreGameLogicImpl.eventRentLevelDecreaseBoardSide(
                    propertyNo = propertyNo
                )
            )
            onClickIsLoading()
        }
    }

    private fun rentLevelIncreaseForColorSet(propertyNo: Int) {
        viewModelScope.launch {
            onClickIsLoading()
            updateResults(
                results = firestoreGameLogicImpl.eventRentLevelIncreaseColorSet(
                    propertyNo = propertyNo
                )
            )
            onClickIsLoading()
        }
    }

    private fun rentLevelDecreaseForColorSet(propertyNo: Int) {
        viewModelScope.launch {
            onClickIsLoading()
            updateResults(
                results = firestoreGameLogicImpl.eventRentLevelDecreaseColorSet(
                    propertyNo = propertyNo
                )
            )
            onClickIsLoading()
        }
    }

    private fun pay50PerPropertyOwned(playerId: String) {
        viewModelScope.launch {
            onClickIsLoading()
            firestoreGameLogicImpl.eventDeduct50PerProperty(
                playerId = playerId,
            )
            onClickIsLoading()
        }
    }

//    private fun rentLevel1For2RentPayments() {
//        viewModelScope.launch {
//
//        }
//    }

    fun onClickActionCheckUserInputRequired(navigateToHome: () -> Unit) {
        when (qrPrefState.value.event) {
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

            "monoeve_3", "monoeve_6", "monoeve_7", "monoeve_10",
            "monoeve_12", "monoeve_19", "monoeve_15", "monoeve_21" -> {
                navigateToHome()
            }
        }
    }

    fun onClickAction() {
        var propertyOwnerCheck = false
        viewModelScope.launch {
            val playerId = gamePreferencesRepository.gameState.first().playerId
            val otherPlayerId = actionUserInput.playerId
            val propertyNo1 = actionUserInput.propertyNo1
            val propertyNo2 = actionUserInput.propertyNo2
            val count: Int
            withContext(Dispatchers.IO) {
                when (qrPrefState.value.event) {
                    "monoeve_1", "monoeve_9" -> {
                        propertyOwnerCheck = playerPropertyRepositoryImpl
                            .playerPropertyCheckIfPropertyBelongsToPlayer(
                                propertyNo = propertyNo1.toInt(),
                                playerId = playerId
                            )
                        propertyOwnerCheck = playerPropertyRepositoryImpl
                            .playerPropertyCheckIfPropertyBelongsToPlayer(
                                propertyNo = propertyNo2.toInt(),
                                playerId = otherPlayerId
                            )
                    }

                    "monoeve_4", "monoeve_22", "monoeve_5", "monoeve_17",
                    "monoeve_11", "monoeve_14", "monoeve_16", "monoeve_20" -> {
                        propertyOwnerCheck = playerPropertyRepositoryImpl
                            .playerPropertyCheckIfPropertyBelongsToPlayer(
                                propertyNo = propertyNo1.toInt(),
                                playerId = playerId
                            )
                    }

                    "monoeve_2", "monoeve_13", "monoeve_18" -> {
                        count = playerPropertyRepositoryImpl.playerPropertyExists(
                            propertyNo = propertyNo1.toInt()
                        )

                        propertyOwnerCheck = (count > 0)
                    }

                    "monoeve_8" -> {
                        count = playerPropertyRepositoryImpl
                            .playerPropertyCountPlayerProperties(
                                playerId = playerId
                            )
                        propertyOwnerCheck = (count > 0)
                    }
                }
            }

            if (propertyOwnerCheck) {
                when (qrPrefState.value.event) {
                    "monoeve_1", "monoeve_9" -> {
                        swapProperty(
                            playerId = playerId,
                            otherPlayerId = otherPlayerId,
                            propertyNo1 = propertyNo1.toInt(),
                            propertyNo2 = propertyNo2.toInt()
                        )
                    }

                    "monoeve_2", "monoeve_13", "monoeve_18" -> {
                        rentLevelResetTo1(propertyNo1.toInt())
                    }

                    "monoeve_20" -> {
                        rentLevelJumpsTo5(propertyNo1.toInt())
                    }

                    "monoeve_4", "monoeve_22" -> {
                        rentLevelIncreaseForYouAndDecreaseForNeighbours(propertyNo1.toInt())
                    }

                    "monoeve_5" -> {
                        rentLevelIncreaseForBoardSide(propertyNo1.toInt())
                    }

                    "monoeve_17" -> {
                        rentLevelDecreaseForBoardSide(propertyNo1.toInt())
                    }

                    "monoeve_11", "monoeve_14" -> {
                        rentLevelIncreaseForColorSet(propertyNo1.toInt())
                    }

                    "monoeve_16" -> {
                        rentLevelDecreaseForColorSet(propertyNo1.toInt())
                    }

                    "monoeve_8" -> {
                        pay50PerPropertyOwned(playerId)
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