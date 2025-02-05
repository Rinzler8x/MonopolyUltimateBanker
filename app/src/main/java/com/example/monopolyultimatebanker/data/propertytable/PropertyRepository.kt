package com.example.monopolyultimatebanker.data.propertytable

import kotlinx.coroutines.flow.Flow

interface PropertyRepository {

    fun getAllPropertiesStream(): Flow<List<Property>>

    fun getPropertyStream(qrCode: String): Flow<Property>

    suspend fun insertProperty(property: Property)
}