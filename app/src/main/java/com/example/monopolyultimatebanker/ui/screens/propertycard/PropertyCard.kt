package com.example.monopolyultimatebanker.ui.screens.propertycard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.monopolyultimatebanker.R
import com.example.monopolyultimatebanker.ui.navigation.NavigationDestination
import java.lang.reflect.Field

object PropertyCardDestination: NavigationDestination {
    override val route = "property_card"
}

@Composable
fun PropertyCard(
    modifier: Modifier = Modifier,
    propertyCardViewModel: PropertyCardViewModel = hiltViewModel()
) {

    val propertyState by propertyCardViewModel.propertyState.collectAsStateWithLifecycle()
    val qrPrefState by propertyCardViewModel.qrPrefState.collectAsStateWithLifecycle()

    Scaffold { innerPadding ->
        Column(
            modifier = modifier.padding(innerPadding).fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(64.dp)
        ) {
            Row(
                modifier = modifier.background(Color.Blue).fillMaxWidth().height(50.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = propertyState.propertyNo.toString(),
                    modifier = modifier.background(color = Color.Red)
                )
            }

            Text(
                text = propertyState.propertyName,
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "Rent Level"
                )
                Text(
                    text = "Rent"
                )
            }

            repeat(5) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp)
                ) {
                    Row {
                        repeat(5) {
                            Text(
                                text = it.plus(1).toString(),
                                modifier = modifier.padding(end = 16.dp)
                            )
                        }
                    }

                    //Dynamically change the field variable
                    val fieldName = "rentLevel${it + 1}"
                    val field: Field = propertyState.javaClass.getDeclaredField(fieldName)
                    field.isAccessible = true // Make the field accessible
                    val rentValue = field.get(propertyState) as Int
                    Text(text = "$${rentValue}")
                }
            }

            Button(
                onClick = {}
            ) {
                Text(
                    text = stringResource(R.string.pay)
                )
            }
        }
    }
}


