package com.example.monopolyultimatebanker.data.playerpropertytable

import javax.inject.Inject

class PlayerPropertyRepositoryImpl @Inject constructor(
    private val playerPropertyDao: PlayerPropertyDao
) : PlayerPropertyRepository {
    override fun getPlayerProperty(propertyNo: Int): PlayerProperty = playerPropertyDao.getPlayerProperty(propertyNo)

    override fun playerPropertyExists(propertyNo: Int): Int = playerPropertyDao.propertyExists(propertyNo)

    override suspend fun playerPropertyGetAllPlayerProperties(playerId: String): List<Int>? = playerPropertyDao.getAllPlayerProperties(playerId)

    override suspend fun playerPropertyUpdatePropertyState(playerId: String, propertyNo: Int, rentLevel: Int) = playerPropertyDao.updatePropertyState(playerId, propertyNo, rentLevel)

    override suspend fun playerPropertyCheckIfPropertyBelongsToPlayer(propertyNo: Int, playerId: String): Boolean = playerPropertyDao.checkIfPropertyBelongsToPlayer(propertyNo, playerId) == 1

//    override suspend fun playerPropertySwapProperty(playerId: String, propertyNo: Int) = playerPropertyDao.swapProperty(playerId, propertyNo)
//
//    override suspend fun playerPropertyPropertySwap(
//        player1Id: String,
//        player2Id: String,
//        property1No: Int,
//        property2No: Int
//    ) = playerPropertyDao.propertySwap(player1Id, player2Id, property1No, property2No)
//
//    override suspend fun playerPropertyRentLevelRest1(propertyNo: Int) = playerPropertyDao.rentLevelReset1(propertyNo)
//
//    override suspend fun playerPropertyRentLevelJumpTo5(propertyNo: Int) = playerPropertyDao.rentLevelJumpTo5(propertyNo)
//
//    override suspend fun playerPropertyRentLevelIncrease(propertyNo: Int) = playerPropertyDao.rentLevelIncrease(propertyNo)
//
//    override suspend fun playerPropertyRentLevelDecrease(propertyNo: Int) = playerPropertyDao.rentLevelDecrease(propertyNo)

    override suspend fun playerPropertyRentLevelDecreaseForNeighbors(propertyNo: Int): List<Int>? = playerPropertyDao.rentLevelDecreaseForNeighbors(propertyNo)

//    override suspend fun playerPropertyEventRentDecreaseForNeighbors(propertyNo: Int) = playerPropertyDao.eventRentDecreaseForNeighbors(propertyNo)

    override suspend fun playerPropertyEventRentLevelIncreaseBoardSide(propertyNo: Int): List<Int>? = playerPropertyDao.eventRentLevelIncreaseBoardSide(propertyNo)

    override suspend fun playerPropertyEventRentLevelDecreaseBoardSide(propertyNo: Int): List<Int>? = playerPropertyDao.eventRentLevelDecreaseBoardSide(propertyNo)

    override suspend fun playerPropertyEventRentLevelIncreaseColorSet(propertyNo: Int): List<Int>? = playerPropertyDao.eventRentLevelIncreaseColorSet(propertyNo)

    override suspend fun playerPropertyEventRentLevelDecreaseColorSet(propertyNo: Int): List<Int>? = playerPropertyDao.eventRentLevelDecreaseColorSet(propertyNo)

    override suspend fun playerPropertyInsert(playerProperty: PlayerProperty)  = playerPropertyDao.insert(playerProperty)

    override suspend fun playerPropertyDeleteProperty(rentLevel: Int) = playerPropertyDao.deleteProperty(rentLevel)

    override suspend fun playerPropertyDeleteAllProperties() = playerPropertyDao.deleteAllProperties()
}