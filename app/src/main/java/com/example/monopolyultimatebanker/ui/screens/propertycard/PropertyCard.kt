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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                propertyColor = propertyState.color,
                propertyNo = propertyState.propertyNo,
                propertyName = propertyState.propertyName,
                propertyState = propertyState,
                rentLevel = playerPropertyState.rentLevel,
                onClickPay = propertyCardViewModel::onClickPay,
            )

            if(propertyBottomSheetState.showBottomSheet) {
                PropertyBottomSheet(
                    onClickPropertyBottomSheet = propertyCardViewModel::onCLickPropertyBottomSheet,
                    sheetState = sheetState,
                    innerPadding = innerPadding,
                    ownedPlayerProperties = propertyBottomSheetState.ownedPlayerProperties,
                    selectedProperties = propertyBottomSheetState.selectedProperties,
                    onClickCheckBox = propertyCardViewModel::onClickCheckBox,
                    onClickTransferProperties = propertyCardViewModel::transferProperties,
                )
            }

            if(multiPurposeDialogState.purchaseDialogState) {
                MultiPurposePropertyDialog(
                    onClickMultiPurposePropertyDialog = propertyCardViewModel::onClickPurchaseDialog,
                    title = "Property Purchased",
                    description = "${propertyState.propertyName} was purchased successfully.",
                    isPurchase = true,
                    navigateToHomeScreen = navigateToHomeScreen
                )
            }

            if(multiPurposeDialogState.insufficientFundsDialogState){
                MultiPurposePropertyDialog(
                    onClickMultiPurposePropertyDialog = propertyCardViewModel::onClickInsufficientFundsDialog,
                    title = "Insufficient Funds",
                    description = "You have insufficient funds to purchase this property.",
                    isInsufficientFunds = true,
                    navigateToHomeScreen = navigateToHomeScreen
                )
            }

            if(multiPurposeDialogState.resultDialogState) {
                MultiPurposePropertyDialog(
                    onClickMultiPurposePropertyDialog = propertyCardViewModel::onClickResultDialog,
                    title = "Rent Paid",
                    description = "Property No. ${propertyState.propertyNo} rent paid.\nNew Rent Level: ${multiPurposeDialogState.rentLevel}",
                    isResult = true,
                    navigateToHomeScreen = navigateToHomeScreen
                )
            }

            if(multiPurposeDialogState.propertyTransferDialogState){
                MultiPurposePropertyDialog(
                    onClickMultiPurposePropertyDialog = propertyCardViewModel::onClickPropertyTransferDialog,
                    title = "Properties Transfer",
                    description = "All selected properties were transferred successfully.",
                    isPropertyTransfer = true,
                )
            }

            if(multiPurposeDialogState.rentLevelIncreaseDialogState) {
                MultiPurposePropertyDialog(
                    onClickMultiPurposePropertyDialog = propertyCardViewModel::onClickRentLevelIncreaseDialog,
                    title = "Property Rent Level",
                    description = if(playerPropertyState.rentLevel == 5) {
                        "Property rent level is maxed out."
                    } else {
                        "Property rent level increased to ${multiPurposeDialogState.rentLevel}"
                    },
                    isOnlyRentLevelIncrease = true,
                    navigateToHomeScreen = navigateToHomeScreen
                )
            }
        }
    }
}

@Composable
private fun PropertyCardContent(
    propertyColor: Int,
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
                shape = RoundedCornerShape(10.dp)
            )
    ) {

        Row(
            modifier = modifier
                .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                .background(Color(propertyColor))
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = modifier
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        color = Color.Black,
                        shape = CircleShape
                    )
                    .background(Color.Yellow)
                    .padding(vertical = 12.dp, horizontal = 20.dp)
            ) {
                Text(
                    text = propertyNo.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                )
            }
        }

        Text(
            text = propertyName,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            textAlign = TextAlign.Center,
            modifier = modifier.padding(42.dp)
        )

        Row {
            Column(
                modifier = modifier.weight(.65f).padding(start = 10.dp, bottom = 6.dp)
            ) {
                Text(
                    text = "Rent Level",
                    fontWeight = FontWeight.Bold,
                )
            }

            Column(
                modifier = modifier.weight(.35f)
            ) {
                Text(
                    text = "Rent",
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        repeat(5) { firstLoop ->
            val gradientColor = Color(propertyColor).copy(alpha = 1f - ((4 - firstLoop) * 0.2f))
            Row(
                modifier = if(firstLoop == 4) {
                    modifier
                        .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
                        .background(gradientColor)
                        .padding(horizontal = 10.dp, vertical = 20.dp)
                } else {
                    modifier
                        .background(gradientColor)
                        .padding(horizontal = 10.dp, vertical = 20.dp)
                },
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
                                    fontWeight = FontWeight.Bold,
                                    modifier = modifier.rotate(45f)
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
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }

    }

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom,
        modifier = modifier.padding(16.dp).fillMaxWidth()
    ) {
        Button(
            onClick = onClickPay,
            modifier = modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = stringResource(R.string.pay)
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
                Text(text = "Please select the properties you wish transfer to the property owner.")
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 12.dp),
                ) {
                    Text(text = "Property No.")
                    Text(text = "Property Value")
                }
            }

            items(ownedPlayerProperties) { property ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 12.dp),
                ) {
                    Checkbox(
                        checked = selectedProperties.contains(OwnedPlayerProperties(property.ppId, property.propertyNo, property.propertyPrice)),
                        onCheckedChange = { isChecked ->
                            onClickCheckBox(property.ppId, property.propertyNo, property.propertyPrice, isChecked)
                        }
                    )
                    Text(text = property.propertyNo.toString())
                    Text(text = property.propertyPrice.toString())
                }
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Button(
                        onClick = {
                            onClickTransferProperties()
                        },
                    ) {
                        Text(text = "Confirm Transfer")
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
    isPropertyTransfer: Boolean = false,
    isPurchase: Boolean = false,
    isInsufficientFunds: Boolean = false,
    isResult: Boolean = false,
    isOnlyRentLevelIncrease: Boolean = false,
    navigateToHomeScreen: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = {
            if(isPurchase || isResult || isInsufficientFunds || isOnlyRentLevelIncrease) {
                onClickMultiPurposePropertyDialog()
                navigateToHomeScreen()
            } else if(isPropertyTransfer) {
                onClickMultiPurposePropertyDialog()
            }
        },
        title = { Text(text = title) },
        text = { Text(text = description) },
        confirmButton = {
            TextButton(
                onClick = {
                    if(isPurchase || isResult || isInsufficientFunds || isOnlyRentLevelIncrease) {
                        onClickMultiPurposePropertyDialog()
                        navigateToHomeScreen()
                    } else if(isPropertyTransfer) {
                        onClickMultiPurposePropertyDialog()
                    }
                },
            ) {
                Text(text = stringResource(R.string.confirm))
            }
        }
    )
}


