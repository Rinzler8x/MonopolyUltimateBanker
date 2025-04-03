package com.example.monopolyultimatebanker.ui.screens.propertycard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
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
    navigateToHomeScreen: () -> Unit,
    propertyCardViewModel: PropertyCardViewModel = hiltViewModel()
) {

    val propertyState by propertyCardViewModel.propertyState.collectAsStateWithLifecycle()
    val gamePrefState by propertyCardViewModel.gamePreferenceState.collectAsStateWithLifecycle()
    val qrPrefState by propertyCardViewModel.qrPrefState.collectAsStateWithLifecycle()

    Scaffold { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(20.dp)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(54.dp)
        ) {
            Row(
                modifier = modifier
                    .background(Color(propertyState.color))
                    .fillMaxWidth()
                    .height(50.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = propertyState.propertyNo.toString(),
                )
            }

            Text(
                text = propertyState.propertyName,
            )

            Row {
                Column(
                    modifier = modifier.weight(.65f)
                ) {
                    Text(
                        text = "Rent Level"
                    )
                }

                Column(
                    modifier = modifier.weight(.35f)
                ) {
                    Text(
                        text = "Rent"
                    )
                }
            }

            repeat(5) { firstLoop ->
                Row(

                ) {
                    Column(
                        modifier = modifier.weight(.65f)
                    ) {
                        Row(
                            modifier = modifier.padding(horizontal = 6.dp),
                        ) {
                            repeat(5) { secondLoop ->
                                Box(
                                    modifier = if(firstLoop == secondLoop) {
                                        modifier
                                            .rotate(-45f)
                                            .border(
                                                width = 2.dp,
                                                color = Color.Black,
                                                shape = RoundedCornerShape(6.dp)
                                            )
                                            .background(Color.Black, RoundedCornerShape(6.dp))
                                            .padding(8.dp)
                                    } else {
                                        Modifier
                                            .rotate(-45f)
                                            .border(
                                                width = 2.dp,
                                                color = Color.Black,
                                                shape = RoundedCornerShape(6.dp)
                                            )
                                            .background(Color.White, RoundedCornerShape(6.dp))
                                            .padding(8.dp)
                                    },
                                ) {
                                    Text(
                                        text = secondLoop.plus(1).toString(),
                                        color = if(firstLoop == secondLoop) {
                                            Color.White

                                        } else {
                                            Color.Black
                                        },
                                        modifier = modifier.rotate(45f)
                                    )
                                }
                                Spacer(
                                    modifier = modifier.padding(horizontal = 1.dp)
                                )
                            }
                        }
                    }
                    Column(
                        modifier = modifier.weight(.35f)
                    ) {
                        //Dynamically change the field variable
                        val fieldName = "rentLevel${firstLoop + 1}"
                        val field: Field = propertyState.javaClass.getDeclaredField(fieldName)
                        field.isAccessible = true // Make the field accessible
                        val rentValue = field.get(propertyState) as Int
                        Text(text = "$${rentValue}")
                    }
                }
            }

            Button(
                onClick = { propertyCardViewModel.onClickPay(navigateToHomeScreen) }
            ) {
                Text(
                    text = stringResource(R.string.pay)
                )
            }
        }
    }
}


