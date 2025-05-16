package com.example.monopolyultimatebanker.ui.screens.qrcodescanner

import androidx.camera.core.ImageAnalysis
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.monopolyultimatebanker.R
import com.example.monopolyultimatebanker.ui.navigation.NavigationDestination
import com.example.monopolyultimatebanker.utils.ObserverAsEvents
import com.example.monopolyultimatebanker.utils.SnackbarController
import kotlinx.coroutines.launch

object QrCodeScannerDestination: NavigationDestination {
    override val route = "qr_code_scanner"
}

@Composable
fun QrCodeScanner(
    modifier: Modifier = Modifier,
    navigateToPropertyScreen: () -> Unit,
    navigateToEventScreen: () -> Unit,
    navigateToCollect200: () -> Unit,
    qrCodeScannerViewModel: QrCodeScannerViewModel = hiltViewModel()
) {

    val qrState = qrCodeScannerViewModel.qrState
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraCtrlState by qrCodeScannerViewModel.uiCameraCtrl.collectAsStateWithLifecycle()
    val cameraCtrl = qrCodeScannerViewModel.getCameraController(context)
    val qrScannerUtil = cameraCtrlState.qrScannerUtil
    val dialogState by qrCodeScannerViewModel.uiDialog.collectAsStateWithLifecycle()

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
        floatingActionButton = { CodeFloatingActionButton(onClickCodeDialog = qrCodeScannerViewModel::onClickCodeDialog) }
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = modifier.padding(innerPadding)
        ) {
            if(!dialogState.codeDialogState){
                AndroidView(
                    modifier = modifier
                        .size(500.dp),
                    factory = { ctx ->
                        PreviewView(ctx).apply {
                            cameraCtrl.cameraController.setImageAnalysisAnalyzer(
                                ContextCompat.getMainExecutor(ctx),
                                MlKitAnalyzer(
                                    listOf(qrScannerUtil.qrScanner),
                                    ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED,
                                    ContextCompat.getMainExecutor(ctx)
                                ) { result: MlKitAnalyzer.Result? ->

                                    val qrCodeResults = result?.getValue(qrScannerUtil.qrScanner)
                                    if(!qrCodeResults.isNullOrEmpty()) {
                                        val tempVar = qrCodeResults.first().rawValue!!
                                        if(tempVar.startsWith("monopro")) {
                                            qrCodeScannerViewModel.saveQrCodeAndNavigateToPropertyScreen(
                                                qrCode =  tempVar,
                                                qrScannerInput = true,
                                                navigateToPropertyScreen = navigateToPropertyScreen
                                            )
                                            qrCodeScannerViewModel.unbindCameraController()
                                        } else if (tempVar.startsWith("monoeve")) {
                                            qrCodeScannerViewModel.saveQrCodeAndNavigateToEventScreen(
                                                qrCode = tempVar,
                                                qrScannerInput = true,
                                                navigateToEventScreen = navigateToEventScreen
                                            )
                                            qrCodeScannerViewModel.unbindCameraController()
                                        } else if (tempVar.startsWith("collect")){
                                            qrCodeScannerViewModel.unbindCameraController()
                                            navigateToCollect200()
                                        } else {
                                            qrCodeScannerViewModel.showWrongQrCodeSnackbar()
                                        }
                                    }
                                }
                            )
                            cameraCtrl.cameraController.bindToLifecycle(lifecycleOwner)
                            this.controller = cameraCtrl.cameraController
                        }
                    }
                )
            } else {
                cameraCtrl.cameraController.unbind()
            }
        }

        if(dialogState.codeDialogState){
            CodeDialog(
                onClickCodeDialog = qrCodeScannerViewModel::onClickCodeDialog,
                setQrCode = qrCodeScannerViewModel::setQrCode,
                qrCode = qrState.qrCode,
                saveQrCodeAndNavigateToPropertyScreen = qrCodeScannerViewModel::saveQrCodeAndNavigateToPropertyScreen,
                saveQrCodeAndNavigateToEventScreen = qrCodeScannerViewModel::saveQrCodeAndNavigateToEventScreen,
                navigateToPropertyScreen = navigateToPropertyScreen,
                navigateToEventScreen = navigateToEventScreen,
                unBindCamera = qrCodeScannerViewModel::unbindCameraController,
                radioOptions = dialogState.radioOptions,
                selectedOption = dialogState.selectedOption,
                onOptionSelected = qrCodeScannerViewModel::onOptionSelected,
                navigateToCollect200 = navigateToCollect200
            )
        }
    }
}

@Composable
fun RadioButtonSingleSelection(
    radioOptions: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.selectableGroup()) {
        radioOptions.forEach { text ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = { onOptionSelected(text) },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (text == selectedOption),
                    onClick = null,
                    modifier = modifier.padding(vertical = 12.dp)
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun CodeDialog(
    onClickCodeDialog: () -> Unit,
    setQrCode: (String) -> Unit,
    qrCode: String,
    saveQrCodeAndNavigateToPropertyScreen: (String, Boolean, () -> Unit) -> Unit,
    saveQrCodeAndNavigateToEventScreen: (String, Boolean, () -> Unit) -> Unit,
    navigateToPropertyScreen: () -> Unit,
    navigateToEventScreen: () -> Unit,
    navigateToCollect200: () -> Unit,
    unBindCamera: () -> Unit,
    radioOptions: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog (
        onDismissRequest = {
            setQrCode("")
            onClickCodeDialog()
        },
        title = { Text(text = "Card Code", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column {
                Text(
                    text = "Enter a card code.",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = modifier.padding(bottom = 4.dp)
                )
                RadioButtonSingleSelection(
                    radioOptions = radioOptions,
                    selectedOption = selectedOption,
                    onOptionSelected = onOptionSelected,
                )
                Spacer(modifier = modifier.padding(vertical = 8.dp))
                OutlinedTextField(
                    value = qrCode,
                    onValueChange = setQrCode,
                    label = { Text(text = stringResource(R.string.qr_code), style = MaterialTheme.typography.bodyLarge) },
                    visualTransformation = VisualTransformation.None,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done),
                    supportingText = {
                        if(qrCode.isNotBlank()) {
                            if(qrCode.toInt() !in 1..22) {
                                Text(
                                    text = "Must be between 1 to 22.",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = modifier.padding(bottom =  4.dp)
                                )
                            }
                        }
                    },
                    isError = (qrCode.isNotBlank() && (qrCode.toInt() !in 1..22)),
                    enabled = (!selectedOption.startsWith("Collect"))
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    setQrCode("")
                    onClickCodeDialog()
                }
            ) {
                Text(text = stringResource(R.string.dismiss), style = MaterialTheme.typography.bodyLarge)
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onClickCodeDialog()
                    unBindCamera()
                    if(selectedOption.startsWith("Property")) {
                        saveQrCodeAndNavigateToPropertyScreen(
                            qrCode,
                            false,
                            navigateToPropertyScreen
                        )
                    } else if(selectedOption.startsWith("Event")) {
                        saveQrCodeAndNavigateToEventScreen(
                            qrCode,
                            false,
                            navigateToEventScreen
                        )
                    } else if(selectedOption.startsWith("Collect")) {
                        navigateToCollect200()
                    }
                },
                enabled = ((qrCode.isNotBlank() && (qrCode.toInt() in 1..22)) || selectedOption.startsWith("Collect"))
            ) {
                Text(text = stringResource(R.string.confirm), style = MaterialTheme.typography.bodyLarge)
            }
        }
    )
}

@Composable
private fun CodeFloatingActionButton(
    onClickCodeDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClickCodeDialog,
        shape = MaterialTheme.shapes.medium
    ) {
        Icon(
            painter = painterResource(R.drawable.baseline_dialpad_24),
            contentDescription = stringResource(R.string.qr_code),
            modifier = modifier.size(32.dp)
        )
    }
}