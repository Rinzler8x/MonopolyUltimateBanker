package com.example.monopolyultimatebanker.data.propertytable

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PropertyDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(property: Property)

    @Query("SELECT * FROM property ORDER BY property_no ASC")
    fun getAllProperties(): Flow<List<Property>>

    @Query("SELECT * FROM property WHERE qr_code = :qrCode")
    fun getProperty(qrCode: String)
}