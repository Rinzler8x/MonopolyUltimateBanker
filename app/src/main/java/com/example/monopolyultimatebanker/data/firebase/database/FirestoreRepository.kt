package com.example.monopolyultimatebanker.data.firebase.database

import androidx.compose.runtime.LongState
import com.example.monopolyultimatebanker.data.gametable.Game
import com.example.monopolyultimatebanker.data.playerpropertytable.PlayerProperty
import kotlinx.coroutines.flow.Flow


interface FirestoreRepository {

    //games collection
    suspend fun insertGamePlayer(gameId: String, playerName: String): String

    suspend fun updateGamePlayer(playerId: String, playerBalance: Int)

    suspend fun deleteGame(playerId: String)

    suspend fun countGamePlayers(gameId: String): Int

    suspend fun transferRent(payerId: String, receipentId: String, addAmount: Int, deductAmount: Int)

    fun getGame(gameId: String): Flow<List<FirestoreGame>>

    //player_properties
    suspend fun insertPlayerProperty(gameId: String, playerId: String, propertyNo: Int)

    suspend fun updatePlayerPropertyRentLevel(ppId: String, rentLevel: Int)

    suspend fun updatePlayerPropertyOwner(ppId: String, playerId: String)

    suspend fun swapPlayerProperty(ppId1: String, ppId2: String, playerId1: String, playerId2: String)

    suspend fun deleteAllGamePlayerProperty(playerId: String)

    fun getPlayerProperty(gameId: String): Flow<List<FirestorePlayerProperty>>

    //users collection
    suspend fun insertUsername(email: String, username: String)

    suspend fun getUsername(email: String): String
}