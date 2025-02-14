package com.example.monopolyultimatebanker.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.monopolyultimatebanker.data.eventtable.Event
import com.example.monopolyultimatebanker.data.eventtable.EventDao
import com.example.monopolyultimatebanker.data.gametable.Game
import com.example.monopolyultimatebanker.data.gametable.GameDao
import com.example.monopolyultimatebanker.data.playerpropertytable.PlayerProperty
import com.example.monopolyultimatebanker.data.playerpropertytable.PlayerPropertyDao
import com.example.monopolyultimatebanker.data.propertytable.Property
import com.example.monopolyultimatebanker.data.propertytable.PropertyDao

@Database(
    entities = [Property::class, Event::class, Game::class, PlayerProperty::class],
    version = 1,
    exportSchema = false
)
abstract class MonopolyDatabase: RoomDatabase() {

    abstract fun propertyDao(): PropertyDao
    abstract fun eventDao(): EventDao
    abstract fun gameDao(): GameDao
    abstract fun playerPropertyDao(): PlayerPropertyDao

}