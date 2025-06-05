package com.example.monopolyultimatebanker.data.playerpropertytable

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_property")
data class PlayerProperty(
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "ppid") val ppId: String = "",
    @ColumnInfo(name = "player_id") val playerId: String = "",
    @ColumnInfo(name = "property_no") val propertyNo: Int = 1,
    @ColumnInfo(name = "rent_level") val rentLevel: Int = 1,
)
