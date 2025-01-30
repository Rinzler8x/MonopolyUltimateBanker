package com.example.monopolyultimatebanker.ui.screens.signupandlogin

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.monopolyultimatebanker.R
import com.example.monopolyultimatebanker.ui.navigation.NavigationDestination

object SignUpAndLogInDestination: NavigationDestination {
    override val route = "signInAndLogIn"
}

@Composable
fun SignUpAndLogInScreen(
    modifier: Modifier = Modifier,
    navigateTo: () -> Unit,
    viewModel: SignUpAndLogInViewModel = hiltViewModel()
) {
//    var checked by remember { mutableStateOf(true) }
    val uiState = viewModel.uiState
    Scaffold(
        snackbarHost = {
        }
    ) { contentPadding ->
        Column (
            modifier = modifier
                .padding(contentPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier.width(IntrinsicSize.Min),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if(uiState.checked){
                    LogInForm(
                        uiState = uiState,
                        onClickLogIn = { viewModel.onClickLogIn(navigateTo = { navigateTo() }) },
                        updateEmail = viewModel::updateEmail,
                        updatePassword = viewModel::updatePassword,
                        isNotEmpty = viewModel::isNotEmptyForLogIn,
                        checked = true,
                        onCheckChange = viewModel::onCheckedChange
                    )
                } else {
                    SignInForm(
                        uiState = uiState,
                        onClickSignIn = { viewModel.onClickSignIn(navigateTo = { navigateTo() }) },
                        updateUsername = viewModel::updateUsername,
                        updateEmail = viewModel::updateEmail,
                        updatePassword = viewModel::updatePassword,
                        isNotEmpty = viewModel::isNotEmptyForSignUp,
                        checked = false,
                        onCheckChange = viewModel::onCheckedChange
                    )
                }
            }
        }
    }
}


@Composable
fun LogInForm(
    uiState: UiState,
    onClickLogIn: () -> Unit,
    updateEmail: (String) -> Unit,
    updatePassword: (String) -> Unit,
    isNotEmpty: () -> Boolean,
    checked: Boolean,
    onCheckChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = uiState.email,
        onValueChange = updateEmail,
        label = { Text(stringResource(R.string.email)) },
        modifier = Modifier.padding(vertical = 10.dp)
    )
    OutlinedTextField(
        value = uiState.password,
        onValueChange = updatePassword,
        label = { Text(stringResource(R.string.password)) },
    )
    Row (
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ){
        Row {
            Text(text = stringResource(R.string.sign_in))
            Text(text = "?")
        }
        Switch(
            checked = checked,
            onCheckedChange = { onCheckChange(it) }
        )
    }
    Button(
        onClick = onClickLogIn,
        enabled = isNotEmpty(),
        modifier = Modifier.padding(vertical = 10.dp)
    ) {
        Text(text = stringResource(R.string.log_in))
    }
}

@Composable
fun SignInForm(
    uiState: UiState,
    onClickSignIn: () -> Unit,
    updateUsername: (String) -> Unit,
    updateEmail: (String) -> Unit,
    updatePassword: (String) -> Unit,
    isNotEmpty: () -> Boolean,
    checked: Boolean,
    onCheckChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = uiState.userName,
        onValueChange = updateUsername,
        label = { Text(stringResource(R.string.username)) },
    )
    OutlinedTextField(
        value = uiState.email,
        onValueChange = updateEmail,
        label = { Text(stringResource(R.string.email)) },
        modifier = Modifier.padding(vertical = 10.dp)
    )
    OutlinedTextField(
        value = uiState.password,
        onValueChange = updatePassword,
        label = { Text(stringResource(R.string.password)) },
    )
    Row (
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ){
        Row {
            Text(text = stringResource(R.string.log_in))
            Text(text = "?")
        }
        Switch(
            checked = checked,
            onCheckedChange = { onCheckChange(it) }
        )
    }
    Button(
        onClick = onClickSignIn,
        enabled = isNotEmpty(),
        modifier = Modifier.padding(vertical = 10.dp)
    ) {
        Text(text = stringResource(R.string.sign_in))
    }
}