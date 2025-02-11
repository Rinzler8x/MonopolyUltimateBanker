package com.example.monopolyultimatebanker.ui.screens.home

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monopolyultimatebanker.data.firebase.database.FirestoreRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firestoreRepositoryImpl: FirestoreRepositoryImpl
): ViewModel() {

    var navDrawerState by mutableStateOf(DrawerState(initialValue = DrawerValue.Closed))
        private set


    fun onClickNavIcon(compositionCoroutineContext: CoroutineContext) {
        viewModelScope.launch {
            withContext(compositionCoroutineContext) {
                navDrawerState.apply {
                    if (isClosed) open() else close()
                }
            }
        }
    }
}