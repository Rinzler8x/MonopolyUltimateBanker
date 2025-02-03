package com.example.monopolyultimatebanker.data.gametable

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface GameDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(game: Game)

    @Query("UPDATE game SET player_balance = player_balance - :amount WHERE player_id = :payerId")
    suspend fun deductBalance(payerId: Int, amount: Int)

    @Query("UPDATE game SET player_balance = player_balance + :amount WHERE player_id = :recipientId")
    suspend fun addBalance(recipientId: Int, amount: Int)

    @Transaction
    suspend fun transferRent(payerId: Int, recipientId: Int, amount: Int) {
        deductBalance(payerId, amount)
        addBalance(recipientId, amount)
    }
}