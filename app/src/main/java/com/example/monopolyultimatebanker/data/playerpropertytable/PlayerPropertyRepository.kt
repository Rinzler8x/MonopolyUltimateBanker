package com.example.monopolyultimatebanker.data.playerpropertytable

import kotlinx.coroutines.flow.Flow


interface PlayerPropertyRepository {

    fun getPlayerProperty(propertyNo: Int): PlayerProperty

    fun getPlayerPropertyFlow(propertyNo: Int): Flow<PlayerProperty>

    fun getPlayerPropertiesList(playerId: String): Flow<List<PlayerPropertiesList>?>

    fun playerPropertyGetTotalAssetsValue(playerId: String): Int?

    fun playerPropertyExists(propertyNo: Int): Int

    suspend fun playerPropertyGetAllPlayerProperties(playerId: String): List<Int>?

    suspend fun playerPropertyUpdatePropertyState(playerId: String, propertyNo: Int, rentLevel: Int)

    suspend fun playerPropertyCheckIfPropertyBelongsToPlayer(propertyNo: Int, playerId: String): Boolean

    suspend fun playerPropertyCountPlayerProperties(playerId: String): Int

    suspend fun playerPropertyGetPlayerProperties(playerId: String): List<OwnedPlayerProperties>?

    suspend fun playerPropertyRentLevelDecreaseForNeighbors(propertyNo: Int): List<Int>?

    suspend fun playerPropertyEventRentLevelIncreaseBoardSide(propertyNo: Int): List<Int>?

    suspend fun playerPropertyEventRentLevelDecreaseBoardSide(propertyNo: Int): List<Int>?

    suspend fun playerPropertyEventRentLevelIncreaseColorSet(propertyNo: Int): List<Int>?

    suspend fun playerPropertyEventRentLevelDecreaseColorSet(propertyNo: Int): List<Int>?

    suspend fun playerPropertyInsert(playerProperty: PlayerProperty)

    suspend fun playerPropertyDeleteProperty(rentLevel: Int)

    suspend fun playerPropertyDeleteAllProperties()
}