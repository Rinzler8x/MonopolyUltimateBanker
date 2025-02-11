package com.example.monopolyultimatebanker.data.firebase.database

import androidx.compose.runtime.LongState
import com.example.monopolyultimatebanker.data.gametable.Game
import com.example.monopolyultimatebanker.data.playerpropertytable.PlayerProperty
import kotlinx.coroutines.flow.Flow


interface FirestoreRepository {

    //games collection
    suspend fun insertGamePlayer(gamePlayer: Game)

    suspend fun updateGamePlayer(gamePlayer: Game)

    suspend fun deleteGame(playerId: String)

    fun getGame(): Flow<List<FirestoreGame>>

//    val game: Flow<List<Game>>


    //player_properties
    suspend fun insertPlayerProperty(playerProperty: PlayerProperty)

    suspend fun updatePlayerProperty(playerProperty: PlayerProperty)

    suspend fun deleteAllGamePlayerProperty(playerId: String)

//    val playerProperties: Flow<List<PlayerProperty>>
}