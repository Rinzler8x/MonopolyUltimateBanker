package com.example.monopolyultimatebanker.data.gametable

import kotlinx.coroutines.flow.Flow

interface GameRepository {

    fun getGameStream(): Flow<List<Game>>

    fun getGamePlayer(playerId: String): Game

    fun gamePlayerExists(playerName: String): PlayerState

    suspend fun updatePlayerState(amount: Int, playerName: String)

    suspend fun gameUpdateTempPlayerId(playerId: String, playerName: String)

    suspend fun gameDeductBalance(payerId: String, amount: Int)

    suspend fun gameAddBalance(recipientId: String, amount: Int)

    suspend fun gameTransferRent(payerId: String, recipientId: String, amount: Int)

    suspend fun gameCollect200BothPlayers(player1Id: String, player2Id: String)

    suspend fun gameEventDeduct50PerProperty(playerId: String)

    suspend fun gameInsert(game: Game)
}