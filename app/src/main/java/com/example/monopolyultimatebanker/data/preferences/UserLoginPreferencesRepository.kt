package com.example.monopolyultimatebanker.data.preferences

import android.content.ContentValues.TAG
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class UserLogin(
    val isLoggedIn: Boolean = false,
    val userName: String = "",
    val email: String = "",
)

class UserLoginPreferencesRepository @Inject constructor (
    private val dataStore: DataStore<Preferences>
){
    private companion object {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_NAME = stringPreferencesKey("user_name")
        val EMAIL = stringPreferencesKey("email")
    }

    val userLogin: Flow<UserLogin> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading UserLoginPreference.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            UserLogin(
                isLoggedIn = preferences[IS_LOGGED_IN] ?: true, //Set true to avoid login screen, **CHANGE BACK AFTER DEVELOPMENT**
                userName = preferences[USER_NAME] ?: "",
                email = preferences[EMAIL] ?: ""
            )
    }

    suspend fun saveUserLoginPreference(
        isLoggedIn: Boolean,
        userName: String,
        email: String,
    ) {
        dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = isLoggedIn
            preferences[USER_NAME] = userName
            preferences[EMAIL] = email
        }

    }
}