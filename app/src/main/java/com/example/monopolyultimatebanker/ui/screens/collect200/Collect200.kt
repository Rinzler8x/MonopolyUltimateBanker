package com.example.monopolyultimatebanker.ui.screens.collect200

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.monopolyultimatebanker.R
import com.example.monopolyultimatebanker.ui.navigation.NavigationDestination

object Collect200Destination: NavigationDestination {
    override val route = "collect_200"
}

@Composable
fun Collect200(
    navigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    collect200ViewModel: Collect200ViewModel = hiltViewModel()
) {
    val resultsDialogState by collect200ViewModel.uiResultDialog.collectAsStateWithLifecycle()

    Scaffold { innerPadding ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                modifier = modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(R.drawable.monoploy_collect_200),
                    contentDescription = stringResource(R.string.collect_200),
                    contentScale = ContentScale.Fit,
                    modifier = modifier.fillMaxSize()
                )
                Box(
                    modifier = modifier
                        .padding(top = 26.dp,end = 8.dp)
                        .sizeIn(minWidth = 240.dp, minHeight = 100.dp)
                        .align(Alignment.Center)
                        .clickable {
                            collect200ViewModel.onClickCollect()
                            collect200ViewModel.onClickResultDialog()
                        }
                )
            }

            if(resultsDialogState.resultDialogState) {
                ResultDialog(
                    onClickResultDialog = collect200ViewModel::onClickResultDialog,
                    navigateToHome = navigateToHome,
                )
            }
        }
    }
}

@Composable
private fun ResultDialog(
    onClickResultDialog: () -> Unit,
    navigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = {
            onClickResultDialog()
            navigateToHome()
        },
        title = { Text(text = "200$ Collected!", style = MaterialTheme.typography.headlineSmall) },
        text = { Text(text = "The amount was successfully transferred to your account.", style = MaterialTheme.typography.titleMedium) },
        confirmButton = {
            TextButton(
                onClick = {
                    onClickResultDialog()
                    navigateToHome()
                }
            ) {
                Text(text = stringResource(R.string.confirm), style = MaterialTheme.typography.bodyLarge)
            }
        }
    )
}