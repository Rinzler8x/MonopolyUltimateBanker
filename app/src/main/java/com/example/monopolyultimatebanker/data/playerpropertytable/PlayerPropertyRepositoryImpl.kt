package com.example.monopolyultimatebanker.data.playerpropertytable

import javax.inject.Inject

class PlayerPropertyRepositoryImpl @Inject constructor(
    private val playerPropertyDao: PlayerPropertyDao
) : PlayerPropertyRepository {
    override fun getPlayerProperty(propertyNo: Int) = playerPropertyDao.getPlayerProperty(propertyNo)

    override fun playerPropertyExists(propertyNo: Int): Int = playerPropertyDao.propertyExists(propertyNo)

    override suspend fun playerPropertyUpdatePropertyState(playerProperty: PlayerProperty) = playerPropertyDao.updatePropertyState(playerProperty)

    override suspend fun playerPropertySwapProperty(playerId: String, propertyNo: Int) = playerPropertyDao.swapProperty(playerId, propertyNo)

    override suspend fun playerPropertyPropertySwap(
        player1Id: String,
        player2Id: String,
        property1No: Int,
        property2No: Int
    ) = playerPropertyDao.propertySwap(player1Id, player2Id, property1No, property2No)

    override suspend fun playerPropertyRentLevelRest1(propertyNo: Int) = playerPropertyDao.rentLevelReset1(propertyNo)

    override suspend fun playerPropertyRentLevelJumpTo5(propertyNo: Int) = playerPropertyDao.rentLevelJumpTo5(propertyNo)

    override suspend fun playerPropertyRentLevelIncrease(propertyNo: Int) = playerPropertyDao.rentLevelIncrease(propertyNo)

    override suspend fun playerPropertyRentLevelDecrease(propertyNo: Int) = playerPropertyDao.rentLevelDecrease(propertyNo)

    override suspend fun playerPropertyRentLevelDecreaseForNeighbors(propertyNo: Int) = playerPropertyDao.rentLevelDecreaseForNeighbors(propertyNo)

    override suspend fun playerPropertyEventRentDecreaseForNeighbors(propertyNo: Int) = playerPropertyDao.eventRentDecreaseForNeighbors(propertyNo)

    override suspend fun playerPropertyEventRentLevelIncreaseBoardSide(propertyNo: Int) = playerPropertyDao.eventRentLevelIncreaseBoardSide(propertyNo)

    override suspend fun playerPropertyEventRentLevelDecreaseBoardSide(propertyNo: Int) = playerPropertyDao.eventRentLevelDecreaseBoardSide(propertyNo)

    override suspend fun playerPropertyEventRentLevelIncreaseColorSet(propertyNo: Int) = playerPropertyDao.eventRentLevelIncreaseColorSet(propertyNo)

    override suspend fun playerPropertyEventRentLevelDecreaseColorSet(propertyNo: Int) = playerPropertyDao.eventRentLevelDecreaseColorSet(propertyNo)

    override suspend fun playerPropertyInsert(playerProperty: PlayerProperty)  = playerPropertyDao.insert(playerProperty)
}