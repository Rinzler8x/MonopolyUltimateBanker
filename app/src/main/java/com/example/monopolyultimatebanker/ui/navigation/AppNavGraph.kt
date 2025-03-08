package com.example.monopolyultimatebanker.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.monopolyultimatebanker.ui.screens.eventcard.EventCard
import com.example.monopolyultimatebanker.ui.screens.eventcard.EventCardDestination
import com.example.monopolyultimatebanker.ui.screens.home.HomeDestination
import com.example.monopolyultimatebanker.ui.screens.home.HomeScreen
import com.example.monopolyultimatebanker.ui.screens.propertycard.PropertyCard
import com.example.monopolyultimatebanker.ui.screens.propertycard.PropertyCardDestination
import com.example.monopolyultimatebanker.ui.screens.qrcodescanner.QrCodeScanner
import com.example.monopolyultimatebanker.ui.screens.qrcodescanner.QrCodeScannerDestination
import com.example.monopolyultimatebanker.ui.screens.signupandlogin.SignUpAndLogInDestination
import com.example.monopolyultimatebanker.ui.screens.signupandlogin.SignUpAndLogInScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: NavViewModel = hiltViewModel()
) {
    val uiState by viewModel.navUiState.collectAsStateWithLifecycle()

    if(uiState.isLoggedIn == null) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier.fillMaxSize()
        ) {
            CircularProgressIndicator()
        }
    } else {
        NavHost(
            navController = navController,
//            startDestination = if(uiState.isLoggedIn!!) HomeDestination.route else SignUpAndLogInDestination.route,
            startDestination = PropertyCardDestination.route, //Statically set to bypass other screens, **FOR DEVELOPMENT PURPOSE ONLY**
            modifier = modifier
        ) {
            composable(route = SignUpAndLogInDestination.route) {
                SignUpAndLogInScreen(
                    navigateToHomeScreen = {
                        navController.navigate(HomeDestination.route) {
                            popUpTo(SignUpAndLogInDestination.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(route = HomeDestination.route) {
                HomeScreen(
                    navigateToQrCodeScanner = {
                        navController.navigate(QrCodeScannerDestination.route)
                    }
                )
            }

            composable(route = QrCodeScannerDestination.route) {
                QrCodeScanner(
                    navigateToPropertyScreen = {
                        navController.navigate(PropertyCardDestination.route) {
                            popUpTo(QrCodeScannerDestination.route) {
                                inclusive = true
                            }
                        }
                    },
                    navigateToEventScreen = {
                        navController.navigate(EventCardDestination.route) {
                            popUpTo(QrCodeScannerDestination.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            composable(route = PropertyCardDestination.route) {
                PropertyCard()
            }

            composable(route = EventCardDestination.route) {
                EventCard()
            }
        }
    }
}