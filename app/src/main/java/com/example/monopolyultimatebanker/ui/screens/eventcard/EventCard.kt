package com.example.monopolyultimatebanker.ui.screens.eventcard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.monopolyultimatebanker.R
import com.example.monopolyultimatebanker.data.firebase.database.UpdatedProperty
import com.example.monopolyultimatebanker.data.gametable.Game
import com.example.monopolyultimatebanker.ui.navigation.NavigationDestination

object EventCardDestination: NavigationDestination {
    override val route = "event_card"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventCard(
    navigateToHomeScreen: () -> Unit,
    modifier: Modifier = Modifier,
    eventCardViewModel: EventCardViewModel = hiltViewModel()
) {

    val eventState by eventCardViewModel.eventState.collectAsStateWithLifecycle()
    val qrPrefState by eventCardViewModel.qrPrefState.collectAsStateWithLifecycle()
    val gamePrefState by eventCardViewModel.gamePrefState.collectAsStateWithLifecycle()
    val gameState by eventCardViewModel.gameState.collectAsStateWithLifecycle()
    val playerBottomSheetState by eventCardViewModel.uiPlayerBottomSheet.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState()
    val propertyDialogState by eventCardViewModel.uiPropertyDialog.collectAsStateWithLifecycle()
    val actionUserInput = eventCardViewModel.actionUserInput
    val resultsUiState by eventCardViewModel.uiResults.collectAsStateWithLifecycle()

    Scaffold { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            EventCardContent(
                eventId = eventState.eventId,
                title = eventState.title,
                phrase = eventState.phrase,
                action = eventState.action,
                onClickAction = eventCardViewModel::onClickActionCheckUserInputRequired,
                navigateToHomeScreen = navigateToHomeScreen,
            )

            if (playerBottomSheetState.showBottomSheet) {
                PlayerBottomSheet(
                    onClickPlayerBottomSheet = eventCardViewModel::onCLickPlayerBottomSheet,
                    onClickPropertyDialog = eventCardViewModel::onClickPropertyDialog1,
                    updatePlayerName = eventCardViewModel::updatePlayerId,
                    sheetState = sheetState,
                    innerPadding = innerPadding,
                    game = gameState.gameState,
                    playerId = gamePrefState.playerId
                )
            }

            if (propertyDialogState.propertyDialogState1) {
                PropertyDialog(
                    propertyDialog1 = propertyDialogState.propertyDialogState1,
                    onClickPropertyDialog1 = eventCardViewModel::onClickPropertyDialog1,
                    onClickPropertyDialog2 = eventCardViewModel::onClickPropertyDialog2,
                    onClickAction = eventCardViewModel::onClickAction,
                    propertyNo = actionUserInput.propertyNo1,
                    updatePropertyNo = eventCardViewModel::updatePropertyNo1,
                    doubleInput = propertyDialogState.doubleInput,
                    onClickDoubleInput = eventCardViewModel::onClickDoubleInput,
                    description = "Enter first property no.",
                )
            }

            if (propertyDialogState.propertyDialogState2) {
                PropertyDialog(
                    propertyDialog1 = propertyDialogState.propertyDialogState1,
                    onClickPropertyDialog1 = eventCardViewModel::onClickPropertyDialog1,
                    onClickPropertyDialog2 = eventCardViewModel::onClickPropertyDialog2,
                    onClickAction = eventCardViewModel::onClickAction,
                    propertyNo = actionUserInput.propertyNo2,
                    updatePropertyNo = eventCardViewModel::updatePropertyNo2,
                    doubleInput = propertyDialogState.doubleInput,
                    onClickDoubleInput = eventCardViewModel::onClickDoubleInput,
                    description = "Enter second property no.",
                )
            }

            if(propertyDialogState.resultDialogState) {
                ResultDialog(
                    onClickResultDialog = eventCardViewModel::onClickResultDialog,
                    isLoading = propertyDialogState.isLoading,
                    properties = resultsUiState.updatedProperties,
                    noPropertiesUpdated = resultsUiState.noPropertiesUpdated,
                    navigateToHomeScreen = navigateToHomeScreen,
                    clearResults = eventCardViewModel::clearResults
                )
            }

            if(propertyDialogState.wrongPropertyInputDialogState) {
                WrongPropertyInputDialog(
                    onClickWrongPropertyInputDialog = eventCardViewModel::onClickWrongPropertyInputDialog,
                )
            }
        }
    }
}

@Composable
private fun EventCardContent(
    eventId: Int,
    title: String,
    phrase: String,
    action: String,
    navigateToHomeScreen: () -> Unit,
    onClickAction: (() -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorStops = arrayOf(
        0.0f to Color(0xFF0B4F80),
        0.4f to Color.White
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = Color.Black,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(10.dp)
            .background(Brush.verticalGradient(colorStops = colorStops)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(10.dp)
                .shadow(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Yellow)
                .padding(top = 22.dp, bottom = 14.dp, start = 12.dp, end = 12.dp)

        ) {
            Text(
                text = title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displaySmall,
                modifier = modifier.weight(1f)
            )
        }

        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = phrase,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(
            modifier = modifier
                .padding(vertical = 10.dp)
        )

        Row(
            modifier = modifier
                .fillMaxWidth()
            ,
            horizontalArrangement = Arrangement.Center
        ) {
            val drawableId: Int = when(eventId) {
                1, 9 -> R.drawable.monoeve_1_9_icon
                2, 13, 18 -> R.drawable.monoeve_2_13_18_icon
                3 -> R.drawable.monoeve_3_icon
                4 -> R.drawable.monoeve_4_icon
                5 -> R.drawable.monoeve_5_icon
                6, 7, 19 -> R.drawable.monoeve_6_7_19_icon
                8 -> R.drawable.monoeve_8_icon
                10 -> R.drawable.monoeve_10_icon
                11 -> R.drawable.monoeve_11_icon
                12 -> R.drawable.monoeve_12_icon
                14 -> R.drawable.monoeve_14_icon
                15, 21 -> R.drawable.monoeve_15_21_icon
                16, 17 -> R.drawable.monoeve_16_17_icon
                20 -> R.drawable.monoeve_20_icon
                22 -> R.drawable.monoeve_22_icon
                else -> R.drawable.monoeve_12_icon
            }

            Image(
                painter = painterResource(id = drawableId),
                contentDescription = stringResource(R.string.event_card_icon),
                contentScale = ContentScale.FillWidth,
                modifier = modifier
                    .weight(1f)
            )
        }

        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 0.dp, bottom = 14.dp, start = 10.dp, end = 10.dp)
            ,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = action,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
            )
        }

    }
    Row {
        Button(
            onClick = { onClickAction(navigateToHomeScreen) },
            shape = MaterialTheme.shapes.medium,
            modifier = modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Action",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerBottomSheet(
    onClickPlayerBottomSheet: () -> Unit,
    onClickPropertyDialog: () -> Unit,
    updatePlayerName: (String) -> Unit,
    sheetState: SheetState,
    innerPadding: PaddingValues,
    game: List<Game>,
    playerId: String,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = {
            onClickPlayerBottomSheet()
        },
        sheetState = sheetState,
        modifier = modifier
            .fillMaxHeight()
            .padding(innerPadding)
    ) {
        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
        ) {
            item {
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Player", style = MaterialTheme.typography.headlineSmall)
                    Text(text = "Balance", style = MaterialTheme.typography.headlineSmall)
                }
                HorizontalDivider(thickness = 2.dp, modifier = modifier.padding(horizontal = 12.dp))
            }
            items(items = game.sortedByDescending { it.playerBalance }, key = { it.playerId }) { player ->
                if(player.playerId != playerId) {
                    TextButton(
                        onClick = {
                            updatePlayerName(player.playerId)
                            onClickPlayerBottomSheet()
                            onClickPropertyDialog()
                        },
                    ) {
                        Row(
                            modifier = modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp, horizontal = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = player.playerName,
                                style = MaterialTheme.typography.titleLarge
                            )

                            Text(
                                text = "${player.playerBalance}",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PropertyDialog(
    propertyDialog1: Boolean,
    onClickPropertyDialog1: () -> Unit,
    onClickPropertyDialog2: () -> Unit,
    onClickAction: () -> Unit,
    propertyNo: String,
    updatePropertyNo: (String) -> Unit,
    doubleInput: Boolean,
    onClickDoubleInput: () -> Unit,
    description: String,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = {
            updatePropertyNo("")
            onClickPropertyDialog1()
        },
        title = { Text(text = "Property No.", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column {
                Text(text = description, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = modifier.padding(vertical = 8.dp))
                OutlinedTextField(
                    value = propertyNo,
                    onValueChange = updatePropertyNo,
                    label = { Text(text = "Property No", style = MaterialTheme.typography.bodyLarge) },
                    visualTransformation = VisualTransformation.None,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done),
                    supportingText = {
                        if(propertyNo.isNotBlank()){
                            if(propertyNo.toInt() !in 1..22) {
                                Text(
                                    text = "Must be between 1 to 22.",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = modifier.padding(bottom =  4.dp)
                                )
                            }
                        }
                    },
                    textStyle = MaterialTheme.typography.bodyLarge,
                    isError = (propertyNo.isNotBlank() && (propertyNo.toInt() !in 1..22))
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    updatePropertyNo("")
                    onClickPropertyDialog1()
                }
            ) {
                Text(
                    text = stringResource(R.string.dismiss),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if(doubleInput) {
                        onClickPropertyDialog1()
                        onClickPropertyDialog2()
                        onClickDoubleInput()
                    } else {
                        if(propertyDialog1) {
                            onClickPropertyDialog1()
                        } else {
                            onClickPropertyDialog2()
                        }
                        onClickAction()
                    }
                },
                enabled = (propertyNo.isNotBlank() && (propertyNo.toInt() in 1..22))
            ) {
                Text(
                    text = stringResource(R.string.confirm),
                    style = MaterialTheme.typography.bodyLarge

                )
            }
        }
    )
}

@Composable
private fun ResultDialog(
    onClickResultDialog: () -> Unit,
    isLoading: Boolean,
    properties: List<UpdatedProperty>,
    noPropertiesUpdated: String,
    clearResults: () -> Unit,
    navigateToHomeScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = {
            onClickResultDialog()
            navigateToHomeScreen()
        },
        title = { Text(text = "Updated Properties", style = MaterialTheme.typography.headlineSmall) },
        text = {
            LazyColumn {
                if(isLoading) {
                    item { Row(
                        modifier = modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) { CircularProgressIndicator() } }
                } else {
                    if(properties.isNotEmpty()) {
                        item {
                            Row(modifier = modifier
                                .fillMaxWidth()
                                .padding(horizontal = 2.dp, vertical = 4.dp)) {
                                Column(modifier = modifier.weight(0.5f)) {
                                    Text(text = "Property No.", style = MaterialTheme.typography.titleMedium)
                                }
                                Column(modifier = modifier.weight(0.5f)) {
                                    Text(text = "Rent Level",  style = MaterialTheme.typography.titleMedium)
                                }
                            }
                        }
                        items(properties.sortedBy { it.propertyNo }, key = { it.propertyNo }) { property ->
                            Row(
                                modifier = modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp, vertical = 6.dp)
                            ) {
                                Column(modifier = modifier.weight(0.5f)) {
                                    Text(text = "${property.propertyNo}", style = MaterialTheme.typography.bodyLarge)
                                }
                                Column(modifier = modifier
                                    .weight(0.5f)
                                    .padding(horizontal = 4.dp)) {
                                    Text(text = "${property.rentLevel}", style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                        }
                    } else {
                        item {
                            Row(
                                horizontalArrangement = Arrangement.Start,
                                modifier = modifier.fillMaxWidth()
                            ) {
                                Text(text = noPropertiesUpdated, style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onClickResultDialog()
                    navigateToHomeScreen()
                    clearResults()
                },
            ) {
                Text(text = stringResource(R.string.confirm), style = MaterialTheme.typography.bodyLarge)
            }
        }
    )
}

@Composable
private fun WrongPropertyInputDialog(
    onClickWrongPropertyInputDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = {
            onClickWrongPropertyInputDialog()
        },
        title = { Text(text = "Invalid Input", style = MaterialTheme.typography.headlineSmall) },
        text = { Text(text = "One of property no. provided doesn't belong to the right player." +
                "\nPlease retry again.", style = MaterialTheme.typography.titleSmall) },
        confirmButton = {
            TextButton(
                onClick = {
                    onClickWrongPropertyInputDialog()
                },
            ) {
                Text(text = stringResource(R.string.confirm), style = MaterialTheme.typography.bodyLarge)
            }
        }
    )
}
