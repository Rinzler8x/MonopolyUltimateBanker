package com.example.monopolyultimatebanker.data.playerpropertytable

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface PlayerPropertyDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(playerProperty: PlayerProperty)

    @Query("UPDATE player_property SET player_id = :playerId WHERE property_no = :propertyNo")
    suspend fun swapProperty(playerId: Int, propertyNo: Int)

    @Transaction
    suspend fun propertySwap(player1Id: Int, player2Id: Int, property1No: Int, property2No: Int) {
        swapProperty(player2Id, property1No)
        swapProperty(player1Id, property2No)
    }
}