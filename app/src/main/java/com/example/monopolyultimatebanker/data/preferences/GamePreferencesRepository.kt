package com.example.monopolyultimatebanker.data.preferences

import android.content.ContentValues.TAG
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class GamePrefState(
    val gameId: String = "",
    val playerId: String = "",
    val isGameActive: Boolean = false,
    val gameOverCount: Int = 0
)

class GamePreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
){

    private companion object {
        val GAME_ID = stringPreferencesKey("game_id")
        val PLAYER_ID = stringPreferencesKey("player_id")
        val IS_GAME_ACTIVE = booleanPreferencesKey("is_game_active")
        val GAME_OVER_COUNT = intPreferencesKey("game_over_count")
    }

    val gameState: Flow<GamePrefState> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading GamePreference.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { pref ->
            GamePrefState(
                gameId = pref[GAME_ID] ?: "",
                playerId = pref[PLAYER_ID] ?: "",
                isGameActive = pref[IS_GAME_ACTIVE] ?: false, //Set true to avoid create game screen, **CHANGE BACK AFTER DEVELOPMENT**
                gameOverCount = pref[GAME_OVER_COUNT] ?: 0,
            )
        }

    suspend fun saveGamePreference(
        gameId: String,
        playerId: String,
        isGameActive: Boolean
    ) {
        dataStore.edit { pref ->
            pref[GAME_ID] = gameId
            pref[PLAYER_ID] = playerId
            pref[IS_GAME_ACTIVE] = isGameActive
        }
    }

    suspend fun gameOverCountUpdate(countValue: Int) {
        dataStore.edit { pref ->
            pref[GAME_OVER_COUNT] = countValue
        }
    }

    suspend fun resetGamePreference() {
        dataStore.edit {
            it.clear()
        }
    }

}