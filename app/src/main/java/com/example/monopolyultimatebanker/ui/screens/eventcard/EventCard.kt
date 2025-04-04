package com.example.monopolyultimatebanker.ui.screens.eventcard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.monopolyultimatebanker.data.gametable.Game
import com.example.monopolyultimatebanker.ui.navigation.NavigationDestination
import com.example.monopolyultimatebanker.ui.screens.home.GameState
import kotlinx.coroutines.CoroutineScope

object EventCardDestination: NavigationDestination {
    override val route = "event_card"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventCard(
    modifier: Modifier = Modifier,
    eventCardViewModel: EventCardViewModel = hiltViewModel()
) {

    val eventState by eventCardViewModel.eventState.collectAsStateWithLifecycle()
    val qrPrefState by eventCardViewModel.qrPrefState.collectAsStateWithLifecycle()
    val gameState by eventCardViewModel.gameState.collectAsStateWithLifecycle()
    val playerBottomSheetState = eventCardViewModel.playerBottomSheetState
    val sheetState = rememberModalBottomSheetState()

    Scaffold { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(40.dp)
        ) {
            EventCardContent(
                title = eventState.title,
                phrase = eventState.phrase,
                action = eventState.action,
                onClickAction = eventCardViewModel::onCLickPlayerBottomSheet
            )

            if (playerBottomSheetState.showBottomSheet) {
                PlayerBottomSheet(
                    onClickPlayerBottomSheet = eventCardViewModel::onCLickPlayerBottomSheet,
                    sheetState = sheetState,
                    innerPadding = innerPadding,
                    game = gameState.gameState
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerBottomSheet(
    onClickPlayerBottomSheet: () -> Unit,
    sheetState: SheetState,
    innerPadding: PaddingValues,
    game: List<Game>,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = {
            onClickPlayerBottomSheet()
        },
        sheetState = sheetState,
        modifier = modifier.fillMaxHeight().padding(innerPadding)
    ) {
        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
//                .padding(horizontal = 50.dp),
        ) {
            item {
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Player")
                    Text(text = "Balance")
                }
            }
            items(items = game.sortedByDescending { it.playerBalance }, key = { it.playerId }) {
//                Row(
//                    modifier = modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 10.dp),
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    Text(text = it.playerName)
//                    Text(text = it.playerBalance.toString())
//                }
//
                TextButton(
                    onClick = {
                        println(it.playerName)
                    },
                ) {
                    Row(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = it.playerName,
                            fontSize = 20.sp,
                        )
                        Text(text = it.playerBalance.toString(), fontSize = 20.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun EventCardContent(
    title: String,
    phrase: String,
    action: String,
    onClickAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = title)
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = phrase,
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
            text = action,
            textAlign = TextAlign.Center
        )
    }

    Button(
        onClick = onClickAction
    ) {
        Text(
            text = "Action"
        )
    }
}