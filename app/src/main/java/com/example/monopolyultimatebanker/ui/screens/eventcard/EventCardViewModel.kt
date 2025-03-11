package com.example.monopolyultimatebanker.ui.screens.eventcard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monopolyultimatebanker.data.eventtable.Event
import com.example.monopolyultimatebanker.data.eventtable.EventRepositoryImpl
import com.example.monopolyultimatebanker.data.preferences.QrPreferencesRepository
import com.example.monopolyultimatebanker.data.preferences.QrType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class EventCardViewModel @Inject constructor(
    private val qrPreferencesRepository: QrPreferencesRepository,
    private val eventRepositoryImpl: EventRepositoryImpl
): ViewModel() {

    val qrPrefState: StateFlow<QrType> =
        qrPreferencesRepository.qrState.map {
            QrType(property = it.property, event = it.event)
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = QrType()
            )

    @OptIn(ExperimentalCoroutinesApi::class)
    val eventState: StateFlow<Event> = qrPrefState
        .flatMapLatest {
            eventRepositoryImpl.getEventStream(it.event)
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = Event()
            )
}