package com.example.monopolyultimatebanker.data.firebase.database

import com.example.monopolyultimatebanker.data.gametable.GameRepositoryImpl
import com.example.monopolyultimatebanker.data.playerpropertytable.OwnedPlayerProperties
import com.example.monopolyultimatebanker.data.playerpropertytable.PlayerPropertyRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class UpdatedProperty(
    val propertyNo: Int,
    val rentLevel: Int,
)

class FirestoreGameLogicImpl @Inject constructor(
    private val gameRepositoryImpl: GameRepositoryImpl,
    private val firestoreRepositoryImpl: FirestoreRepositoryImpl,
    private val playerPropertyRepositoryImpl: PlayerPropertyRepositoryImpl,
) : FirestoreGameLogic {

    /**Game*/
    override suspend fun transferRent(propertyNo: Int, playerId: String, playerBalance: Int, rentValue: Int) {
        withContext(Dispatchers.IO) {
            val playerPropertyDetails = playerPropertyRepositoryImpl.getPlayerProperty(propertyNo = propertyNo)
            val playerPropertyOwnerDetails = gameRepositoryImpl.getGamePlayer(playerPropertyDetails.playerId)

            firestoreRepositoryImpl.transferRent(
                payerId = playerId,
                receipentId = playerPropertyDetails.playerId,
                addAmount = (playerPropertyOwnerDetails.playerBalance + rentValue),
                deductAmount = (playerBalance - rentValue)
            )
        }
    }

    override suspend fun purchaseProperty(playerId: String, gameId: String, propertyNo: Int, playerBalance: Int, propertyValue: Int) {
        withContext(Dispatchers.IO) {
            firestoreRepositoryImpl.insertPlayerProperty(
                gameId = gameId,
                playerId = playerId,
                propertyNo = propertyNo
            )
            firestoreRepositoryImpl.updateGamePlayer(
                playerId = playerId,
                playerBalance = (playerBalance - propertyValue)
            )
        }
    }

    override suspend fun eventDeduct50PerProperty(playerId: String) {
        withContext(Dispatchers.IO) {
            val playerBalance = gameRepositoryImpl.getGamePlayer(playerId).playerBalance
            val count = playerPropertyRepositoryImpl.playerPropertyCountPlayerProperties(playerId)
            if(count > 0) {
                val amount = playerBalance - (50 * count)
                firestoreRepositoryImpl.updateGamePlayer(
                    playerId = playerId,
                    playerBalance = amount
                )
            }
        }
    }

    override suspend fun collect200(playerId: String) {
        withContext(Dispatchers.IO) {
            val playerBalance = gameRepositoryImpl.getGamePlayer(playerId).playerBalance

            firestoreRepositoryImpl.updateGamePlayer(
                playerId = playerId,
                playerBalance = (playerBalance + 200)
            )
        }
    }

    override suspend fun navigateToNewLocation(playerId: String) {
        withContext(Dispatchers.IO) {
            val playerBalance = gameRepositoryImpl.getGamePlayer(playerId).playerBalance

            firestoreRepositoryImpl.updateGamePlayer(
                playerId = playerId,
                playerBalance = (playerBalance - 100)
            )
        }
    }

    /**PlayerProperty*/

    override suspend fun propertySwap(propertyNo1: Int, propertyNo2: Int, playerId1: String, playerId2: String) {
        withContext(Dispatchers.IO) {
            val ppId1 = playerPropertyRepositoryImpl.getPlayerProperty(propertyNo = propertyNo1).ppId
            val ppId2 = playerPropertyRepositoryImpl.getPlayerProperty(propertyNo = propertyNo2).ppId
            firestoreRepositoryImpl.swapPlayerProperty(
                ppId1 = ppId1,
                ppId2 = ppId2,
                playerId1 = playerId1,
                playerId2 = playerId2
            )
        }
    }

    override suspend fun transferPlayerProperty(playerProperties: List<OwnedPlayerProperties>, recipientId: String, playerBalance: Int): Int {
        return withContext(Dispatchers.IO) {
            var tempBalance = playerBalance
            playerProperties.forEach { property ->
                firestoreRepositoryImpl.updatePlayerPropertyOwner(
                    ppId = property.ppId,
                    playerId = recipientId
                )
                tempBalance += property.propertyPrice
            }

            tempBalance
        }
    }

    override suspend fun rentLevelReset1(propertyNo: Int): List<UpdatedProperty> {
        return withContext(Dispatchers.IO) {
            val property = playerPropertyRepositoryImpl.getPlayerProperty(propertyNo)
            firestoreRepositoryImpl.updatePlayerPropertyRentLevel(
                ppId = property.ppId,
                rentLevel = 1
            )

            listOf(
                UpdatedProperty(
                    propertyNo = property.propertyNo,
                    rentLevel = 1
                )
            )
        }
    }

    override suspend fun rentLevelJumpTo5(propertyNo: Int): List<UpdatedProperty> {
        return withContext(Dispatchers.IO) {
            val property = playerPropertyRepositoryImpl.getPlayerProperty(propertyNo)
            firestoreRepositoryImpl.updatePlayerPropertyRentLevel(
                ppId = property.ppId,
                rentLevel = 5
            )

            listOf(
                UpdatedProperty(
                    propertyNo = property.propertyNo,
                    rentLevel = 5
                )
            )
        }
    }

    override suspend fun rentLevelIncrease(propertyNo: Int): List<UpdatedProperty> {
        return withContext(Dispatchers.IO) {
            val property = playerPropertyRepositoryImpl.getPlayerProperty(propertyNo)

            if(property.rentLevel != 5) {
                firestoreRepositoryImpl.updatePlayerPropertyRentLevel(
                    ppId = property.ppId,
                    rentLevel = property.rentLevel + 1
                )

                listOf(
                    UpdatedProperty(
                        propertyNo = property.propertyNo,
                        rentLevel = property.rentLevel + 1
                    )
                )
            } else {
                emptyList()
            }
        }
    }

    override suspend fun rentLevelDecrease(propertyNo: Int): List<UpdatedProperty> {
        return withContext(Dispatchers.IO) {
            val property = playerPropertyRepositoryImpl.getPlayerProperty(propertyNo)

            if(property.rentLevel != 1) {
                firestoreRepositoryImpl.updatePlayerPropertyRentLevel(
                    ppId = property.ppId,
                    rentLevel = property.rentLevel - 1
                )

                listOf(
                    UpdatedProperty(
                        propertyNo = property.propertyNo,
                        rentLevel = property.rentLevel - 1
                    )
                )
            } else {
                emptyList()
            }
        }
    }

    override suspend fun eventRentDecreaseForNeighbors(propertyNo: Int): List<UpdatedProperty> {
        return withContext(Dispatchers.IO) {
            val neighbours = playerPropertyRepositoryImpl
                .playerPropertyRentLevelDecreaseForNeighbors(propertyNo) ?: emptyList()
            val updateProperties: MutableList<UpdatedProperty> = mutableListOf()

            if(neighbours.isNotEmpty()){
                neighbours.forEach { i ->
                    val property = playerPropertyRepositoryImpl.getPlayerProperty(i)
                    if(property.rentLevel != 1) {
                        firestoreRepositoryImpl.updatePlayerPropertyRentLevel(
                            ppId = property.ppId,
                            rentLevel = property.rentLevel - 1
                        )
                        updateProperties.add(
                            UpdatedProperty(
                                propertyNo = property.propertyNo,
                                rentLevel = property.rentLevel - 1
                            )
                        )
                    }
                }
            }

            rentLevelIncrease(propertyNo).forEach { property ->
                updateProperties.add(
                    UpdatedProperty(
                        propertyNo = property.propertyNo,
                        rentLevel = property.rentLevel
                    )
                )
            }

            updateProperties
        }
    }

    override suspend fun eventRentLevelIncreaseBoardSide(propertyNo: Int): List<UpdatedProperty> {
        return withContext(Dispatchers.IO) {
            val properties = playerPropertyRepositoryImpl
                .playerPropertyEventRentLevelIncreaseBoardSide(propertyNo) ?: emptyList()
            val updateProperties: MutableList<UpdatedProperty> = mutableListOf()

            if(properties.isNotEmpty()) {
                properties.forEach { i ->
                    val property = playerPropertyRepositoryImpl.getPlayerProperty(i)
                    firestoreRepositoryImpl.updatePlayerPropertyRentLevel(
                        ppId = property.ppId,
                        rentLevel = property.rentLevel + 1
                    )

                    updateProperties.add(
                        UpdatedProperty(
                            propertyNo = property.propertyNo,
                            rentLevel = property.rentLevel + 1
                        )
                    )
                }
            }

            updateProperties
        }
    }

    override suspend fun eventRentLevelDecreaseBoardSide(propertyNo: Int): List<UpdatedProperty> {
        return withContext(Dispatchers.IO) {
            val properties = playerPropertyRepositoryImpl
                .playerPropertyEventRentLevelDecreaseBoardSide(propertyNo) ?: emptyList()
            val updateProperties: MutableList<UpdatedProperty> = mutableListOf()

            if(properties.isNotEmpty()) {
                properties.forEach { i ->
                    val property = playerPropertyRepositoryImpl.getPlayerProperty(i)
                    firestoreRepositoryImpl.updatePlayerPropertyRentLevel(
                        ppId = property.ppId,
                        rentLevel = property.rentLevel - 1
                    )

                    updateProperties.add(
                        UpdatedProperty(
                            propertyNo = property.propertyNo,
                            rentLevel = property.rentLevel - 1
                        )
                    )
                }
            }

            updateProperties
        }
    }

    override suspend fun eventRentLevelIncreaseColorSet(propertyNo: Int): List<UpdatedProperty> {
        return withContext(Dispatchers.IO) {
            val properties = playerPropertyRepositoryImpl
                .playerPropertyEventRentLevelIncreaseColorSet(propertyNo) ?: emptyList()
            val updateProperties: MutableList<UpdatedProperty> = mutableListOf()

            if(properties.isNotEmpty()) {
                properties.forEach { i ->
                    val property = playerPropertyRepositoryImpl.getPlayerProperty(i)
                    firestoreRepositoryImpl.updatePlayerPropertyRentLevel(
                        ppId = property.ppId,
                        rentLevel = property.rentLevel + 1
                    )

                    updateProperties.add(
                        UpdatedProperty(
                            propertyNo = property.propertyNo,
                            rentLevel = property.rentLevel + 1
                        )
                    )
                }
            }

            updateProperties
        }
    }

    override suspend fun eventRentLevelDecreaseColorSet(propertyNo: Int): List<UpdatedProperty> {
        return withContext(Dispatchers.IO) {
            val properties = playerPropertyRepositoryImpl
                .playerPropertyEventRentLevelDecreaseColorSet(propertyNo) ?: emptyList()
            val updateProperties: MutableList<UpdatedProperty> = mutableListOf()

            if(properties.isNotEmpty()) {
                properties.forEach { i ->
                    val property = playerPropertyRepositoryImpl.getPlayerProperty(i)
                    firestoreRepositoryImpl.updatePlayerPropertyRentLevel(
                        ppId = property.ppId,
                        rentLevel = property.rentLevel - 1
                    )

                    updateProperties.add(
                        UpdatedProperty(
                            propertyNo = property.propertyNo,
                            rentLevel = property.rentLevel - 1
                        )
                    )
                }
            }

            updateProperties
        }
    }

    override suspend fun setRentLevelToDeleteConstant(playerId: String) {
        withContext(Dispatchers.IO) {
            val properties = playerPropertyRepositoryImpl
                .playerPropertyGetAllPlayerProperties(playerId) ?: emptyList()

            if(properties.isNotEmpty()) {
                properties.forEach { i ->
                    val property = playerPropertyRepositoryImpl.getPlayerProperty(i)
                    firestoreRepositoryImpl.updatePlayerPropertyRentLevel(
                        ppId = property.ppId,
                        rentLevel = -999
                    )
                }
            }
        }
    }
}