package com.example.monopolyultimatebanker.data.propertytable

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "property")
data class Property(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "property_no") val propertyNo: Int = 1,
    @ColumnInfo(name = "qr_code") val qrCode: String,
    @ColumnInfo(name = "property_name") val propertyName: String,
    @ColumnInfo(name = "rent_level_1") val rentLevel1: Int,
    @ColumnInfo(name = "rent_level_2") val rentLevel2: Int,
    @ColumnInfo(name = "rent_level_3") val rentLevel3: Int,
    @ColumnInfo(name = "rent_level_4") val rentLevel4: Int,
    @ColumnInfo(name = "rent_level_5") val rentLevel5: Int,
    @ColumnInfo(name = "color") val color: String,
    @ColumnInfo(name = "board_side") val boardSide: Int,
)

