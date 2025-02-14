package com.example.monopolyultimatebanker.ui.screens.home

import android.widget.Space
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DividerDefaults
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
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.monopolyultimatebanker.R
import com.example.monopolyultimatebanker.ui.navigation.NavigationDestination

object HomeDestination: NavigationDestination {
    override val route = "home"
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val dialogState = viewModel.dialogState
    val gameState by viewModel.gamePreferenceState.collectAsStateWithLifecycle()
    val userLoginState by viewModel.userLoginPreferenceState.collectAsStateWithLifecycle()

    ModalNavigationDrawer(
        drawerState = viewModel.navDrawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(
                    username = userLoginState.userName,
                    gameId = gameState.gameId
                )
            }
        },
        gesturesEnabled = viewModel.navDrawerState.isOpen
    ) {
        Scaffold(
            topBar = {
                GameTopAppBar(onClickNavIcon = { viewModel.onClickNavIcon(scope.coroutineContext) })
            },
            floatingActionButton = {
                ExpandableFloatingActionButton(
                    onClickCreateGame = viewModel::onClickCreateGameDialog,
                    onClickJoinGame = viewModel::onClickJoinGameDialog
                )
            }
        ) { contentPadding ->
            Column (
                modifier = modifier.padding(contentPadding)
            ) {
                NoActiveGame(vm = viewModel)

                if(dialogState.createGameDialog){
                    CreateGameDialog(
                        onClickCreateGame = viewModel::onClickCreateGameDialog,
                        createGame = viewModel::createNewGame,
                        gameId = dialogState.gameId,
                        updateGameId = viewModel::updateGameId
                    )
                }

                if(dialogState.joinGameDialog) {
                    JoinGameDialog(
                        onClickJoinGame = viewModel::onClickJoinGameDialog,
                        joinGame = viewModel::joinNewGame,
                        gameId = dialogState.gameId,
                        updateGameId = viewModel::updateGameId
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
                onClick = {},
                enabled = false
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
fun NoActiveGame(modifier: Modifier = Modifier, vm: HomeViewModel) {

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JoinGameDialog(
    onClickJoinGame: () -> Unit,
    joinGame: () -> Unit,
    gameId: String,
    updateGameId: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    AlertDialog(
        onDismissRequest = {
            updateGameId("")
            onClickJoinGame()
        },
        title = { Text("Join Game") },
        text = {
            Column {
                Text(
                    text = "Enter game id to join.",
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
                    onClickJoinGame()
                }
            ) {
                Text(text = stringResource(R.string.dismiss))
            }

        },
        confirmButton = {
            TextButton(
                onClick = {
                    joinGame()
                    onClickJoinGame()
                }
            ) {
                Text(text = stringResource(R.string.join))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateGameDialog(
    onClickCreateGame: () -> Unit,
    createGame: () -> Unit,
    updateGameId: (String) -> Unit,
    gameId: String,
    modifier: Modifier = Modifier
) {
    AlertDialog (
        onDismissRequest = {
            updateGameId("")
            onClickCreateGame()
        },
        title = { Text("Create Game") },
        text = {
            Column {
                Text(
                    text = "To create a new game, give it a name.",
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
                    onClickCreateGame()
                }
            ) {
                Text(text = stringResource(R.string.dismiss))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    createGame()
                    onClickCreateGame()
                }
            ) {
                Text(text = stringResource(R.string.create))
            }
        }
    )
}

@Composable
private fun ExpandableFloatingActionButton(
    onClickJoinGame: () -> Unit,
    onClickCreateGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
//        AnimatedVisibility(
//            modifier = modifier.padding(bottom = 12.dp),
//            visible = state.value,
//            enter = slideInVertically {
//                // Slide in from 40 dp from the top.
//                with(density) { -40.dp.roundToPx() }
//            } + expandVertically(
//                // Expand from the top.
//                expandFrom = Alignment.Top
//            ) + fadeIn(
//                // Fade in with the initial alpha of 0.3f.
//                initialAlpha = 0.3f
//            ),
//            exit =  slideOutVertically(targetOffsetY = { fullHeight -> fullHeight }) + shrinkVertically() + fadeOut()
//        ) {}
        SmallFloatingActionButton (
            onClick = onClickJoinGame,
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_groups_24),
                contentDescription = stringResource(R.string.floating_action_button)
            )
        }
        SmallFloatingActionButton (
            onClick = onClickCreateGame,
        ) {
            Icon(
                Icons.Filled.Add,
                contentDescription = stringResource(R.string.floating_action_button)
            )
        }
        FloatingActionButton(
            onClick = {},
            modifier = modifier.padding(top = 4.dp),
        ) {
            Icon(
                Icons.Filled.Add,
                contentDescription = stringResource(R.string.floating_action_button)
            )
        }
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