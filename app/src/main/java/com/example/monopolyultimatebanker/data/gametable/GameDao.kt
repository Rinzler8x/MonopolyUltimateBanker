package com.example.monopolyultimatebanker.data.gametable

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

data class PlayerState(
    val count: Int = 0,
    val id: String? = "",
)

@Dao
interface GameDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(game: Game)

    @Query("SELECT * FROM game")
    fun getGame(): Flow<List<Game>>

    @Query("SELECT * FROM game WHERE player_id = :playerId")
    fun getPlayer(playerId: String): Game

    @Query("SELECT COUNT(*) AS count, player_id AS id FROM game where player_name = :playerName")
    fun playerExists(playerName: String): PlayerState //TODO: Convert it to return just Int

    @Query("UPDATE game SET player_balance = :amount WHERE player_name = :playerName")
    suspend fun updatePlayerState(amount: Int, playerName: String)

    @Query("DELETE FROM game WHERE player_balance = :playerBalance")
    suspend fun deletePlayer(playerBalance: Int)

    @Query("DELETE FROM game")
    suspend fun deleteGame()
}