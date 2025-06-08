package com.example.monopolyultimatebanker.ui.screens.signupandlogin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.monopolyultimatebanker.R
import com.example.monopolyultimatebanker.ui.navigation.NavigationDestination
import com.example.monopolyultimatebanker.utils.ObserverAsEvents
import com.example.monopolyultimatebanker.utils.SnackbarController
import kotlinx.coroutines.launch

object SignUpAndLogInDestination: NavigationDestination {
    override val route = "sign_in_and_log_in"
}

@Composable
fun SignUpAndLogInScreen(
    modifier: Modifier = Modifier,
    navigateToHomeScreen: () -> Unit,
    viewModel: SignUpAndLogInViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val uiState = viewModel.uiState
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

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
    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        },
    ) { contentPadding ->

        val colorStops = arrayOf(
            0.0f to Color(0xFF77CBDA),
            0.8f to Color.White
        )
        Column (
            modifier = modifier
                .padding(contentPadding)
                .fillMaxSize()
                .background(Brush.verticalGradient(colorStops = colorStops))
                .clickable(interactionSource = interactionSource, indication = null, onClick = { focusManager.clearFocus() }),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = modifier.padding(top = 100.dp))
            Image(
                painter = painterResource(R.drawable.monopoly_title_logo),
                contentDescription = stringResource(R.string.monoploy_title),
                contentScale = ContentScale.Fit,
                modifier = modifier.size(height = 200.dp, width = 320.dp)
            )
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.elevatedCardElevation(20.dp),
                modifier = modifier.padding(top = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .width(IntrinsicSize.Min)
                        .background(Color.White)
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if(uiState.checked){
                        LogInForm(
                            uiState = uiState,
                            onClickLogIn = { viewModel.onClickLogIn(navigateTo = { navigateToHomeScreen() }) },
                            updateEmail = viewModel::updateEmail,
                            updatePassword = viewModel::updatePassword,
                            isNotEmpty = viewModel::isNotEmptyForLogIn,
                            checked = true,
                            onCheckChange = viewModel::onCheckedChange
                        )
                    } else {
                        SignInForm(
                            uiState = uiState,
                            onClickSignIn = { viewModel.onClickSignIn(navigateTo = { navigateToHomeScreen() }) },
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
    var showPassword by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = uiState.email,
        onValueChange = updateEmail,
        label = { Text(text = stringResource(R.string.email), style = MaterialTheme.typography.bodyLarge) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
        modifier = Modifier.padding(vertical = 10.dp)
    )
    OutlinedTextField(
        value = uiState.password,
        onValueChange = updatePassword,
        label = { Text(text = stringResource(R.string.password), style = MaterialTheme.typography.bodyLarge) },
        visualTransformation = if(showPassword){ VisualTransformation.None } else { PasswordVisualTransformation() },
        trailingIcon = @Composable {
            IconButton(
                onClick = { showPassword = !showPassword }
            ) {
                if(showPassword) {
                    Icon(painter = painterResource(R.drawable.password_visibility_off), contentDescription = "visibility on")
                } else {
                    Icon(painter = painterResource(R.drawable.password_visibility_on), contentDescription = "visibility off")
                }
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
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
        shape = MaterialTheme.shapes.medium,
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
    var showPassword by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = uiState.userName,
        onValueChange = updateUsername,
        label = { Text(text = stringResource(R.string.username), style = MaterialTheme.typography.bodyLarge) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next)
    )
    OutlinedTextField(
        value = uiState.email,
        onValueChange = updateEmail,
        label = { Text(text = stringResource(R.string.email), style = MaterialTheme.typography.bodyLarge) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
        modifier = Modifier.padding(vertical = 10.dp)
    )
    OutlinedTextField(
        value = uiState.password,
        onValueChange = updatePassword,
        label = { Text(text = stringResource(R.string.password), style = MaterialTheme.typography.bodyLarge) },
        visualTransformation = if(showPassword){ VisualTransformation.None } else { PasswordVisualTransformation() },
        trailingIcon = @Composable {
            IconButton(
                onClick = { showPassword = !showPassword }
            ) {
                if(showPassword) {
                    Icon(painter = painterResource(R.drawable.password_visibility_off), contentDescription = "visibility")
                } else {
                    Icon(painter = painterResource(R.drawable.password_visibility_on), contentDescription = "visibility")
                }
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
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
            onCheckedChange = { onCheckChange(it) },
        )
    }
    Button(
        onClick = onClickSignIn,
        enabled = isNotEmpty(),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.padding(vertical = 10.dp)
    ) {
        Text(text = stringResource(R.string.sign_in))
    }
}