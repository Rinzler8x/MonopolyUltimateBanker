package com.example.monopolyultimatebanker.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.monopolyultimatebanker.ui.navigation.NavigationDestination

object HomeDestination: NavigationDestination {
    override val route = "home"
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    Scaffold() { contentPadding ->
        Box(
            modifier = modifier.padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            Column {
                Text("Scan this card then any property card.\n(Rent level resets to 1)")
                Spacer(modifier = modifier.padding(top = 50.dp))
                Button(
                    onClick = { viewModel.insertDataTest() }
                ) {
                    Text("run test")
                }
            }
        }
    }
}