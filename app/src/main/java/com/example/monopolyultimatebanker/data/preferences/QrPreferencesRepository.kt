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

data class QrType(
    val property: String = "monopro_1",
    val event: String = "monoeve_1"
)

class QrPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private companion object{
        val QR_CODE_PRO = stringPreferencesKey("qr_code_pro")
        val QR_CODE_EVE = stringPreferencesKey("qr_code_eve")
    }

    val qrState: Flow<QrType> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading UserLoginPreference.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { pref ->
            QrType(
                property = pref[QR_CODE_PRO] ?: "",
                event = pref[QR_CODE_EVE] ?: ""
            )
        }

    suspend fun saveProQrPreference(qrCode: String ) {
        dataStore.edit { pref ->
            pref[QR_CODE_PRO] = qrCode
        }
    }

    suspend fun saveEveQrPreference(qrCode: String) {
        dataStore.edit { pref ->
            pref[QR_CODE_EVE] = qrCode
        }
    }

    suspend fun resetQrPreference() {
        dataStore.edit {
            it.clear()
        }
    }
}