package com.example.monopolyultimatebanker.data.firebase.database

interface FirestoreGameLogic {

    suspend fun transferRent(payerId: String, recipientId: String, addAmount: Int, deductAmount: Int)

    suspend fun collect200BothPlayers(player1Id: String, player2Id: String, amount: Int)

    suspend fun eventDeduct50PerProperty(playerId: String)

    suspend fun propertySwap(propertyNo1: Int, propertyNo2: Int, playerId1: String, playerId2: String)

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

