package com.example.monopolyultimatebanker.data.propertytable

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PropertyRepositoryImpl @Inject constructor(
    private val propertyDao: PropertyDao
) : PropertyRepository {
    override fun getAllPropertiesStream(): Flow<List<Property>> = propertyDao.getAllProperties()

    override fun getPropertyStream(qrCode: String): Flow<Property> = propertyDao.getProperty(qrCode)

    override suspend fun insertProperty(property: Property) = propertyDao.insert(property)
}