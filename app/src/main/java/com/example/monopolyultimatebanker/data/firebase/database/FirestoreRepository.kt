package com.example.monopolyultimatebanker.data.firebase.database

import com.example.monopolyultimatebanker.data.gametable.Game
import com.example.monopolyultimatebanker.data.playerpropertytable.PlayerProperty


interface FirestoreRepository {

    //games collection
    suspend fun insertGamePlayer(gamePlayer: Game)

    suspend fun updateGamePlayer(gamePlayer: Game)

    suspend fun deleteGame(playerId: String)

//    val game: Flow<List<Game>>


    //player_properties
    suspend fun insertPlayerProperty(playerProperty: PlayerProperty)

    suspend fun updatePlayerProperty(playerProperty: PlayerProperty)

    suspend fun deleteAllGamePlayerProperty(playerId: String)

//    val playerProperties: Flow<List<PlayerProperty>>
}