package com.example.monopolyultimatebanker.data.eventtable

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val eventDao: EventDao
) : EventRepository {
    override fun getAllEventsStream(): Flow<List<Event>> = eventDao.getAllEvents()

    override fun getEventStream(qrCode: String): Flow<Event> = eventDao.getEvent(qrCode)

    override suspend fun insertEvent(event: Event) = eventDao.insert(event)
}