package com.example.monopolyultimatebanker.data.playerpropertytable


interface PlayerPropertyRepository {

    fun getPlayerProperty(propertyNo: Int): PlayerProperty

    fun playerPropertyExists(propertyNo: Int): Int

    suspend fun playerPropertyGetAllPlayerProperties(playerId: String): List<Int>?

    suspend fun playerPropertyUpdatePropertyState(playerId: String, propertyNo: Int, rentLevel: Int)

    suspend fun playerPropertyCheckIfPropertyBelongsToPlayer(propertyNo: Int, playerId: String): Boolean

//    suspend fun playerPropertySwapProperty(playerId: String, propertyNo: Int)
//
//    suspend fun playerPropertyPropertySwap(player1Id: String, player2Id: String, property1No: Int, property2No: Int)
//
//    suspend fun playerPropertyRentLevelRest1(propertyNo: Int)
//
//    suspend fun playerPropertyRentLevelJumpTo5(propertyNo: Int)
//
//    suspend fun playerPropertyRentLevelIncrease(propertyNo: Int)
//
//    suspend fun playerPropertyRentLevelDecrease(propertyNo: Int)

    suspend fun playerPropertyRentLevelDecreaseForNeighbors(propertyNo: Int): List<Int>?

//    suspend fun playerPropertyEventRentDecreaseForNeighbors(propertyNo: Int)

    suspend fun playerPropertyEventRentLevelIncreaseBoardSide(propertyNo: Int): List<Int>?

    suspend fun playerPropertyEventRentLevelDecreaseBoardSide(propertyNo: Int): List<Int>?

    suspend fun playerPropertyEventRentLevelIncreaseColorSet(propertyNo: Int): List<Int>?

    suspend fun playerPropertyEventRentLevelDecreaseColorSet(propertyNo: Int): List<Int>?

    suspend fun playerPropertyInsert(playerProperty: PlayerProperty)

    suspend fun playerPropertyDeleteProperty(rentLevel: Int)

    suspend fun playerPropertyDeleteAllProperties()
}