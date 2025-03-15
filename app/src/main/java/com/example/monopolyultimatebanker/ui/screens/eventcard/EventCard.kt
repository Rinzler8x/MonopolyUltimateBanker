package com.example.monopolyultimatebanker.ui.screens.eventcard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
            modifier = modifier.padding(innerPadding).padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(40.dp)
        ) {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = eventState.title)
            }

            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = eventState.phrase,
                    textAlign = TextAlign.Center
                )
            }

            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Filled.Home,
                    contentDescription = "Icon",
                    modifier = modifier.size(100.dp)
                )
            }

            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = eventState.action,
                    textAlign = TextAlign.Center
                )
            }

            Button(
                onClick = {}
            ) {
                Text(
                    text = "Action"
                )
            }
        }
    }
}