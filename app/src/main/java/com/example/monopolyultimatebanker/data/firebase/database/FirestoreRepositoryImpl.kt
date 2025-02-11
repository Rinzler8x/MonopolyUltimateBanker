package com.example.monopolyultimatebanker.data.firebase.database

import android.content.ContentValues.TAG
import android.util.Log
import com.example.monopolyultimatebanker.data.gametable.Game
import com.example.monopolyultimatebanker.data.playerpropertytable.PlayerProperty
import com.example.monopolyultimatebanker.data.preferences.GamePreferencesRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
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
    private val gamePreferencesRepository: GamePreferencesRepository
): FirestoreRepository {

    private val gameRef = db.collection("games")
    private val playerPropertyRef = db.collection("player_properties")
    private val gameScope = CoroutineScope(Dispatchers.IO)

    //games collection functions

//    override val game: Flow<List<Game>>
//        get() = flow {
//            gameScope.launch {
//                gameRef.whereEqualTo("game_id", "bro")
//                    .addSnapshotListener{ snapshot, e ->
//                        if (e != null) {
//                            Log.w(TAG, "listen:error", e)
//                            return@addSnapshotListener
//                        }
//
//                        for(dc in snapshot!!.documentChanges) {
//                            when(dc.type) {
//                                DocumentChange.Type.ADDED -> Log.d(TAG, "Game document: ${dc.document.data}")
//                                DocumentChange.Type.MODIFIED -> Log.d(TAG, "Modified game document: ${dc.document.data}")
//                                DocumentChange.Type.REMOVED -> Log.d(TAG, "Removed game: ${dc.document.data}")
//                            }
//                        }
//                    }
//            }
//        }

    override fun getGame() = gameRef.whereEqualTo("gameId", "bruh").snapshots().map { it.toObjects<FirestoreGame>() }

    override suspend fun insertGamePlayer(gamePlayer: Game) {
//        val data = hashMapOf(
//            "game_id" to "bruhbruh",
//            "player_balance" to gamePlayer.playerBalance,
//            "player_name" to gamePlayer.playerName
//        )
        val data = FirestoreGame(
            gameId = "bruh",
            playerName = gamePlayer.playerName,
            playerBalance = gamePlayer.playerBalance
        )
        withContext(Dispatchers.IO) {
            try {
                gameRef
                    .add(data)
                    .addOnSuccessListener { docRef ->
                        Log.d(TAG, "Message: ${docRef.id}")
                    }
                    .await()

            } catch(e: Exception) {
                Log.d(TAG, "Message: ${e.message}")
            }
        }
    }

    override suspend fun updateGamePlayer(gamePlayer: Game) {
        withContext(Dispatchers.IO) {
            try {
                gameRef.document("r5Oo7sd2ysLkAS449F1M")
                    .update(
                        "game_id","bruhbruh",
                        "player_name", gamePlayer.playerName,
                        "player_balance", gamePlayer.playerBalance
                    )
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

    //player_properties collection function

//    override val playerProperties: Flow<List<PlayerProperty>>
//        get() = flow {
//            gameScope.launch {
//                playerPropertyRef.whereEqualTo("game_id", "bro")
//                    .addSnapshotListener{ snapshot, e ->
//                        if (e != null) {
//                            Log.w(TAG, "listen:error", e)
//                            return@addSnapshotListener
//                        }
//
//                        for(dc in snapshot!!.documentChanges) {
//                            when(dc.type) {
//                                DocumentChange.Type.ADDED -> Log.d(TAG, "PlayerProperty document: ${dc.document.data}")
//                                DocumentChange.Type.MODIFIED -> Log.d(TAG, "Modified PlayerProperty document: ${dc.document.data}")
//                                DocumentChange.Type.REMOVED -> Log.d(TAG, "Removed PlayerProperty: ${dc.document.data}")
//                            }
//                        }
//                    }
//            }
//        }


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
}