package com.example.monopolyultimatebanker.data.gametable

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "game")
data class Game(
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "player_id") val playerId: Int,
    @ColumnInfo(name = "player_name") val playerName: String,
    @ColumnInfo(name = "player_balance") val playerBalance: Int,
)
