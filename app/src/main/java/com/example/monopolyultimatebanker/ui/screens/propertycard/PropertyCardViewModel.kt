package com.example.monopolyultimatebanker.ui.screens.propertycard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monopolyultimatebanker.data.firebase.database.FirestoreGameLogicImpl
import com.example.monopolyultimatebanker.data.firebase.database.FirestorePlayerProperty
import com.example.monopolyultimatebanker.data.firebase.database.FirestoreRepositoryImpl
import com.example.monopolyultimatebanker.data.gametable.GameRepositoryImpl
import com.example.monopolyultimatebanker.data.playerpropertytable.PlayerPropertyRepositoryImpl
import com.example.monopolyultimatebanker.data.preferences.GamePrefState
import com.example.monopolyultimatebanker.data.preferences.GamePreferencesRepository
import com.example.monopolyultimatebanker.data.preferences.QrPreferencesRepository
import com.example.monopolyultimatebanker.data.preferences.QrType
import com.example.monopolyultimatebanker.data.propertytable.Property
import com.example.monopolyultimatebanker.data.propertytable.PropertyRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.reflect.Field
import javax.inject.Inject

data class PlayerPropertyState(
    val playerProperties: List<FirestorePlayerProperty> = listOf()
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

    fun onClickPay(navigateToHomeScreen: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val player = gameRepositoryImpl.getGamePlayer(
                    playerId = gamePreferencesRepository.gameState.first().playerId
                )
                if(playerPropertyRepositoryImpl.playerPropertyExists(propertyState.value.propertyNo) == 0) {
                    firestoreRepositoryImpl.insertPlayerProperty(
                        gameId = gamePreferencesRepository.gameState.first().gameId,
                        playerId = gamePreferencesRepository.gameState.first().playerId,
                        propertyNo = propertyState.value.propertyNo
                    )
                    firestoreRepositoryImpl.updateGamePlayer(
                        playerId = gamePreferencesRepository.gameState.first().playerId,
                        playerBalance = player.playerBalance - propertyState.value.rentLevel1
                    )
                } else {
                    val propertyOwner = playerPropertyRepositoryImpl.getPlayerProperty(propertyNo = propertyState.value.propertyNo)
                    val propertyOwnerDetails = gameRepositoryImpl.getGamePlayer(propertyOwner.playerId)

                    val fieldName = "rentLevel${propertyOwner.rentLevel}"
                    val field: Field = propertyState.value.javaClass.getDeclaredField(fieldName)
                    field.isAccessible = true
                    val rentValue = field.get(propertyState.value) as Int

                    firestoreGameLogicImpl.rentLevelIncrease(propertyState.value.propertyNo)
                    if(player.playerId != propertyOwnerDetails.playerId) {
                        firestoreGameLogicImpl.transferRent(
                            payerId = player.playerId,
                            recipientId = propertyOwner.playerId,
                            addAmount = (propertyOwnerDetails.playerBalance + rentValue),
                            deductAmount = (player.playerBalance - rentValue)
                        )
                    }
                }
            }
            navigateToHomeScreen()
        }
    }
}