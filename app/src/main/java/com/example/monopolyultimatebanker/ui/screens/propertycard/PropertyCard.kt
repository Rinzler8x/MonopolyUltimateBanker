package com.example.monopolyultimatebanker.ui.screens.propertycard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.monopolyultimatebanker.R
import com.example.monopolyultimatebanker.data.playerpropertytable.OwnedPlayerProperties
import com.example.monopolyultimatebanker.data.propertytable.Property
import com.example.monopolyultimatebanker.ui.navigation.NavigationDestination
import java.lang.reflect.Field

object PropertyCardDestination: NavigationDestination {
    override val route = "property_card"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyCard(
    modifier: Modifier = Modifier,
    navigateToHomeScreen: () -> Unit,
    propertyCardViewModel: PropertyCardViewModel = hiltViewModel()
) {

    val propertyState by propertyCardViewModel.propertyState.collectAsStateWithLifecycle()
    val playerPropertyState by propertyCardViewModel.playerPropertyState.collectAsStateWithLifecycle()
    val qrPrefState by propertyCardViewModel.qrPrefState.collectAsStateWithLifecycle()
    val propertyBottomSheetState by propertyCardViewModel.uiPropertyBottomSheetState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState()
    val multiPurposeDialogState by propertyCardViewModel.uiMultiPurposePropertyDialog.collectAsStateWithLifecycle()
    val propertyColor = Color(propertyState.color)

    Scaffold { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(20.dp)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            PropertyCardContent(
                propertyColor = propertyColor,
                propertyNo = propertyState.propertyNo,
                propertyName = propertyState.propertyName,
                propertyState = propertyState,
                rentLevel = playerPropertyState.rentLevel,
                onClickPay = propertyCardViewModel::onClickPay,
            )

            if(propertyBottomSheetState.showBottomSheet) {
                PropertyBottomSheet(
                    onClickPropertyBottomSheet = propertyCardViewModel::onClickPropertyBottomSheet,
                    sheetState = sheetState,
                    innerPadding = innerPadding,
                    ownedPlayerProperties = propertyBottomSheetState.ownedPlayerProperties,
                    selectedProperties = propertyBottomSheetState.selectedProperties,
                    onClickCheckBox = propertyCardViewModel::onClickCheckBox,
                    onClickTransferProperties = propertyCardViewModel::transferProperties,
                    rentValue = propertyBottomSheetState.rentValue,
                    playerBalance = propertyBottomSheetState.playerBalance
                )
            }

            if(multiPurposeDialogState.purchaseDialogState) {
                MultiPurposePropertyDialog(
                    onClickMultiPurposePropertyDialog = propertyCardViewModel::onClickPurchaseDialog,
                    title = "Property Purchased",
                    description = "${propertyState.propertyName} was purchased successfully.",
                    navigateToHomeScreen = navigateToHomeScreen
                )
            }

            if(multiPurposeDialogState.insufficientFundsDialogState){
                MultiPurposePropertyDialog(
                    onClickMultiPurposePropertyDialog = propertyCardViewModel::onClickInsufficientFundsDialog,
                    title = "Insufficient Funds",
                    description = "You have insufficient funds to purchase this property.",
                    navigateToHomeScreen = navigateToHomeScreen
                )
            }

            if(multiPurposeDialogState.resultDialogState) {
                MultiPurposePropertyDialog(
                    onClickMultiPurposePropertyDialog = propertyCardViewModel::onClickResultDialog,
                    title = "Rent Paid",
                    description = "Property No.${propertyState.propertyNo} rent paid.\nNew Rent Level: ${multiPurposeDialogState.rentLevel}.",
                    navigateToHomeScreen = navigateToHomeScreen
                )
            }

            if(multiPurposeDialogState.propertyTransferDialogState){
                MultiPurposePropertyDialog(
                    onClickMultiPurposePropertyDialog = propertyCardViewModel::onClickPropertyTransferDialog,
                    title = "Properties Transferred",
                    description = "All selected properties were transferred successfully and debt paid.",
                    navigateToHomeScreen = navigateToHomeScreen
                )
            }

            if(multiPurposeDialogState.rentLevelIncreaseDialogState) {
                MultiPurposePropertyDialog(
                    onClickMultiPurposePropertyDialog = propertyCardViewModel::onClickRentLevelIncreaseDialog,
                    title = "Rent Level Increased",
                    description = if(playerPropertyState.rentLevel == 5) {
                        "Property rent level is maxed out."
                    } else {
                        "Property rent level increased to ${multiPurposeDialogState.rentLevel}."
                    },
                    navigateToHomeScreen = navigateToHomeScreen
                )
            }
        }
    }
}

@Composable
private fun PropertyCardContent(
    propertyColor: Color,
    propertyNo: Int,
    propertyName: String,
    propertyState: Property,
    rentLevel: Int,
    onClickPay: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .border(
                width = 4.dp,
                color = Color.Black,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
    ) {

        Row(
            modifier = modifier
                .background(propertyColor)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$propertyNo",
                style = MaterialTheme.typography.displaySmall,
                modifier = if (propertyNo in 1..9) {
                    modifier
                        .clip(CircleShape)
                        .background(Color.Yellow)
                        .padding(top = 16.dp, bottom = 8.dp, start = 20.dp, end = 20.dp)
                } else {
                    modifier
                        .clip(CircleShape)
                        .background(Color.Yellow)
                        .padding(top = 22.dp, bottom = 14.dp, start = 16.dp, end = 16.dp)
                }
            )
        }

        Text(
            text = propertyName,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = modifier.padding(42.dp)
        )

        Row {
            Column(
                modifier = modifier.weight(.65f).padding(start = 10.dp, bottom = 6.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.rent_level),
                    style = MaterialTheme.typography.titleLarge,
                )
            }

            Column(
                modifier = modifier.weight(.35f)
            ) {
                Text(
                    text = stringResource(id = R.string.rent),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        repeat(5) { firstLoop ->
            val gradientColor = propertyColor.copy(alpha = 1f - ((4 - firstLoop) * 0.2f))
            Row(
                modifier = modifier
                    .background(gradientColor)
                    .padding(horizontal = 10.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
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
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = modifier.rotate(45f).padding(top = 2.dp,end = 2.dp)
                                )
                            }
                            Spacer(
                                modifier = modifier.padding(horizontal = 1.dp)
                            )
                        }
                    }
                }
                Column (
                    modifier = modifier.weight(.35f)
                ) {
                    //Dynamically change the field variable
                    val fieldName = "rentLevel${firstLoop + 1}"
                    val field: Field = propertyState.javaClass.getDeclaredField(fieldName)
                    field.isAccessible = true // Make the field accessible
                    val rentValue = field.get(propertyState) as Int
                    Text(
                        text = if(rentLevel == firstLoop + 1) { "$${rentValue}*" } else { "$${rentValue}" },
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        }
    }

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom,
        modifier = modifier.padding(12.dp).fillMaxWidth()
    ) {
        Button(
            onClick = onClickPay,
            modifier = modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = stringResource(R.string.pay),
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun PropertyBottomSheet(
    onClickPropertyBottomSheet: () -> Unit,
    sheetState: SheetState,
    innerPadding: PaddingValues,
    ownedPlayerProperties: List<OwnedPlayerProperties>,
    selectedProperties: List<OwnedPlayerProperties>,
    onClickCheckBox: (String, Int, Int, Boolean) -> Unit,
    onClickTransferProperties: () -> Unit,
    rentValue: Int,
    playerBalance: Int,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = {
            onClickPropertyBottomSheet()
        },
        sheetState = sheetState,
        modifier = modifier
            .fillMaxHeight()
            .padding(innerPadding)
    ) {
        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
        ) {
            item {
                Text(
                    text = "Insufficient Funds",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    modifier = modifier.padding(vertical = 10.dp).fillMaxWidth()
                )
                Text(
                    text = "Please select the properties you wish to transfer to the property owner.",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = modifier.padding(horizontal = 10.dp).fillMaxWidth()
                )
                HorizontalDivider(thickness = 2.dp, modifier = modifier.padding(vertical = 10.dp, horizontal = 8.dp))
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp, bottom = 8.dp, start = 12.dp, end = 12.dp),
                ) {
                    Column( modifier = modifier.weight(0.1f) ) {  }
                    Column( modifier = modifier.weight(0.45f) ) {
                        Text(text = "Property No.", style = MaterialTheme.typography.titleMedium)
                    }
                    Column( modifier = modifier.weight(0.45f) ) {
                        Text(text = "Property Value", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            items(items = ownedPlayerProperties.sortedBy { it.propertyNo }, key = { it.propertyNo }) { property ->
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                ) {
                    Column( modifier = modifier.weight(0.1f).padding(end = 8.dp) ) {
                        Checkbox(
                            checked = selectedProperties.contains(OwnedPlayerProperties(property.ppId, property.propertyNo, property.propertyPrice)),
                            onCheckedChange = { isChecked ->
                                onClickCheckBox(property.ppId, property.propertyNo, property.propertyPrice, isChecked)
                            }
                        )
                    }
                    Column( modifier = modifier.weight(0.45f).padding(top = 18.dp) ) {
                        Text(text = property.propertyNo.toString(), style = MaterialTheme.typography.bodyLarge)
                    }
                    Column( modifier = modifier.weight(0.45f).padding(top = 18.dp) ) {
                        Text(text = property.propertyPrice.toString(), style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            item {
                val temp = playerBalance - rentValue
                Text(
                    text = "Total: ${temp + selectedProperties.sumOf { it.propertyPrice }}",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = modifier.fillMaxWidth().padding(vertical = 18.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            onClickTransferProperties()
                        },
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(text = "Confirm Transfer", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}


@Composable
private fun MultiPurposePropertyDialog(
    onClickMultiPurposePropertyDialog: () -> Unit,
    title: String,
    description: String,
    navigateToHomeScreen: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = {
            onClickMultiPurposePropertyDialog()
            navigateToHomeScreen()
        },
        title = { Text(text = title, style = MaterialTheme.typography.headlineSmall) },
        text = { Text(text = description, style = MaterialTheme.typography.titleMedium) },
        confirmButton = {
            TextButton(
                onClick = {
                    onClickMultiPurposePropertyDialog()
                    navigateToHomeScreen()
                },
            ) {
                Text(text = stringResource(R.string.confirm), style = MaterialTheme.typography.bodyLarge)
            }
        }
    )
}


