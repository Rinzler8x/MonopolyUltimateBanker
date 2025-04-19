package com.example.monopolyultimatebanker.data.playerpropertytable

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface PlayerPropertyDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(playerProperty: PlayerProperty)

    @Query("SELECT * FROM player_property WHERE property_no = :propertyNo")
    fun getPlayerProperty(propertyNo: Int): PlayerProperty

    @Query("SELECT COUNT(*) as count FROM player_property WHERE property_no = :propertyNo")
    fun propertyExists(propertyNo: Int): Int

    @Query("UPDATE player_property SET player_id = :playerId, property_no = :propertyNo, rent_level = :rentLevel WHERE property_no = :propertyNo")
    suspend fun updatePropertyState(playerId: String, propertyNo: Int, rentLevel: Int)

    @Query("UPDATE player_property SET player_id = :playerId WHERE property_no = :propertyNo")
    suspend fun swapProperty(playerId: String, propertyNo: Int)

    @Transaction
    suspend fun propertySwap(player1Id: String, player2Id: String, property1No: Int, property2No: Int) {
        swapProperty(player2Id, property1No)
        swapProperty(player1Id, property2No)
    }

    @Query("UPDATE player_property SET rent_level = 1 WHERE property_no = :propertyNo")
    suspend fun rentLevelReset1(propertyNo: Int)

    @Query("UPDATE player_property SET rent_level = 5 WHERE property_no = :propertyNo")
    suspend fun rentLevelJumpTo5(propertyNo: Int)

    @Query("UPDATE player_property SET rent_level = rent_level + 1 WHERE property_no = :propertyNo AND rent_level != 5")
    suspend fun rentLevelIncrease(propertyNo: Int)

    @Query("UPDATE player_property SET rent_level = rent_level - 1 WHERE property_no = :propertyNo AND rent_level != 1")
    suspend fun rentLevelDecrease(propertyNo: Int)

    @Query(
        "SELECT property.property_no FROM property INNER JOIN player_property ON player_property.property_no = property.property_no " +
        "WHERE property.property_no = :propertyNo - 1 OR property.property_no = :propertyNo + 1"
    )
    suspend fun rentLevelDecreaseForNeighbors(propertyNo: Int): List<Int>?

    @Transaction
    suspend fun eventRentDecreaseForNeighbors(propertyNo: Int) {
        rentLevelIncrease(propertyNo)
        rentLevelDecreaseForNeighbors(propertyNo)
    }

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