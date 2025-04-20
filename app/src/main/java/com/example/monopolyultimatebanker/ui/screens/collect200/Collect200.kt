package com.example.monopolyultimatebanker.ui.screens.collect200

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
            Text(text = "Collect 200")
            Button(
                onClick = {
                    collect200ViewModel.onClickCollect()
                    collect200ViewModel.onClickResultDialog()
                }
            ) {
                Text(text = "Collect")
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
        title = { Text(text = "200 Collected!") },
        text = { Text(text = "The amount was successfully transferred to your account.") },
        confirmButton = {
            TextButton(
                onClick = {
                    onClickResultDialog()
                    navigateToHome()
                }
            ) {
                Text(text = stringResource(R.string.confirm))
            }
        }
    )
}