package com.example.monopolyultimatebanker.data.gametable

import kotlinx.coroutines.flow.Flow

interface GameRepository {

    fun getGameStream(): Flow<List<Game>>

    fun getGamePlayer(playerId: String): Game

    fun gamePlayerExists(playerName: String): PlayerState

    suspend fun updatePlayerState(amount: Int, playerName: String)

    suspend fun gameInsert(game: Game)

    suspend fun deletePlayer(playerBalance: Int)

    suspend fun deleteGame()
}