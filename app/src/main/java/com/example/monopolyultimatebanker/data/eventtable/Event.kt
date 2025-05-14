package com.example.monopolyultimatebanker.data.eventtable

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event")
data class Event(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "event_id") val eventId: Int = 1,
    @ColumnInfo(name = "qr_code") val qrCode: String = "",
    @ColumnInfo(name = "title") val title: String = "",
    @ColumnInfo(name = "phrase") val phrase: String = "",
    @ColumnInfo(name = "action") val action: String = "",
)