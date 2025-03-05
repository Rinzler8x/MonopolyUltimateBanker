package com.example.monopolyultimatebanker.data.preferences

import android.content.ContentValues.TAG
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class QrPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private companion object{
        val QR_CODE = stringPreferencesKey("qr_code")
    }

    val qrState: Flow<String> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading UserLoginPreference.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { pref ->
            pref[QR_CODE] ?: ""
        }

    suspend fun saveQrPreference( qrCode: String ) {
        dataStore.edit { pref ->
            pref[QR_CODE] = qrCode
        }
    }

    suspend fun resetQrPreference() {
        dataStore.edit {
            it.clear()
        }
    }
}