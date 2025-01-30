package com.example.monopolyultimatebanker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.monopolyultimatebanker.ui.screens.home.HomeDestination
import com.example.monopolyultimatebanker.ui.screens.home.HomeScreen
import com.example.monopolyultimatebanker.ui.screens.signupandlogin.SignUpAndLogInDestination
import com.example.monopolyultimatebanker.ui.screens.signupandlogin.SignUpAndLogInScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = SignUpAndLogInDestination.route,
        modifier = modifier
    ) {
        composable(route = SignUpAndLogInDestination.route) {
            SignUpAndLogInScreen(
                navigateTo = { navController.navigate(route = HomeDestination.route) }
            )
        }

        composable(route = HomeDestination.route) {
            HomeScreen()
        }
    }
}