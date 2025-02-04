package com.example.monopolyultimatebanker.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monopolyultimatebanker.data.firebase.database.FirestoreRepositoryImpl
import com.example.monopolyultimatebanker.data.gametable.Game
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firestoreRepositoryImpl: FirestoreRepositoryImpl
): ViewModel() {

    fun insertDataTest() {
        val game = Game("1", "Jason", 50)
        viewModelScope.launch {
            firestoreRepositoryImpl.updateGamePlayer(game)
        }
    }
}