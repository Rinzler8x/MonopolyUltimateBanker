package com.example.monopolyultimatebanker.data.eventtable

import kotlinx.coroutines.flow.Flow

interface EventRepository {

    fun getAllEventsStream(): Flow<List<Event>>

    fun getEventStream(qrCode: String): Flow<Event>

    suspend fun insertEvent(event: Event)
}