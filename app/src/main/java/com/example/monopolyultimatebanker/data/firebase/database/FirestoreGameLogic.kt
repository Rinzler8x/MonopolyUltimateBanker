package com.example.monopolyultimatebanker.data.firebase.database

import com.example.monopolyultimatebanker.data.gametable.Game
import com.example.monopolyultimatebanker.data.playerpropertytable.OwnedPlayerProperties
import com.example.monopolyultimatebanker.data.propertytable.Property
import kotlinx.coroutines.flow.StateFlow

interface FirestoreGameLogic {

    suspend fun transferRent(property: StateFlow<Property>, player: Game)

    suspend fun purchaseProperty(playerId: String, gameId: String, propertyNo: Int, playerBalance: Int, propertyValue: Int)

    suspend fun eventDeduct50PerProperty(playerId: String)

    suspend fun collect200(playerId: String)

    suspend fun navigateToNewLocation(playerId: String)

    suspend fun propertySwap(propertyNo1: Int, propertyNo2: Int, playerId1: String, playerId2: String)

    suspend fun transferPlayerProperty(recipientId: String, playerId: String, playerBalance: Int, playerProperties: List<OwnedPlayerProperties>)

    suspend fun rentLevelReset1(propertyNo: Int): List<UpdatedProperty>

    suspend fun rentLevelJumpTo5(propertyNo: Int): List<UpdatedProperty>

    suspend fun rentLevelIncrease(propertyNo: Int): List<UpdatedProperty>

    suspend fun rentLevelDecrease(propertyNo: Int): List<UpdatedProperty>

    suspend fun eventRentDecreaseForNeighbors(propertyNo: Int): List<UpdatedProperty>

    suspend fun eventRentLevelIncreaseBoardSide(propertyNo: Int): List<UpdatedProperty>

    suspend fun eventRentLevelDecreaseBoardSide(propertyNo: Int): List<UpdatedProperty>

    suspend fun eventRentLevelIncreaseColorSet(propertyNo: Int): List<UpdatedProperty>

    suspend fun eventRentLevelDecreaseColorSet(propertyNo: Int): List<UpdatedProperty>

    suspend fun setRentLevelToDeleteConstant(playerId: String)
}

