package com.example.monopolyultimatebanker

import android.content.Context
import android.provider.ContactsContract.Data
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.monopolyultimatebanker.data.eventtable.EventDao
import com.example.monopolyultimatebanker.data.eventtable.EventRepository
import com.example.monopolyultimatebanker.data.eventtable.EventRepositoryImpl
import com.example.monopolyultimatebanker.data.firebase.authentication.FirebaseAuthRepository
import com.example.monopolyultimatebanker.data.firebase.authentication.FirebaseAuthRepositoryImpl
import com.example.monopolyultimatebanker.data.firebase.database.FirestoreRepository
import com.example.monopolyultimatebanker.data.firebase.database.FirestoreRepositoryImpl
import com.example.monopolyultimatebanker.data.gametable.GameDao
import com.example.monopolyultimatebanker.data.gametable.GameRepository
import com.example.monopolyultimatebanker.data.gametable.GameRepositoryImpl
import com.example.monopolyultimatebanker.data.playerpropertytable.PlayerPropertyDao
import com.example.monopolyultimatebanker.data.playerpropertytable.PlayerPropertyRepository
import com.example.monopolyultimatebanker.data.playerpropertytable.PlayerPropertyRepositoryImpl
import com.example.monopolyultimatebanker.data.preferences.GamePreferencesRepository
import com.example.monopolyultimatebanker.data.preferences.QrPreferencesRepository
import com.example.monopolyultimatebanker.data.preferences.UserLoginPreferencesRepository
import com.example.monopolyultimatebanker.data.propertytable.PropertyDao
import com.example.monopolyultimatebanker.data.propertytable.PropertyRepository
import com.example.monopolyultimatebanker.data.propertytable.PropertyRepositoryImpl
import com.example.monopolyultimatebanker.data.room.MonopolyDatabase
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

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class QrDataStore
private val Context.qrDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "qr_preference"
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

    @Provides
    @Singleton
    @QrDataStore
    fun provideQrDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.qrDataStore
    }

    @Provides
    @Singleton
    fun provideQrPreferencesRepository (
        @QrDataStore dataStore: DataStore<Preferences>
    ): QrPreferencesRepository {
        return QrPreferencesRepository(dataStore)
    }

    @Provides
    @Singleton
    fun provideMonopolyDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, MonopolyDatabase::class.java, "monoploy_database")
//            .fallbackToDestructiveMigration()
            .createFromAsset("database/monopoly_prepopulate.db")
            .build()

    @Provides
    @Singleton
    fun providePropertyDao(database: MonopolyDatabase): PropertyDao {
        return database.propertyDao()
    }

    @Provides
    @Singleton
    fun provideEventDao(database: MonopolyDatabase): EventDao {
        return database.eventDao()
    }

    @Provides
    @Singleton
    fun provideGameDao(database: MonopolyDatabase): GameDao {
        return database.gameDao()
    }

    @Provides
    @Singleton
    fun providePlayerPropertyDao(database: MonopolyDatabase): PlayerPropertyDao {
        return database.playerPropertyDao()
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

    @Binds
    @Singleton
    abstract fun bindsPropertyRepository(propertyRepositoryImpl: PropertyRepositoryImpl): PropertyRepository

    @Binds
    @Singleton
    abstract fun bindsEventRepository(eventRepositoryImpl: EventRepositoryImpl): EventRepository

    @Binds
    @Singleton
    abstract fun bindsGameRepository(gameRepositoryImpl: GameRepositoryImpl): GameRepository

    @Binds
    @Singleton
    abstract fun bindsPlayerPropertyRepository(playerPropertyRepositoryImpl: PlayerPropertyRepositoryImpl): PlayerPropertyRepository

}