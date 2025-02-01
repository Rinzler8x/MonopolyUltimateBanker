package com.example.monopolyultimatebanker.data.eventtable

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(event: Event)

    @Query("SELECT * FROM event ORDER BY event_id ASC")
    fun getAllEvents(): Flow<List<Event>>

    @Query("SELECT * FROM event WHERE qr_code = :qrCode")
    fun getEvent(qrCode: String): Flow<Event>
}