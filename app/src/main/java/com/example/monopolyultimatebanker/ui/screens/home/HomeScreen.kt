package com.example.monopolyultimatebanker.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.monopolyultimatebanker.R
import com.example.monopolyultimatebanker.data.gametable.Game
import com.example.monopolyultimatebanker.data.playerpropertytable.PlayerPropertiesList
import com.example.monopolyultimatebanker.ui.navigation.NavigationDestination
import com.example.monopolyultimatebanker.utils.ObserverAsEvents
import com.example.monopolyultimatebanker.utils.SnackbarController
import kotlinx.coroutines.launch

object HomeDestination: NavigationDestination {
    override val route = "home"
}

@Composable
fun HomeScreen(
    navigateToQrCodeScanner: () -> Unit,
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val newGameDialogState = homeViewModel.newGameDialogState
    val gamePrefState by homeViewModel.gamePreferenceState.collectAsStateWithLifecycle()
    val userLoginState by homeViewModel.userLoginPreferenceState.collectAsStateWithLifecycle()
    val firestoreGameState by homeViewModel.firestoreGameState.collectAsStateWithLifecycle()
    val firestorePlayerPropertyState by homeViewModel.firestorePlayerPropertyState.collectAsStateWithLifecycle()
    val gameState by homeViewModel.gameState.collectAsStateWithLifecycle()
    val multiPurposeDialogState by homeViewModel.uiMultiPurposeDialog.collectAsStateWithLifecycle()
    val playerPropertiesListState by homeViewModel.playerPropertiesListState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    ObserverAsEvents(
        flow = SnackbarController.events,
        key1 = snackbarHostState
    ) { event ->
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()

            val result = snackbarHostState.showSnackbar(
                message = event.message,
                actionLabel = event.action?.name,
                duration = SnackbarDuration.Long
            )

            if(result == SnackbarResult.ActionPerformed) {
                event.action?.action?.invoke()
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = homeViewModel.navDrawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(
                    username = userLoginState.userName,
                    gameId = gamePrefState.gameId,
                    isGameActive = gamePrefState.isGameActive ?: false,
                    onClickLeaveGameDialog = homeViewModel::onClickLeaveGameDialog,
                    onCLickLogOutDialog = homeViewModel::onClickLogOutDialog,
                    onClickNavigateToNewLocationDialog = homeViewModel::onClickNavigateToNewLocationDialog,
                    onClickPlayerPropertiesListDialog = homeViewModel::onClickPlayerPropertiesListDialog
                )
            }
        },
        gesturesEnabled = true
    ) {
        Scaffold(
            topBar = {
                GameTopAppBar(onClickNavIcon = { homeViewModel.onClickNavIcon(scope.coroutineContext) })
            },
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState
                )
            },
            floatingActionButton = {
                if(gamePrefState.isGameActive == true) {
                    QrFloatingActionButton(
                        onClickQrCodeScanner = navigateToQrCodeScanner
                    )
                } else {
                    CreateOrJoinGameFAB(
                        onClickCreateOrJoinGame = homeViewModel::onClickCreateOrJoinGameDialog,
                    )
                }
            }
        ) { contentPadding ->
            Column (
                modifier = modifier.padding(contentPadding)
            ) {
                if(gamePrefState.isGameActive == null) {
                    CircularProgressIndicator()
                } else {
                    if(gamePrefState.isGameActive == true){
                        ActiveGame(game = gameState.gameState)
                    } else {
                        NoActiveGame(onClickCreateOrJoinGame = homeViewModel::onClickCreateOrJoinGameDialog)
                    }
                }

                if(newGameDialogState.createOrJoinGameDialog){
                    CreateOrJoinGameDialog(
                        onClickCreateOrJoinGame = homeViewModel::onClickCreateOrJoinGameDialog,
                        newGame = homeViewModel::newGame,
                        gameId = newGameDialogState.gameId,
                        updateGameId = homeViewModel::updateGameId,
                    )
                }

                if(multiPurposeDialogState.leaveGameDialog){
                    MultiPurposeDialog(
                        onClickDialogState = homeViewModel::onClickLeaveGameDialog,
                        title = "Leave Game",
                        description = "Are you sure you want leave the game?",
                        isLeaveGame = true,
                        leaveGame = homeViewModel::leaveGame,
                    )
                }

                if(multiPurposeDialogState.logoutDialog){
                    MultiPurposeDialog(
                        onClickDialogState = homeViewModel::onClickLogOutDialog,
                        title = "Log Out",
                        description = "Are you sure you want to log out?",
                        isLogOut = true,
                        logOut = {},
                    )
                }

                if(multiPurposeDialogState.navigateToNewLocationDialog){
                    MultiPurposeDialog(
                        onClickDialogState = homeViewModel::onClickNavigateToNewLocationDialog,
                        title = "Navigation",
                        description = "Pay 100$ and navigate to any property space.",
                        isNavigate = true,
                        navigateToNewLocation = homeViewModel::navigateToNewLocation,
                    )
                }

                if(multiPurposeDialogState.playerPropertiesListDialog){
                    MultiPurposeDialog(
                        onClickDialogState = homeViewModel::onClickPlayerPropertiesListDialog,
                        title = "Player Properties",
                        description = "",
                        isPlayerPropertyList = true,
                        playerPropertyList = playerPropertiesListState.playerPropertiesListState!!
                    )
                }
            }
        }
    }
}

@Composable
private fun DrawerContent(
    username: String,
    gameId: String,
    isGameActive: Boolean,
    onClickLeaveGameDialog: () -> Unit,
    onCLickLogOutDialog: () -> Unit,
    onClickNavigateToNewLocationDialog: () -> Unit,
    onClickPlayerPropertiesListDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.AccountCircle,
            contentDescription = "Account Icon",
            modifier = modifier.size(120.dp)
        )
        Text(text = "Username: $username", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = modifier.padding(vertical = 2.dp))
        Text("Game ID: $gameId", style = MaterialTheme.typography.titleMedium)
        HorizontalDivider(thickness = 2.dp, modifier = modifier.padding(bottom = 12.dp, top = 8.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onClickLeaveGameDialog,
                enabled = isGameActive,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = "Leave", style = MaterialTheme.typography.titleMedium)
            }
            Button(
                onClick = onCLickLogOutDialog,
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Log Out", style = MaterialTheme.typography.titleMedium)
            }
        }
        HorizontalDivider(thickness = 2.dp, modifier = modifier.padding(bottom = 12.dp, top = 12.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onClickNavigateToNewLocationDialog,
                enabled = isGameActive,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = "Navigation", style = MaterialTheme.typography.titleMedium)
            }
        }
        HorizontalDivider(thickness = 2.dp, modifier = modifier.padding(bottom = 12.dp, top = 12.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onClickPlayerPropertiesListDialog,
                enabled = isGameActive,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = "Properties", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun ActiveGame(
    modifier: Modifier = Modifier,
    game: List<Game>,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 50.dp),
    ) {
        item {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Player", style = MaterialTheme.typography.headlineSmall)
                Text(text = "Balance", style = MaterialTheme.typography.headlineSmall)
            }
        }
        items(items = game.sortedByDescending { it.playerBalance }, key = { it.playerId }) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = it.playerName, style = MaterialTheme.typography.titleMedium)
                Text(text = "${it.playerBalance}", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun NoActiveGame(onClickCreateOrJoinGame: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = "Create or Join Game",
            style = MaterialTheme.typography.titleMedium,
            modifier = modifier.clickable { onClickCreateOrJoinGame() }
        )
        HorizontalDivider(
            thickness = 2.dp,
            modifier = modifier
                .width(220.dp)
                .padding(vertical = 4.dp)
        )
    }
}

@Composable
private fun MultiPurposeDialog(
    onClickDialogState: () -> Unit,
    title: String,
    description: String,
    isLeaveGame: Boolean = false,
    leaveGame: () -> Unit = {},
    isLogOut: Boolean = false,
    logOut: () -> Unit = {},
    isNavigate: Boolean = false,
    navigateToNewLocation: () -> Unit = {},
    isPlayerPropertyList: Boolean = false,
    playerPropertyList: List<PlayerPropertiesList> = listOf(),
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onClickDialogState,
        title = { Text(text = title, style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column {
                if(!isPlayerPropertyList){
                    Text(text = description, style = MaterialTheme.typography.titleMedium)
                }

                if(isPlayerPropertyList) {
                    if(playerPropertyList.isNotEmpty()) {
                        Spacer(modifier = modifier.padding(top = 4.dp))
                        Row(
                            modifier = modifier.fillMaxWidth().padding(bottom = 2.dp)
                        ) {
                            Column(modifier = modifier.weight(.2f)) { Text(text = "No.", style = MaterialTheme.typography.titleSmall) }
                            Column(modifier = modifier.weight(.65f).padding(horizontal = 0.dp)) { Text(text = "Property Name", style = MaterialTheme.typography.titleMedium) }
                            Column(modifier = modifier.weight(.15f)) { Text(text = "Rent", style = MaterialTheme.typography.titleMedium) }
                        }
                        playerPropertyList.forEach { playerProperty ->
                            Row( modifier = modifier.fillMaxWidth().padding(top = 6.dp) ) {
                                Column(modifier = modifier.weight(.2f)) {
                                    Text(text = "${playerProperty.propertyNo}", style = MaterialTheme.typography.bodyLarge, lineHeight = 18.sp)
                                }
                                Column(modifier = modifier.weight(.65f)) {
                                    Text(text = playerProperty.propertyName, style = MaterialTheme.typography.bodyLarge, lineHeight = 18.sp)
                                }
                                Column(modifier = modifier.weight(.15f).padding(start = 2.dp)) {
                                    Text(text = "${playerProperty.rentLevel}", style = MaterialTheme.typography.bodyLarge, lineHeight = 18.sp)
                                }
                            }
                        }
                    } else {
                        Text(text = "No Property Owned.", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        },
        dismissButton = {
            if(!isPlayerPropertyList){
                TextButton(
                    onClick = onClickDialogState
                ) {
                    Text(text = stringResource(R.string.dismiss), style = MaterialTheme.typography.bodyLarge)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if(isLeaveGame) {
                        leaveGame()
                    } else if(isLogOut) {
                        logOut()
                    } else if(isNavigate) {
                        navigateToNewLocation()
                    }
                    onClickDialogState()
                }
            ) {
                Text(text = stringResource(R.string.confirm), style = MaterialTheme.typography.bodyLarge)
            }
        }
    )
}

@Composable
private fun CreateOrJoinGameDialog(
    onClickCreateOrJoinGame: () -> Unit,
    newGame: () -> Unit,
    updateGameId: (String) -> Unit,
    gameId: String,
    modifier: Modifier = Modifier
) {
    AlertDialog (
        onDismissRequest = {
            updateGameId("")
            onClickCreateOrJoinGame()
        },
        title = { Text(text = "Create or Join Game", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column {
                Text(
                    text = "- Create a new game by giving it a name.\n- Enter game id to join a game.",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = modifier.padding(vertical = 8.dp))
                OutlinedTextField(
                    value = gameId,
                    onValueChange = updateGameId,
                    label = { Text(text = stringResource(R.string.game_id), style = MaterialTheme.typography.bodyLarge) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
                    textStyle = MaterialTheme.typography.bodyLarge
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    updateGameId("")
                    onClickCreateOrJoinGame()
                }
            ) {
                Text(text = stringResource(R.string.dismiss), style = MaterialTheme.typography.bodyLarge)
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    newGame()
                    onClickCreateOrJoinGame()
                },
                enabled = gameId.isNotBlank()
            ) {
                Text(text = stringResource(R.string.confirm), style = MaterialTheme.typography.bodyLarge)
            }
        }
    )
}

@Composable
private fun QrFloatingActionButton(
    onClickQrCodeScanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClickQrCodeScanner,
        shape = MaterialTheme.shapes.medium
    ) {
        Icon(
            painter = painterResource(R.drawable.baseline_qr_code_scanner_24),
            contentDescription = stringResource(R.string.qr_code),
            modifier = modifier.size(38.dp)
        )
    }
}

@Composable
private fun CreateOrJoinGameFAB(
    onClickCreateOrJoinGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClickCreateOrJoinGame,
        shape = MaterialTheme.shapes.medium
    ) {
        Icon(
            Icons.Filled.Add,
            contentDescription = stringResource(R.string.floating_action_button)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameTopAppBar(
    onClickNavIcon: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(text = stringResource(R.string.monoploy_title),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onClickNavIcon,
            ) {
                Icon(Icons.Filled.Menu, contentDescription = "Menu")
            }
        }
    )
}