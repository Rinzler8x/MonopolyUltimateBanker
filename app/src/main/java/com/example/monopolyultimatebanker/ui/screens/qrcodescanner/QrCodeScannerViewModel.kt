package com.example.monopolyultimatebanker.ui.screens.qrcodescanner

import android.content.Context
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.monopolyultimatebanker.utils.QrScanner
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

data class QrCodeState(
    val qrCode: String = "",
    val qrCodeDetected: Boolean = false,
)

data class CameraControlState(
    val cameraController: LifecycleCameraController,
    val qrScannerUtil: QrScanner = QrScanner
)

data class DialogState(
    val codeDialog: Boolean = false
)

@HiltViewModel
class QrCodeScannerViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context
): ViewModel() {

    var qrState by mutableStateOf(QrCodeState())
        private set

    var cameraCtrlState by mutableStateOf(CameraControlState(LifecycleCameraController(appContext)))

    fun setQrCode(input: String){
        qrState = qrState.copy(qrCode = input)
    }

    fun setQrCodeDetected(input: Boolean) {
        qrState = qrState.copy(qrCodeDetected = input)
    }

    fun getCameraController(context: Context): CameraControlState{
        cameraCtrlState = cameraCtrlState.copy(cameraController = LifecycleCameraController(context))
        return cameraCtrlState
    }

    var dialogState by mutableStateOf(DialogState())
        private set

    fun onClickCodeDialog(){
        dialogState = dialogState.copy(codeDialog = !dialogState.codeDialog)
    }

}