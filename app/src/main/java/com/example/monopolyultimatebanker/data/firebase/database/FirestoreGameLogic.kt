package com.example.monopolyultimatebanker.data.firebase.database

import com.example.monopolyultimatebanker.data.playerpropertytable.PlayerProperty

interface FirestoreGameLogic {

    suspend fun transferRent(payerId: String, recipientId: String, addAmount: Int, deductAmount: Int)

    suspend fun collect200BothPlayers(player1Id: String, player2Id: String, amount: Int)

    suspend fun eventDeduct50PerProperty(playerId: String, playerBalance: Int)

    suspend fun propertySwap(ppId1: String, ppId2: String, playerId1: Int, playerId2: Int)

    suspend fun rentLevelReset1(ppId: String)

    suspend fun rentLevelJumpTo5(ppId: String)

    suspend fun rentLevelIncrease(propertyNo: Int)

    suspend fun rentLevelDecrease(propertyNo: Int)

    suspend fun eventRentDecreaseForNeighbors(propertyNo: Int)

    suspend fun eventRentLevelIncreaseBoardSide(propertyNo: Int)

    suspend fun eventRentLevelDecreaseBoardSide(propertyNo: Int)

    suspend fun eventRentLevelIncreaseColorSet(propertyNo: Int)

    suspend fun eventRentLevelDecreaseColorSet(propertyNo: Int)

    suspend fun setRentLevelToDeleteConstant(playerId: String)
}

