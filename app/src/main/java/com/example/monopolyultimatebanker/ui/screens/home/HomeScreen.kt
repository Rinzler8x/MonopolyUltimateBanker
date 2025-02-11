package com.example.monopolyultimatebanker.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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

    ModalNavigationDrawer(
        drawerState = viewModel.navDrawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent()
            }
        },
        gesturesEnabled = viewModel.navDrawerState.isOpen
    ) {
        Scaffold(
            topBar = {
                GameTopAppBar(onClickNavIcon = { viewModel.onClickNavIcon(scope.coroutineContext) })
            },
            floatingActionButton = { ExpandableFloatingActionButton() }
        ) { contentPadding ->
            Column (
                modifier = modifier.padding(contentPadding)
            ) {
                NoActiveGame()
            }
        }
    }

}

@Composable
fun DrawerContent(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth().padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.AccountCircle,
            contentDescription = "Account Icon",
            modifier = modifier.size(120.dp)
        )
        Text("Username: Rinzler")
        Text("Game ID: 123")
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
fun NoActiveGame(modifier: Modifier = Modifier) {

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Text("Create or Join Game")
        HorizontalDivider(
            modifier = modifier.width(220.dp).padding(vertical = 4.dp)
        )
    }
}

@Composable
fun ExpandableFloatingActionButton(
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
            onClick = { },
        ) {
            Icon(
                Icons.Filled.Add,
                contentDescription = stringResource(R.string.floating_action_button)
            )
        }
        SmallFloatingActionButton (
            onClick = { },
        ) {
            Icon(
                Icons.Filled.Add,
                contentDescription = stringResource(R.string.floating_action_button)
            )
        }
        FloatingActionButton(
            onClick = { },
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
fun GameTopAppBar(
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