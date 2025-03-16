package com.example.monopolyultimatebanker.data.playerpropertytable

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.monopolyultimatebanker.data.gametable.Game
import com.example.monopolyultimatebanker.data.propertytable.Property

@Entity(
    tableName = "player_property",
    foreignKeys = [
        ForeignKey(
            entity = Game::class,
            parentColumns = arrayOf("player_id"),
            childColumns = arrayOf("player_id"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Property::class,
            parentColumns = arrayOf("property_no"),
            childColumns = arrayOf("property_no"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
    ])

data class PlayerProperty(
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "ppid") val ppId: String,
    @ColumnInfo(name = "player_id") val playerId: String,
    @ColumnInfo(name = "property_no") val propertyNo: Int,
    @ColumnInfo(name = "rent_level") val rentLevel: Int,
)
