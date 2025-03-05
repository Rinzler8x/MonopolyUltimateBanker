package com.example.monopolyultimatebanker.ui.screens.propertycard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.monopolyultimatebanker.ui.navigation.NavigationDestination

object PropertyCardDestination: NavigationDestination {
    override val route = "property_card"
}

@Composable
fun PropertyCard(
    modifier: Modifier = Modifier,
    navController: NavController,
    propertyCardViewModel: PropertyCardViewModel = hiltViewModel()
) {

    val propertyState by propertyCardViewModel.propertyState.collectAsStateWithLifecycle()
    val qrPrefState by propertyCardViewModel.qrPrefState.collectAsStateWithLifecycle()

    Scaffold { innerPadding ->

        Column(
            modifier = modifier.padding(innerPadding)
        ) {
            Text(text = propertyState.propertyName)
        }
    }
}