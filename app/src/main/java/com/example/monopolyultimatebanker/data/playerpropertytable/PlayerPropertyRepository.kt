package com.example.monopolyultimatebanker.data.playerpropertytable


interface PlayerPropertyRepository {

    fun getPlayerProperty(propertyNo: Int): PlayerProperty

    suspend fun playerPropertySwapProperty(playerId: String, propertyNo: Int)

    suspend fun playerPropertyPropertySwap(player1Id: String, player2Id: String, property1No: Int, property2No: Int)

    suspend fun playerPropertyRentLevelRest1(propertyNo: Int)

    suspend fun playerPropertyRentLevelJumpTo5(propertyNo: Int)

    suspend fun playerPropertyRentLevelIncrease(propertyNo: Int)

    suspend fun playerPropertyRentLevelDecrease(propertyNo: Int)

    suspend fun playerPropertyRentLevelDecreaseForNeighbors(propertyNo: Int)

    suspend fun playerPropertyEventRentDecreaseForNeighbors(propertyNo: Int)

    suspend fun playerPropertyEventRentLevelIncreaseBoardSide(propertyNo: Int)

    suspend fun playerPropertyEventRentLevelDecreaseBoardSide(propertyNo: Int)

    suspend fun playerPropertyEventRentLevelIncreaseColorSet(propertyNo: Int)

    suspend fun playerPropertyEventRentLevelDecreaseColorSet(propertyNo: Int)

    suspend fun playerPropertyInsert(playerProperty: PlayerProperty)
}