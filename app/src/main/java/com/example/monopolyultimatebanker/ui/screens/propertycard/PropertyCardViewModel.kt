package com.example.monopolyultimatebanker.ui.screens.propertycard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monopolyultimatebanker.data.firebase.database.FirestoreGameLogicImpl
import com.example.monopolyultimatebanker.data.firebase.database.FirestoreRepositoryImpl
import com.example.monopolyultimatebanker.data.firebase.database.UpdatedProperty
import com.example.monopolyultimatebanker.data.gametable.Game
import com.example.monopolyultimatebanker.data.gametable.GameRepositoryImpl
import com.example.monopolyultimatebanker.data.playerpropertytable.OwnedPlayerProperties
import com.example.monopolyultimatebanker.data.playerpropertytable.PlayerProperty
import com.example.monopolyultimatebanker.data.playerpropertytable.PlayerPropertyRepositoryImpl
import com.example.monopolyultimatebanker.data.preferences.GamePreferencesRepository
import com.example.monopolyultimatebanker.data.preferences.QrPreferencesRepository
import com.example.monopolyultimatebanker.data.preferences.QrType
import com.example.monopolyultimatebanker.data.propertytable.Property
import com.example.monopolyultimatebanker.data.propertytable.PropertyRepositoryImpl
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
import java.lang.reflect.Field
import javax.inject.Inject


data class PropertyBottomSheetState(
    val showBottomSheet: Boolean = false,
    val selectedProperties: List<OwnedPlayerProperties> = listOf(),
    val ownedPlayerProperties: List<OwnedPlayerProperties> = listOf(),
    val rentValue: Int = 0,
    val playerBalance: Int = 0,
)

data class MultiPurposePropertyDialog(
    val purchaseDialogState: Boolean = false,
    val insufficientFundsDialogState: Boolean = false,
    val resultDialogState: Boolean = false,
    val propertyTransferDialogState: Boolean = false,
    val rentLevelIncreaseDialogState: Boolean = false,
    val rentLevel: Int = 1,
)

@HiltViewModel
class PropertyCardViewModel @Inject constructor(
    private val qrPreferencesRepository: QrPreferencesRepository,
    private val propertyRepositoryImpl: PropertyRepositoryImpl,
    private val gameRepositoryImpl: GameRepositoryImpl,
    private val firestoreGameLogicImpl: FirestoreGameLogicImpl,
    private val firestoreRepositoryImpl: FirestoreRepositoryImpl,
    private val gamePreferencesRepository: GamePreferencesRepository,
    private val playerPropertyRepositoryImpl: PlayerPropertyRepositoryImpl
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

    @OptIn(ExperimentalCoroutinesApi::class)
    val propertyState: StateFlow<Property> = qrPrefState
        .flatMapLatest {
            propertyRepositoryImpl.getPropertyStream(it.property)
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = Property()
            )

    @OptIn(ExperimentalCoroutinesApi::class)
    val playerPropertyState: StateFlow<PlayerProperty> = propertyState
        .flatMapLatest {
            playerPropertyRepositoryImpl.getPlayerPropertyFlow(it.propertyNo).map { playerProperty ->
                playerProperty ?: PlayerProperty(rentLevel = 1)
            }
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = PlayerProperty(propertyNo = 1)
            )

    /**Player Bottom Sheet*/
    private val _uiPropertyBottomSheetState = MutableStateFlow(PropertyBottomSheetState())
    val uiPropertyBottomSheetState: StateFlow<PropertyBottomSheetState> = _uiPropertyBottomSheetState.asStateFlow()

    fun onClickPropertyBottomSheet(ownedProperties: List<OwnedPlayerProperties> = emptyList(), rentValue: Int = 0, playerBalance: Int = 0) {
        _uiPropertyBottomSheetState.update { currentState ->
            currentState.copy(
                showBottomSheet = !_uiPropertyBottomSheetState.value.showBottomSheet,
                ownedPlayerProperties = ownedProperties,
                rentValue = rentValue,
                playerBalance = playerBalance
            )
        }
    }

    fun onClickCheckBox(ppId: String, propertyNo: Int, propertyPrice: Int, isChecked: Boolean) {
        val currentList = _uiPropertyBottomSheetState.value.selectedProperties.toMutableList()
        if(isChecked) {
            if(!currentList.contains(OwnedPlayerProperties(ppId, propertyNo, propertyPrice))) {
                currentList.add(OwnedPlayerProperties(ppId, propertyNo, propertyPrice))
            }
        } else {
            currentList.remove(OwnedPlayerProperties(ppId, propertyNo, propertyPrice))
        }

        _uiPropertyBottomSheetState.update { currentState ->
            currentState.copy(selectedProperties = currentList)
        }
    }

    /**Dialog Box*/
    private val _uiMultiPurposePropertyDialog = MutableStateFlow(MultiPurposePropertyDialog())
    val uiMultiPurposePropertyDialog: StateFlow<MultiPurposePropertyDialog> = _uiMultiPurposePropertyDialog.asStateFlow()

    fun onClickPurchaseDialog() {
        _uiMultiPurposePropertyDialog.update { currentState ->
            currentState.copy(
                purchaseDialogState = !_uiMultiPurposePropertyDialog.value.purchaseDialogState
            )
        }
    }

    fun onClickInsufficientFundsDialog() {
        _uiMultiPurposePropertyDialog.update { currentState ->
            currentState.copy(
                insufficientFundsDialogState = !_uiMultiPurposePropertyDialog.value.insufficientFundsDialogState
            )
        }
    }

    fun onClickResultDialog(rentLevel: Int = 0) {
        _uiMultiPurposePropertyDialog.update { currentState ->
            currentState.copy(
                resultDialogState = !_uiMultiPurposePropertyDialog.value.resultDialogState,
                rentLevel = if(_uiMultiPurposePropertyDialog.value.resultDialogState) {
                    rentLevel
                } else {
                    rentLevel
                }
            )
        }
    }

    fun onClickPropertyTransferDialog() {
        _uiMultiPurposePropertyDialog.update { currentState ->
            currentState.copy(
                propertyTransferDialogState = !_uiMultiPurposePropertyDialog.value.propertyTransferDialogState
            )
        }
    }

    fun onClickRentLevelIncreaseDialog(rentLevel: Int = 0) {
        _uiMultiPurposePropertyDialog.update { currentState ->
            currentState.copy(
                rentLevelIncreaseDialogState = !_uiMultiPurposePropertyDialog.value.rentLevelIncreaseDialogState,
                rentLevel = if(_uiMultiPurposePropertyDialog.value.rentLevelIncreaseDialogState) {
                    rentLevel
                } else {
                    rentLevel
                }
            )
        }
    }

    fun transferProperties() {
        viewModelScope.launch {
            lateinit var propertyOwner: PlayerProperty
            lateinit var propertyOwnerDetails: Game
            lateinit var selectedProperties: List<OwnedPlayerProperties>
            lateinit var player: Game
            var rentValue: Int
            var playerBalance: Int

            withContext(Dispatchers.IO) {
                propertyOwner = playerPropertyRepositoryImpl.getPlayerProperty(propertyNo = propertyState.value.propertyNo)
                propertyOwnerDetails = gameRepositoryImpl.getGamePlayer(playerId = propertyOwner.playerId)
                selectedProperties = _uiPropertyBottomSheetState.value.selectedProperties
                player = gameRepositoryImpl.getGamePlayer(gamePreferencesRepository.gameState.first().playerId)
                rentValue = _uiPropertyBottomSheetState.value.rentValue
                playerBalance = player.playerBalance
            }

            firestoreRepositoryImpl.updateGamePlayer(
                playerId = propertyOwner.playerId,
                playerBalance = (propertyOwnerDetails.playerBalance + playerBalance)
            )

            playerBalance = firestoreGameLogicImpl.transferPlayerProperty(
                playerProperties = selectedProperties,
                recipientId = propertyOwner.playerId,
                playerBalance = (playerBalance - rentValue),
            )

            firestoreRepositoryImpl.updateGamePlayer(
                playerId = player.playerId,
                playerBalance = playerBalance
            )

            onClickPropertyBottomSheet()
            onClickPropertyTransferDialog()
        }
    }

    fun onClickPay() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val player = gameRepositoryImpl.getGamePlayer(
                    playerId = gamePreferencesRepository.gameState.first().playerId
                )

                if(playerPropertyRepositoryImpl.playerPropertyExists(propertyState.value.propertyNo) == 0) {
                    if(player.playerBalance >= propertyState.value.propertyPrice) {
                        firestoreGameLogicImpl.purchaseProperty(
                            playerId = player.playerId,
                            gameId = gamePreferencesRepository.gameState.first().gameId,
                            propertyNo = propertyState.value.propertyNo,
                            playerBalance = player.playerBalance,
                            propertyValue = propertyState.value.propertyPrice
                        )
                        onClickPurchaseDialog()
                    } else {
                        onClickInsufficientFundsDialog()
                    }
                } else {
                    val propertyOwner = playerPropertyRepositoryImpl.getPlayerProperty(propertyNo = propertyState.value.propertyNo)
                    val fieldName = "rentLevel${propertyOwner.rentLevel}"
                    val field: Field = propertyState.value.javaClass.getDeclaredField(fieldName)
                    field.isAccessible = true
                    val rentValue = field.get(propertyState.value) as Int
                    val updatedRent: List<UpdatedProperty>

                    if(player.playerId != propertyOwner.playerId) {
                        if(player.playerBalance < rentValue) {
                            val playerProperties = playerPropertyRepositoryImpl.playerPropertyGetPlayerProperties(playerId = player.playerId) ?: emptyList()

                            if(playerProperties.isNotEmpty()) {
                                onClickPropertyBottomSheet(ownedProperties = playerProperties, rentValue = rentValue, playerBalance = player.playerBalance)
                            } else {
                                val propertyOwnerDetails = gameRepositoryImpl.getGamePlayer(propertyOwner.playerId)

                                firestoreRepositoryImpl.updateGamePlayer(
                                    playerId = propertyOwner.playerId,
                                    playerBalance = (propertyOwnerDetails.playerBalance + player.playerBalance)
                                )

                                firestoreRepositoryImpl.updateGamePlayer(
                                    playerId = player.playerId,
                                    playerBalance = (player.playerBalance - rentValue)
                                )
                                updatedRent = firestoreGameLogicImpl.rentLevelIncrease(propertyNo = propertyState.value.propertyNo)
                                onClickResultDialog(rentLevel = if(updatedRent.isNotEmpty()) { updatedRent.first().rentLevel } else propertyOwner.rentLevel)
                            }
                        } else {
                            firestoreGameLogicImpl.transferRent(
                                propertyNo = propertyState.value.propertyNo,
                                playerId = player.playerId,
                                playerBalance = player.playerBalance,
                                rentValue = rentValue
                            )
                            updatedRent = firestoreGameLogicImpl.rentLevelIncrease(propertyNo = propertyState.value.propertyNo)
                            onClickResultDialog(rentLevel = if(updatedRent.isNotEmpty()) { updatedRent.first().rentLevel } else propertyOwner.rentLevel)
                        }
                    } else {
                        updatedRent = firestoreGameLogicImpl.rentLevelIncrease(propertyNo = propertyState.value.propertyNo)
                        onClickRentLevelIncreaseDialog(rentLevel = if(updatedRent.isNotEmpty()) { updatedRent.first().rentLevel } else propertyOwner.rentLevel)
                    }
                }
            }
        }
    }
}