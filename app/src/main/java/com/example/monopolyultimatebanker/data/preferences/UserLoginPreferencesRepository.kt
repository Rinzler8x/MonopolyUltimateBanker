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
import com.example.monopolyultimatebanker.ui.screens.signupandlogin.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class UserLoginPreferencesRepository (
    private val dataStore: DataStore<Preferences>
){
    private companion object {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_NAME = stringPreferencesKey("user_name")
        val EMAIL = stringPreferencesKey("email")
        val PASSWORD = stringPreferencesKey("password")
    }

    val userLogin: Flow<UiState> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading UserLoginPreference.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
                UiState(
                    userName = preferences[USER_NAME]?: "",
                    email = preferences[EMAIL]?: "",
                    password = preferences[PASSWORD]?: "",
                    notEmpty = false,
                )
    }

    suspend fun saveUserLoginPreference(
        isLoggedIn: Boolean,
        userName: String,
        email: String,
        password: String,
    ) {
        dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = isLoggedIn
            preferences[USER_NAME] = userName
            preferences[EMAIL] = email
            preferences[PASSWORD] = password
        }

    }
}