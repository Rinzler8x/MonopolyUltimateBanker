package com.example.monopolyultimatebanker.ui.screens.qrcodescanner

import android.Manifest
import androidx.camera.core.ImageAnalysis
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.monopolyultimatebanker.R
import com.example.monopolyultimatebanker.ui.navigation.NavigationDestination
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

object QrCodeScannerDestination: NavigationDestination {
    override val route = "qr_code_scanner"
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QrCodeScanner(
    modifier: Modifier = Modifier,
    navigateTo: () -> Unit,
    qrCodeScannerViewModel: QrCodeScannerViewModel = hiltViewModel()
) {

    val qrState = qrCodeScannerViewModel.qrState
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraCtrl = qrCodeScannerViewModel.getCameraController(context)
    val qrScannerUtil = qrCodeScannerViewModel.cameraCtrlState.qrScannerUtil
    val dialogState = qrCodeScannerViewModel.dialogState

    Scaffold(
        floatingActionButton = { CodeFloatingActionButton(onClickCodeDialog = qrCodeScannerViewModel::onClickCodeDialog) }
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = modifier.padding(innerPadding)
        ) {
            if(!dialogState.codeDialog){
                AndroidView(
                    modifier = modifier
//                        .padding(innerPadding)
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
                                        qrCodeScannerViewModel.setQrCode(qrCodeResults.first().rawValue!!)
                                        qrCodeScannerViewModel.navigateToPropertyScreen(navigateTo)
                                    }
                                }
                            )
                            cameraCtrl.cameraController.bindToLifecycle(lifecycleOwner)
                            this.controller = cameraCtrl.cameraController
                        }
                    }
                )
            } else {
                // Dialog is open, stop camera
                cameraCtrl.cameraController.unbind()
            }
            Text(text = qrState.qrCode, modifier.fillMaxSize())
        }

        if(dialogState.codeDialog){
            CodeDialog(
                onClickCodeDialog = qrCodeScannerViewModel::onClickCodeDialog,
                setQrCode = qrCodeScannerViewModel::setQrCode,
                qrCode = qrState.qrCode,
                navigateTo = navigateTo
            )
        }
    }
}

@Composable
private fun CodeDialog(
    onClickCodeDialog: () -> Unit,
    setQrCode: (String) -> Unit,
    qrCode: String,
    navigateTo: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog (
        onDismissRequest = {
            setQrCode("")
            onClickCodeDialog()
        },
        title = { Text("Card Code") },
        text = {
            Column {
                Text(
                    text = "Enter a card code.",
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                )
                Spacer(modifier = modifier.padding(vertical = 8.dp))
                OutlinedTextField(
                    value = qrCode,
                    onValueChange = setQrCode,
                    label = { Text(text = stringResource(R.string.qr_code)) }
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
                Text(text = stringResource(R.string.dismiss))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onClickCodeDialog()
                    navigateTo()
                },
                enabled = qrCode.isNotBlank()
            ) {
                Text(text = stringResource(R.string.confirm))
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
        onClick = onClickCodeDialog
    ) {
        Icon(
            painter = painterResource(R.drawable.baseline_dialpad_24),
            contentDescription = stringResource(R.string.qr_code),
            modifier = modifier.size(32.dp)
        )
    }
}