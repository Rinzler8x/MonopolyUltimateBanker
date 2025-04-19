package com.example.monopolyultimatebanker.ui.screens.eventcard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val gameState by eventCardViewModel.gameState.collectAsStateWithLifecycle()
    val playerBottomSheetState = eventCardViewModel.playerBottomSheetState
    val sheetState = rememberModalBottomSheetState()
    val propertyDialogState = eventCardViewModel.propertyDialogState
    val actionUserInput = eventCardViewModel.actionUserInput
    val resultsUiState by eventCardViewModel.uiResults.collectAsStateWithLifecycle()

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
                onClickAction = eventCardViewModel::onClickActionCheckUserInputRequired
            )

            if (playerBottomSheetState.showBottomSheet) {
                PlayerBottomSheet(
                    onClickPlayerBottomSheet = eventCardViewModel::onCLickPlayerBottomSheet,
                    onClickPropertyDialog = eventCardViewModel::onClickPropertyDialog1,
                    updatePlayerName = eventCardViewModel::updatePlayerId,
                    sheetState = sheetState,
                    innerPadding = innerPadding,
                    game = gameState.gameState
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
                    properties = resultsUiState.updatedProperties,
                    noPropertiesUpdated = resultsUiState.noPropertiesUpdated,
                    navigateToHomeScreen = navigateToHomeScreen,
                    clearResults = eventCardViewModel::clearResults
                )
            }
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
                        .padding(vertical = 10.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Player")
                    Text(text = "Balance")
                }
            }
            items(items = game.sortedByDescending { it.playerBalance }, key = { it.playerId }) {
                TextButton(
                    onClick = {
                        updatePlayerName(it.playerId)
                        onClickPlayerBottomSheet()
                        onClickPropertyDialog()
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
        title = { Text(text = "Property No.") },
        text = {
            Column {
                Text(
                    text = description,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                )
                Spacer(modifier = modifier.padding(vertical = 8.dp))
                OutlinedTextField(
                    value = propertyNo,
                    onValueChange = updatePropertyNo,
                    label = { Text(text = "Property No") },
                    visualTransformation = VisualTransformation.None,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done),
                    supportingText = {
                        if(propertyNo.isNotBlank()){
                            if(propertyNo.toInt() !in 1..22) {
                                Text(
                                    text = "Must be between 1 to 22.",
                                    modifier = modifier.padding(bottom =  4.dp)
                                )
                            }
                        }
                    },
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
                Text(text = stringResource(R.string.dismiss))
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
                Text(text = stringResource(R.string.confirm))
            }
        }
    )
}

@Composable
private fun ResultDialog(
    onClickResultDialog: () -> Unit,
    properties: List<UpdatedProperty>,
    noPropertiesUpdated: String,
    clearResults: () -> Unit,
    navigateToHomeScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = {
            onClickResultDialog()
        },
        title = { Text(text = "Updated Properties") },
        text = {
            LazyColumn {
                if(properties.isNotEmpty()) {
                    item {
                        Row(
                            modifier = modifier.fillMaxWidth()
                        ) {
                            Text("Property No.")
                            Text("Rent Level")
                        }
                    }
                    items(properties, key = { it.propertyNo }) { property ->
                        Row {
                            Text(text = "${property.propertyNo}")
                            Text(text = "${property.rentLevel}")
                        }
                    }
                } else {
                    item {
                        Row(
                            horizontalArrangement = Arrangement.Start,
                            modifier = modifier.fillMaxWidth()
                        ) {
                            Text(text = noPropertiesUpdated)
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
                Text(text = stringResource(R.string.confirm))
            }
        }
    )
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