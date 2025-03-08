package com.example.monopolyultimatebanker.ui.screens.eventcard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.monopolyultimatebanker.ui.navigation.NavigationDestination

object EventCardDestination: NavigationDestination {
    override val route = "event_card"
}

@Composable
fun EventCard(
    modifier: Modifier = Modifier,
    eventCardViewModel: EventCardViewModel = hiltViewModel()
) {

    val eventState by eventCardViewModel.eventState.collectAsStateWithLifecycle()
    val qrPrefState by eventCardViewModel.qrPrefState.collectAsStateWithLifecycle()

    Scaffold { innerPadding ->

        Column(
            modifier = modifier.padding(innerPadding)
        ) {
            Text(text = eventState.title)
        }
    }
}