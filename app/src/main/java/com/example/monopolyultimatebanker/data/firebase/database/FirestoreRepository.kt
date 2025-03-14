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

    fun getGame(gameId: String): Flow<List<FirestoreGame>>

    suspend fun countGamePlayers(gameId: String): Int

//    val game: Flow<List<Game>>


    //player_properties
    suspend fun insertPlayerProperty(playerProperty: PlayerProperty)

    suspend fun updatePlayerProperty(playerProperty: PlayerProperty)

    suspend fun deleteAllGamePlayerProperty(playerId: String)

//    val playerProperties: Flow<List<PlayerProperty>>

    //users collection
    suspend fun insertUsername(email: String, username: String)

    suspend fun getUsername(email: String): String
}