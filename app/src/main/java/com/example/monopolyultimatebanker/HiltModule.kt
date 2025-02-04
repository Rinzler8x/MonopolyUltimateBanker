package com.example.monopolyultimatebanker

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.monopolyultimatebanker.data.firebase.authentication.FirebaseAuthRepository
import com.example.monopolyultimatebanker.data.firebase.authentication.FirebaseAuthRepositoryImpl
import com.example.monopolyultimatebanker.data.firebase.database.FirestoreRepository
import com.example.monopolyultimatebanker.data.firebase.database.FirestoreRepositoryImpl
import com.example.monopolyultimatebanker.data.preferences.GamePreferencesRepository
import com.example.monopolyultimatebanker.data.preferences.UserLoginPreferencesRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UserLoginDataStore
private val Context.userLoginDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_login"
)

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GameStateDataStore
private val Context.gameStateDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "game_state"
)

@Module
@InstallIn(SingletonComponent::class)
object HiltModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return Firebase.auth
    }

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }

    @Provides
    @Singleton
    @UserLoginDataStore
    fun provideUserLoginDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.userLoginDataStore
    }

    @Provides
    @Singleton
    fun provideUserLoginPreferencesRepository(
        @UserLoginDataStore dataStore: DataStore<Preferences>
    ): UserLoginPreferencesRepository {
        return UserLoginPreferencesRepository(dataStore)
    }

    @Provides
    @Singleton
    @GameStateDataStore
    fun provideGameStateDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.gameStateDataStore
    }

    @Provides
    @Singleton
    fun provideGamePreferencesRepository(
        @GameStateDataStore dataStore: DataStore<Preferences>
    ): GamePreferencesRepository {
         return GamePreferencesRepository(dataStore)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindsFirebaseAuthRepository(firebaseAuthRepositoryImpl: FirebaseAuthRepositoryImpl): FirebaseAuthRepository

    @Binds
    @Singleton
    abstract fun bindsFirestoreRepository(firestoreRepositoryImpl: FirestoreRepositoryImpl): FirestoreRepository
}



