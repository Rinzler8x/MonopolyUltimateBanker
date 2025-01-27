package com.example.monopolyultimatebanker

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.monopolyultimatebanker.data.firebase.FirebaseAuthRepository
import com.example.monopolyultimatebanker.data.firebase.FirebaseAuthRepositoryImpl
import com.example.monopolyultimatebanker.data.preferences.UserLoginPreferencesRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
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

}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindsFirebaseAuthRepository(firebaseAuthRepositoryImpl: FirebaseAuthRepositoryImpl): FirebaseAuthRepository
}



