package com.example.monopolyultimatebanker.data.propertytable

import androidx.annotation.ColorInt
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "property")
data class Property(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "property_no") val propertyNo: Int = 1,
    @ColumnInfo(name = "qr_code") val qrCode: String = "",
    @ColumnInfo(name = "property_name") val propertyName: String = "",
    @ColumnInfo(name = "property_price") val propertyPrice: Int = 0,
    @ColumnInfo(name = "rent_level_1") val rentLevel1: Int = 0,
    @ColumnInfo(name = "rent_level_2") val rentLevel2: Int = 0,
    @ColumnInfo(name = "rent_level_3") val rentLevel3: Int = 0,
    @ColumnInfo(name = "rent_level_4") val rentLevel4: Int = 0,
    @ColumnInfo(name = "rent_level_5") val rentLevel5: Int = 0,
    @ColumnInfo(name = "color") @ColorInt val color: Int = 0,
    @ColumnInfo(name = "board_side") val boardSide: Int = 0,
)

