package com.example.monopolyultimatebanker.data.firebase.database

import android.content.ContentValues.TAG
import android.util.Log
import com.example.monopolyultimatebanker.data.gametable.Game
import com.example.monopolyultimatebanker.data.gametable.GameRepositoryImpl
import com.example.monopolyultimatebanker.data.playerpropertytable.PlayerProperty
import com.example.monopolyultimatebanker.data.preferences.GamePreferencesRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class FirestoreGame(
    val gameId: String = "",
    val playerName: String = "",
    val playerBalance: Int = 1500
)
class FirestoreRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val gamePreferencesRepository: GamePreferencesRepository,
    private val gameRepositoryImpl: GameRepositoryImpl
): FirestoreRepository {

    private val gameRef = db.collection("games")
    private val playerPropertyRef = db.collection("player_properties")
    private val userRef = db.collection("users")
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun getGame(gameId: String) = gameRef.whereEqualTo("gameId", gameId).snapshots().map { it ->
        val temp = it.toObjects<FirestoreGame>()
        withContext(Dispatchers.IO) {
            it.map {
                for(i in temp) {
                    if(gameRepositoryImpl.gamePlayerExists(i.playerName).count == 0){
                        gameRepositoryImpl.gameInsert(Game(it.id, i.playerName, i.playerBalance))
                    } else {
                        gameRepositoryImpl.updatePlayerState(i.playerBalance, i.playerName)
                    }
                }
            }
        }
        it.toObjects<FirestoreGame>()
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
        var playerId: String = ""
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
                    .update("player_balance", playerBalance)
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

    override suspend fun insertPlayerProperty(playerProperty: PlayerProperty) {
        withContext(Dispatchers.IO) {
            try {
                playerPropertyRef
                    .add(playerProperty)
                    .await()

            } catch(e: Exception) {
                Log.d(TAG, "Message: ${e.message}")
            }
        }
    }

    override suspend fun updatePlayerProperty(playerProperty: PlayerProperty) {
        withContext(Dispatchers.IO) {
            try {
                playerPropertyRef.document(playerProperty.playerId)
                    .update(
                        "game_id", "bro",
                        "player_if", playerProperty.playerId,
                        "ppid", playerProperty.ppId,
                        "property_no", playerProperty.propertyNo,
                        "rent_level", playerProperty.rentLevel
                    )
                    .await()

            } catch(e: Exception) {
                Log.d(TAG, "Message: ${e.message}")
            }
        }
    }

    override suspend fun deleteAllGamePlayerProperty(playerId: String) {
        withContext(Dispatchers.IO) {
            try {
                playerPropertyRef
                    .whereEqualTo("player_id", playerId)
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

