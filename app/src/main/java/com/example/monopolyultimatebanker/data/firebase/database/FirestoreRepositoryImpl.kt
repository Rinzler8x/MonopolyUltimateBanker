package com.example.monopolyultimatebanker.data.firebase.database

import android.content.ContentValues.TAG
import android.util.Log
import com.example.monopolyultimatebanker.data.gametable.Game
import com.example.monopolyultimatebanker.data.gametable.GameRepositoryImpl
import com.example.monopolyultimatebanker.data.playerpropertytable.PlayerProperty
import com.example.monopolyultimatebanker.data.playerpropertytable.PlayerPropertyRepositoryImpl
import com.example.monopolyultimatebanker.data.preferences.GamePreferencesRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class FirestoreGame(
    val gameId: String = "",
    val playerName: String = "",
    val playerBalance: Int = 1500
)

data class FirestorePlayerProperty(
    val gameId: String = "",
    val playerId: String = "",
    val propertyNo: Int = 1,
    val rentLevel: Int = 1
)

class FirestoreRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val gamePreferencesRepository: GamePreferencesRepository,
    private val gameRepositoryImpl: GameRepositoryImpl,
    private val playerPropertyRepositoryImpl: PlayerPropertyRepositoryImpl
): FirestoreRepository {

    private val gameRef = db.collection("games")
    private val playerPropertyRef = db.collection("player_properties")
    private val userRef = db.collection("users")

    /**Game Logic*/
    override fun getGame(gameId: String) = gameRef.whereEqualTo("gameId", gameId).snapshots().map { it ->
        val temp = it.toObjects<FirestoreGame>()
        withContext(Dispatchers.IO) {
            it.map { it2 ->
                for(i in temp) {
                    if(gameRepositoryImpl.gamePlayerExists(i.playerName).count == 0){
                        gameRepositoryImpl.gameInsert(Game(it2.id, i.playerName, i.playerBalance))
                    } else {
                        gameRepositoryImpl.updatePlayerState(i.playerBalance, i.playerName)
                        if(i.playerBalance == -99999) {
                            gameRepositoryImpl.deletePlayer(i.playerBalance)
                        }
                    }
                }
            }
        }
        it.toObjects<FirestoreGame>()
    }

    override suspend fun countGamePlayers(gameId: String): Int {
        return withContext(Dispatchers.IO) {
            try {
                val count = gameRef.whereEqualTo("gameId", gameId)
                    .get()
                    .await()
                count.size()
            } catch(e: Exception) {
                Log.d(TAG, "MSG: ${e.message}")
                -1
            }
        }
    }

    override suspend fun insertGamePlayer(
        gameId: String,
        playerName: String,
    ): String {
        val data = FirestoreGame(
            gameId = gameId,
            playerName = playerName,
            playerBalance = 1500
        )
        var playerId = ""
        return withContext(Dispatchers.IO) {
            try {
                playerId = gameRef
                    .add(data)
                    .await()
                    .id
                playerId
            } catch(e: Exception) {
                Log.d(TAG, "Message: ${e.message}")
                playerId
            }
        }
    }

    override suspend fun updateGamePlayer(
        playerId: String,
        playerBalance: Int
    ) {
        withContext(Dispatchers.IO) {
            try {
                gameRef.document(playerId)
                    .update("playerBalance", playerBalance)
                    .await()

            } catch(e: Exception) {
                Log.d(TAG, "Message: ${e.message}")
            }
        }
    }

    override suspend fun transferRent(payerId: String, receipentId: String, addAmount: Int, deductAmount: Int) {
        withContext(Dispatchers.IO) {
            try {
                db.runTransaction { transaction ->
                    transaction.update(gameRef.document(payerId), "playerBalance", deductAmount)
                    transaction.update(gameRef.document(receipentId), "playerBalance", addAmount)
                }
                    .await()
            } catch(e: Exception) {
                Log.d(TAG, "Message: ${e.message}")
            }
        }
    }

    override suspend fun deleteGame(playerId: String) {
        withContext(Dispatchers.IO) {
            try {
                gameRef.document(playerId)
                    .delete()
                    .await()

            } catch(e: Exception) {
                Log.d(TAG, "Message: ${e.message}")
            }
        }
    }

    /**PlayerProperty Logic*/
    override fun getPlayerProperty(gameId: String): Flow<List<FirestorePlayerProperty>> =
        playerPropertyRef.whereEqualTo("gameId", gameId).snapshots().map { it ->
            val temp = it.toObjects<FirestorePlayerProperty>()
            withContext(Dispatchers.IO) {
                it.map { it2 ->
                    for(i in temp) {
                        if(playerPropertyRepositoryImpl.playerPropertyExists(i.propertyNo) == 0) {
                            playerPropertyRepositoryImpl.playerPropertyInsert(
                                PlayerProperty(
                                    ppId = it2.id,
                                    playerId = i.playerId,
                                    propertyNo = i.propertyNo,
                                    rentLevel = i.rentLevel
                                )
                            )
                        } else {
                            playerPropertyRepositoryImpl.playerPropertyUpdatePropertyState(
                                playerId = i.playerId,
                                propertyNo = i.propertyNo,
                                rentLevel = i.rentLevel
                            )
                            if(i.rentLevel == -999) {
                                playerPropertyRepositoryImpl.playerPropertyDeleteProperty(i.rentLevel)
                            }
                        }
                    }
                }
            }
            it.toObjects<FirestorePlayerProperty>()
        }

    override suspend fun insertPlayerProperty(gameId: String, playerId: String, propertyNo: Int){
        val data = FirestorePlayerProperty(
            gameId = gameId,
            playerId = playerId,
            propertyNo = propertyNo,
            rentLevel = 1
        )
        return withContext(Dispatchers.IO) {
            try {
                playerPropertyRef
                    .add(data)
                    .await()
            } catch(e: Exception) {
                Log.d(TAG, "Message: ${e.message}")
            }
        }
    }

    override suspend fun updatePlayerPropertyRentLevel(ppId: String, rentLevel: Int) {
        withContext(Dispatchers.IO) {
            try {
                playerPropertyRef.document(ppId)
                    .update("rentLevel", rentLevel)
                    .await()

            } catch(e: Exception) {
                Log.d(TAG, "Message: ${e.message}")
            }
        }
    }

    override suspend fun updatePlayerPropertyOwner(ppId: String, playerId: String) {
        withContext(Dispatchers.IO) {
            try {
                playerPropertyRef.document(ppId)
                    .update("playerId", playerId)
                    .await()

            } catch(e: Exception) {
                Log.d(TAG, "Message: ${e.message}")
            }
        }
    }

    override suspend fun swapPlayerProperty(ppId1: String, ppId2: String, playerId1: String, playerId2: String) {
        withContext(Dispatchers.IO) {
            try {
                db.runTransaction { transaction ->
                    transaction.update(playerPropertyRef.document(ppId1), "playerId", playerId2)
                    transaction.update(playerPropertyRef.document(ppId2), "playerId", playerId1)
                }
                    .await()
            } catch (e: Exception) {
                Log.d(TAG, "Message: ${e.message}")
            }
        }
    }

    override suspend fun deleteAllGamePlayerProperty(playerId: String) {
        withContext(Dispatchers.IO) {
            try {
                playerPropertyRef
                    .whereEqualTo("playerId", playerId)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            document.reference.delete()
                        }
                    }
            } catch(e: Exception) {
                Log.d(TAG, "Message: ${e.message}")
            }
        }
    }

    //Users
    override suspend fun insertUsername(email: String, username: String) {
        val data = hashMapOf(
            "username" to username
        )
        withContext(Dispatchers.IO) {
            try {
                userRef.document(email)
                    .set(data)
                    .await()
            } catch (e: Exception) {
                Log.d(TAG, "Message: ${e.message}")
            }
        }
    }

    override suspend fun getUsername(email: String): String {
        var username: String
        return withContext(Dispatchers.IO) {
            try {
                username = userRef.document(email)
                    .get()
                    .await()
                    .data?.get("username") as? String ?: "User not found"
                username
            } catch(e: Exception) {
                Log.d(TAG, "Message: ${e.message}")
                username = "error"
                username
            }
        }
    }
}

