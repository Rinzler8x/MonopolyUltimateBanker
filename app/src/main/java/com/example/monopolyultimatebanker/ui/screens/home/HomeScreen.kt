package com.example.monopolyultimatebanker.ui.screens.home

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.monopolyultimatebanker.R
import com.example.monopolyultimatebanker.data.gametable.Game
import com.example.monopolyultimatebanker.ui.navigation.NavigationDestination
import com.example.monopolyultimatebanker.utils.ObserverAsEvents
import com.example.monopolyultimatebanker.utils.SnackbarController
import kotlinx.coroutines.launch

object HomeDestination: NavigationDestination {
    override val route = "home"
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navigateToQrCodeScanner: () -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val dialogState = homeViewModel.dialogState
    val gamePrefState by homeViewModel.gamePreferenceState.collectAsStateWithLifecycle()
    val userLoginState by homeViewModel.userLoginPreferenceState.collectAsStateWithLifecycle()
    val firestoreGameState by homeViewModel.firestoreGameState.collectAsStateWithLifecycle()
    val firestorePlayerPropertyState by homeViewModel.firestorePlayerPropertyState.collectAsStateWithLifecycle()
    val gameState by homeViewModel.gameState.collectAsStateWithLifecycle()

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
                    isGameActive = gamePrefState.isGameActive,
                    onClickLeaveGameDialog = homeViewModel::onClickLeaveGameDialog
                )
            }
        },
        gesturesEnabled = homeViewModel.navDrawerState.isOpen
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
                if(gamePrefState.isGameActive) {
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

                if(gamePrefState.isGameActive){
                    ActiveGame(game = gameState.gameState)
                } else {
                    NoActiveGame()
                }

                if(dialogState.createOrJoinGameDialog){
                    CreateOrJoinGameDialog(
                        onClickCreateOrJoinGame = homeViewModel::onClickCreateOrJoinGameDialog,
                        newGame = homeViewModel::newGame,
                        gameId = dialogState.gameId,
                        updateGameId = homeViewModel::updateGameId,
                    )
                }

                if(dialogState.leaveGameDialog){
                    LeaveGameDialog(
                        onClickLeaveGameDialog = homeViewModel::onClickLeaveGameDialog,
                        leaveGame = homeViewModel::leaveGame
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
        Text(text = "Username: $username")
        Text("Game ID: $gameId")
        HorizontalDivider(modifier = modifier.padding(bottom = 12.dp, top = 8.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onClickLeaveGameDialog,
                enabled = isGameActive
            ) {
                Text("Leave")
            }
            Button(
                onClick = {},
            ) {
                Text("Log Out")
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
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Player")
                Text(text = "Balance")
            }
        }
        items(items = game.sortedByDescending { it.playerBalance }, key = { it.playerId }) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = it.playerName)
                Text(text = it.playerBalance.toString())
            }
        }
    }
}

@Composable
private fun NoActiveGame(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Text(text = "Create or Join Game")
        HorizontalDivider(
            modifier = modifier
                .width(220.dp)
                .padding(vertical = 4.dp)
        )
    }
}

@Composable
private fun LeaveGameDialog(
    onClickLeaveGameDialog: () -> Unit,
    leaveGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onClickLeaveGameDialog,
        title = { Text(text = "Leave Game") },
        text = { Text(text = "Are you sure you want leave the game?") },
        dismissButton = {
            TextButton(
                onClick = onClickLeaveGameDialog
            ) {
                Text(text = stringResource(R.string.dismiss))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    leaveGame()
                    onClickLeaveGameDialog()
                }
            ) {
                Text(text = stringResource(R.string.confirm))
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
        title = { Text("Create or Join Game") },
        text = {
            Column {
                Text(
                    text = "- Create a new game by giving it a name.\n- Enter game id to join a game.",
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                )
                Spacer(modifier = modifier.padding(vertical = 8.dp))
                OutlinedTextField(
                    value = gameId,
                    onValueChange = updateGameId,
                    label = { Text(text = stringResource(R.string.game_id)) }
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
                Text(text = stringResource(R.string.dismiss))
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
                Text(text = stringResource(R.string.confirm))
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
        onClick = onClickQrCodeScanner
    ) {
        Icon(
            painter = painterResource(R.drawable.baseline_qr_code_scanner_24),
            contentDescription = stringResource(R.string.qr_code),
            modifier = modifier.size(42.dp)
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
            Text(stringResource(R.string.app_name), maxLines = 1, overflow = TextOverflow.Ellipsis)
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