package com.example.monopolyultimatebanker.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.monopolyultimatebanker.ui.navigation.NavigationDestination

object HomeDestination: NavigationDestination {
    override val route = "home"
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
) {
    Scaffold() { contentPadding ->
        Box(
            modifier = modifier.padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            Text("Home Page")
        }
    }
}