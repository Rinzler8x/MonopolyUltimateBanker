package com.example.monopolyultimatebanker.data.gametable

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(game: Game)

    @Query("SELECT * FROM game")
    fun getGame(): Flow<List<Game>>

    @Query("SELECT * FROM game WHERE player_id = :playerId")
    fun getPlayer(playerId: String): Game

    @Query("UPDATE game SET player_id = :playerId WHERE player_name = :playerName")
    suspend fun updateTempPlayerId(playerId: String, playerName: String)

    @Query("UPDATE game SET player_balance = player_balance - :amount WHERE player_id = :payerId")
    suspend fun deductBalance(payerId: String, amount: Int)

    @Query("UPDATE game SET player_balance = player_balance + :amount WHERE player_id = :recipientId")
    suspend fun addBalance(recipientId: String, amount: Int)

    @Transaction
    suspend fun transferRent(payerId: String, recipientId: String, amount: Int) {
        deductBalance(payerId, amount)
        addBalance(recipientId, amount)
    }

    @Transaction
    suspend fun collect200BothPlayers(player1Id: String, player2Id: String) {
        addBalance(player1Id, 200)
        addBalance(player2Id, 200)
    }

    @Query(
        "UPDATE game SET player_balance = player_balance - (50 * (SELECT COUNT(property_no) FROM player_property WHERE player_id = :playerId))"
    )
    suspend fun eventDeduct50PerProperty(playerId: String)
}