package com.example.monopolyultimatebanker.ui.screens.signupandlogin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.monopolyultimatebanker.R

@Composable
fun SignUpAndLogInScreen(
    modifier: Modifier = Modifier,
    viewModel: SignInAndLogInViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    Column (
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column {
            Text(text = uiState.userName)
            Text(text = uiState.email)
            Text(text = uiState.password)
        }
        OutlinedTextField(
            value = uiState.userName,
            onValueChange = viewModel::updateUsername,
            label = { Text(stringResource(R.string.username)) },
        )
        OutlinedTextField(
            value = uiState.email,
            onValueChange = viewModel::updateEmail,
            label = { Text(stringResource(R.string.email)) },
            modifier = Modifier.padding(vertical = 10.dp)
        )
        OutlinedTextField(
            value = uiState.password,
            onValueChange = viewModel::updatePassword,
            label = { Text(stringResource(R.string.password)) },
        )
        Button(
            onClick = viewModel::onClickSignIn,
            enabled = viewModel.isNotEmpty()
        ) {
            Text(text = stringResource(R.string.sign_in))
        }
    }
}