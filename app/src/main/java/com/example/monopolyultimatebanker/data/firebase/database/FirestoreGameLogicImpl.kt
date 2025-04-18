package com.example.monopolyultimatebanker.data.firebase.database

import com.example.monopolyultimatebanker.data.gametable.GameRepositoryImpl
import com.example.monopolyultimatebanker.data.playerpropertytable.PlayerPropertyRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class FirestoreGameLogicImpl @Inject constructor(
    private val gameRepositoryImpl: GameRepositoryImpl,
    private val firestoreRepositoryImpl: FirestoreRepositoryImpl,
    private val playerPropertyRepositoryImpl: PlayerPropertyRepositoryImpl,
) : FirestoreGameLogic {

    /**Game*/
    override suspend fun transferRent(payerId: String, recipientId: String, addAmount: Int, deductAmount: Int) {
        firestoreRepositoryImpl.transferRent(
            payerId = payerId,
            receipentId = recipientId,
            addAmount = addAmount,
            deductAmount = deductAmount
        )
    }

    override suspend fun collect200BothPlayers(player1Id: String, player2Id: String, amount: Int) {
        firestoreRepositoryImpl.transferRent(
            payerId = player1Id,
            receipentId = player2Id,
            addAmount = amount,
            deductAmount = amount
        )
    }

    override suspend fun eventDeduct50PerProperty(playerId: String, playerBalance: Int) {
        withContext(Dispatchers.IO) {
            val count = gameRepositoryImpl.gameEventDeduct50PerProperty(playerId) ?: 0
            if(count > 0) {
                val amount = playerBalance - (50 * count)
                firestoreRepositoryImpl.updateGamePlayer(
                    playerId = playerId,
                    playerBalance = amount
                )
            }
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

    override suspend fun rentLevelReset1(ppId: String) {
        firestoreRepositoryImpl.updatePlayerPropertyRentLevel(
            ppId = ppId,
            rentLevel = 1
        )
    }

    override suspend fun rentLevelJumpTo5(ppId: String) {
        firestoreRepositoryImpl.updatePlayerPropertyRentLevel(
            ppId = ppId,
            rentLevel = 5
        )
    }

    override suspend fun rentLevelIncrease(propertyNo: Int) {
        withContext(Dispatchers.IO) {
            val property = playerPropertyRepositoryImpl.getPlayerProperty(propertyNo)
            if(property.rentLevel != 5) {
                firestoreRepositoryImpl.updatePlayerPropertyRentLevel(
                    ppId = property.ppId,
                    rentLevel = property.rentLevel + 1
                )
            }
        }
    }

    override suspend fun rentLevelDecrease(propertyNo: Int) {
        withContext(Dispatchers.IO) {
            val property = playerPropertyRepositoryImpl.getPlayerProperty(propertyNo)
            if(property.rentLevel != 1) {
                firestoreRepositoryImpl.updatePlayerPropertyRentLevel(
                    ppId = property.ppId,
                    rentLevel = property.rentLevel - 1
                )
            }
        }
    }

    override suspend fun eventRentDecreaseForNeighbors(propertyNo: Int) {
        withContext(Dispatchers.IO) {
            val neighbours = playerPropertyRepositoryImpl
                .playerPropertyRentLevelDecreaseForNeighbors(propertyNo) ?: emptyList()

            if(neighbours.isNotEmpty()){
                for(i in neighbours) {
                    val property = playerPropertyRepositoryImpl.getPlayerProperty(i)
                    firestoreRepositoryImpl.updatePlayerPropertyRentLevel(
                        ppId = property.ppId,
                        rentLevel = property.rentLevel - 1
                    )
                }
            }
        }
    }

    override suspend fun eventRentLevelIncreaseBoardSide(propertyNo: Int) {
        withContext(Dispatchers.IO) {
            val properties = playerPropertyRepositoryImpl
                .playerPropertyEventRentLevelIncreaseBoardSide(propertyNo) ?: emptyList()

            if(properties.isNotEmpty()) {
                for(i in properties) {
                    val property = playerPropertyRepositoryImpl.getPlayerProperty(i)
                    firestoreRepositoryImpl.updatePlayerPropertyRentLevel(
                        ppId = property.ppId,
                        rentLevel = property.rentLevel + 1
                    )
                }
            }
        }
    }

    override suspend fun eventRentLevelDecreaseBoardSide(propertyNo: Int) {
        withContext(Dispatchers.IO) {
            val properties = playerPropertyRepositoryImpl
                .playerPropertyEventRentLevelDecreaseBoardSide(propertyNo) ?: emptyList()

            if(properties.isNotEmpty()) {
                for(i in properties) {
                    val property = playerPropertyRepositoryImpl.getPlayerProperty(i)
                    firestoreRepositoryImpl.updatePlayerPropertyRentLevel(
                        ppId = property.ppId,
                        rentLevel = property.rentLevel - 1
                    )
                }
            }
        }
    }

    override suspend fun eventRentLevelIncreaseColorSet(propertyNo: Int) {
        withContext(Dispatchers.IO) {
            val properties = playerPropertyRepositoryImpl
                .playerPropertyEventRentLevelIncreaseColorSet(propertyNo) ?: emptyList()

            if(properties.isNotEmpty()) {
                for(i in properties) {
                    val property = playerPropertyRepositoryImpl.getPlayerProperty(i)
                    firestoreRepositoryImpl.updatePlayerPropertyRentLevel(
                        ppId = property.ppId,
                        rentLevel = property.rentLevel + 1
                    )
                }
            }
        }
    }

    override suspend fun eventRentLevelDecreaseColorSet(propertyNo: Int) {
        withContext(Dispatchers.IO) {
            val properties = playerPropertyRepositoryImpl
                .playerPropertyEventRentLevelDecreaseColorSet(propertyNo) ?: emptyList()

            if(properties.isNotEmpty()) {
                for(i in properties) {
                    val property = playerPropertyRepositoryImpl.getPlayerProperty(i)
                    firestoreRepositoryImpl.updatePlayerPropertyRentLevel(
                        ppId = property.ppId,
                        rentLevel = property.rentLevel - 1
                    )
                }
            }
        }
    }

    override suspend fun setRentLevelToDeleteConstant(playerId: String) {
        withContext(Dispatchers.IO) {
            val properties = playerPropertyRepositoryImpl
                .playerPropertyGetAllPlayerProperties(playerId) ?: emptyList()

            if(properties.isNotEmpty()) {
                for(i in properties) {
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