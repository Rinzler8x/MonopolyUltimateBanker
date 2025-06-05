package com.example.monopolyultimatebanker.data.playerpropertytable

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PlayerPropertyRepositoryImpl @Inject constructor(
    private val playerPropertyDao: PlayerPropertyDao
) : PlayerPropertyRepository {
    override fun getPlayerProperty(propertyNo: Int): PlayerProperty = playerPropertyDao.getPlayerProperty(propertyNo)

    override fun getPlayerPropertyFlow(propertyNo: Int): Flow<PlayerProperty> = playerPropertyDao.getPlayerPropertyFlow(propertyNo)

    override fun getPlayerPropertiesList(playerId: String): Flow<List<PlayerPropertiesList>?> = playerPropertyDao.getPlayerPropertiesList(playerId)

    override fun playerPropertyExists(propertyNo: Int): Int = playerPropertyDao.propertyExists(propertyNo)

    override suspend fun playerPropertyGetAllPlayerProperties(playerId: String): List<Int>? = playerPropertyDao.getAllPlayerProperties(playerId)

    override suspend fun playerPropertyUpdatePropertyState(playerId: String, propertyNo: Int, rentLevel: Int) = playerPropertyDao.updatePropertyState(playerId, propertyNo, rentLevel)

    override suspend fun playerPropertyCheckIfPropertyBelongsToPlayer(propertyNo: Int, playerId: String): Boolean = playerPropertyDao.checkIfPropertyBelongsToPlayer(propertyNo, playerId) == 1

    override suspend fun playerPropertyCountPlayerProperties(playerId: String): Int = playerPropertyDao.countPlayerProperties(playerId)

    override suspend fun playerPropertyGetPlayerProperties(playerId: String): List<OwnedPlayerProperties>? = playerPropertyDao.getPlayerProperties(playerId)

    override suspend fun playerPropertyRentLevelDecreaseForNeighbors(propertyNo: Int): List<Int>? = playerPropertyDao.rentLevelDecreaseForNeighbors(propertyNo)

    override suspend fun playerPropertyEventRentLevelIncreaseBoardSide(propertyNo: Int): List<Int>? = playerPropertyDao.eventRentLevelIncreaseBoardSide(propertyNo)

    override suspend fun playerPropertyEventRentLevelDecreaseBoardSide(propertyNo: Int): List<Int>? = playerPropertyDao.eventRentLevelDecreaseBoardSide(propertyNo)

    override suspend fun playerPropertyEventRentLevelIncreaseColorSet(propertyNo: Int): List<Int>? = playerPropertyDao.eventRentLevelIncreaseColorSet(propertyNo)

    override suspend fun playerPropertyEventRentLevelDecreaseColorSet(propertyNo: Int): List<Int>? = playerPropertyDao.eventRentLevelDecreaseColorSet(propertyNo)

    override suspend fun playerPropertyInsert(playerProperty: PlayerProperty)  = playerPropertyDao.insert(playerProperty)

    override suspend fun playerPropertyDeleteProperty(rentLevel: Int) = playerPropertyDao.deleteProperty(rentLevel)

    override suspend fun playerPropertyDeleteAllProperties() = playerPropertyDao.deleteAllProperties()
}