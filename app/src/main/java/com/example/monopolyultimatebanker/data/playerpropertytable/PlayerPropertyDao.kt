package com.example.monopolyultimatebanker.data.playerpropertytable

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

data class OwnedPlayerProperties(
    val ppId: String,
    val propertyNo: Int,
    val propertyPrice: Int
)

data class PlayerPropertiesList(
    val propertyNo: Int,
    val propertyName: String,
    val rentLevel: Int
)

@Dao
interface PlayerPropertyDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(playerProperty: PlayerProperty)

    @Query("SELECT * FROM player_property WHERE property_no = :propertyNo")
    fun getPlayerProperty(propertyNo: Int): PlayerProperty

    @Query("SELECT * FROM player_property WHERE property_no = :propertyNo")
    fun getPlayerPropertyFlow(propertyNo: Int): Flow<PlayerProperty>

    @Query("SELECT property.property_no as propertyNo, property.property_name as propertyName, player_property.rent_level as rentLevel FROM property " +
            "INNER JOIN player_property ON property.property_no = player_property.property_no " +
            "WHERE player_property.player_id = :playerId ORDER BY property.property_no ASC")
    fun getPlayerPropertiesList(playerId: String): Flow<List<PlayerPropertiesList>?>

    @Query("SELECT SUM(property.property_price) as totalAssetsValue FROM player_property " +
            "INNER JOIN property ON player_property.property_no = property.property_no " +
            "WHERE player_property.player_id = :playerId")
    fun getTotalAssetsValue(playerId: String): Int?

    @Query("SELECT COUNT(*) as count FROM player_property WHERE property_no = :propertyNo")
    fun propertyExists(propertyNo: Int): Int

    @Query("UPDATE player_property SET player_id = :playerId, rent_level = :rentLevel WHERE property_no = :propertyNo")
    suspend fun updatePropertyState(playerId: String, propertyNo: Int, rentLevel: Int)

    @Query("SELECT CASE WHEN EXISTS (" +
            "SELECT 1 FROM player_property WHERE property_no = :propertyNo AND player_id = :playerId) THEN 1 ELSE 0 " +
            "END AS belongs_to_player")
    fun checkIfPropertyBelongsToPlayer(propertyNo: Int, playerId: String): Int

    @Query("SELECT COUNT(*) FROM player_property WHERE player_id = :playerId")
    fun countPlayerProperties(playerId: String): Int

    @Query(
        "SELECT player_property.ppid as ppId, property.property_no as propertyNo, property.property_price as propertyPrice FROM property INNER JOIN player_property " +
                "WHERE property.property_no = player_property.property_no " +
                "AND player_property.player_id = :playerId"
    )
    fun getPlayerProperties(playerId: String): List<OwnedPlayerProperties>?

    @Query(
        "SELECT property.property_no FROM property INNER JOIN player_property ON player_property.property_no = property.property_no " +
        "WHERE property.property_no = :propertyNo - 1 OR property.property_no = :propertyNo + 1"
    )
    suspend fun rentLevelDecreaseForNeighbors(propertyNo: Int): List<Int>?

    @Query(
        "SELECT property.property_no FROM property INNER JOIN player_property ON player_property.property_no = property.property_no " +
        "WHERE board_side IN (SELECT board_side FROM property WHERE property_no = :propertyNo) AND rent_level != 5"
    )
    suspend fun eventRentLevelIncreaseBoardSide(propertyNo: Int): List<Int>?

    @Query(
        "SELECT property.property_no FROM property INNER JOIN player_property ON player_property.property_no = property.property_no " +
        "WHERE board_side IN (SELECT board_side FROM property WHERE property_no = :propertyNo) AND rent_level != 1"
    )
    suspend fun eventRentLevelDecreaseBoardSide(propertyNo: Int): List<Int>?

    @Query(
        "SELECT property.property_no FROM property INNER JOIN player_property ON player_property.property_no = property.property_no " +
        "WHERE color IN (SELECT color FROM property WHERE property_no = :propertyNo) AND rent_level != 5"
    )
    suspend fun eventRentLevelIncreaseColorSet(propertyNo: Int): List<Int>?

    @Query(
        "SELECT property.property_no FROM property INNER JOIN player_property ON player_property.property_no = property.property_no " +
        "WHERE color IN (SELECT color FROM property WHERE property_no = :propertyNo) AND rent_level != 1"
    )
    suspend fun eventRentLevelDecreaseColorSet(propertyNo: Int): List<Int>?

    @Query("DELETE FROM player_property WHERE rent_level = :rentLevel")
    suspend fun deleteProperty(rentLevel: Int)

    @Query("DELETE FROM player_property")
    suspend fun deleteAllProperties()

    @Query("SELECT property_no FROM player_property WHERE player_id = :playerId")
    suspend fun getAllPlayerProperties(playerId: String): List<Int>?
}