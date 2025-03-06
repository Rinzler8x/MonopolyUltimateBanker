package com.example.monopolyultimatebanker.ui.screens.propertycard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monopolyultimatebanker.data.preferences.QrPreferencesRepository
import com.example.monopolyultimatebanker.data.preferences.QrType
import com.example.monopolyultimatebanker.data.propertytable.Property
import com.example.monopolyultimatebanker.data.propertytable.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class PropertyCardViewModel @Inject constructor(
    private val qrPreferencesRepository: QrPreferencesRepository,
    private val propertyRepository: PropertyRepository
): ViewModel() {


    val qrPrefState: StateFlow<QrType> =
        qrPreferencesRepository.qrState.map {
            QrType(property = it.property, event = it.event)
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = QrType(property = "monopro_01", event = "monoeve_01")
            )

    @OptIn(ExperimentalCoroutinesApi::class)
    val propertyState: StateFlow<Property> = qrPrefState
        .flatMapLatest {
            propertyRepository.getPropertyStream(it.property)
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = Property()
            )
}