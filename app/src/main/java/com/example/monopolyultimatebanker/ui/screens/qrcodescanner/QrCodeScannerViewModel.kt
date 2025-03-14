package com.example.monopolyultimatebanker.ui.screens.qrcodescanner

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.copy
import com.example.monopolyultimatebanker.data.preferences.QrPreferencesRepository
import com.example.monopolyultimatebanker.utils.QrScanner
import com.example.monopolyultimatebanker.utils.SnackbarController
import com.example.monopolyultimatebanker.utils.SnackbarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QrCodeState(
    val qrCode: String = "",
)

data class CameraControlState(
    val cameraController: LifecycleCameraController,
    val qrScannerUtil: QrScanner = QrScanner
)

data class DialogState(
    val codeDialog: Boolean = false,
    val radioOptions: List<String> = listOf("Property Card", "Event Card"),
    val selectedOption: String = radioOptions[0],
    val prefixValue: String = "monopro_"
)

@HiltViewModel
class QrCodeScannerViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val qrPreferencesRepository: QrPreferencesRepository,
): ViewModel() {

    var qrState by mutableStateOf(QrCodeState())
        private set

    var cameraCtrlState by mutableStateOf(CameraControlState(LifecycleCameraController(appContext)))

    fun setQrCode(input: String){
        qrState = qrState.copy(qrCode = input.trim())
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

    fun onOptionSelected(input: String) {
        dialogState = if(input.startsWith("Property")) {
            dialogState.copy(prefixValue = "monopro_", selectedOption = input)
        } else {
            dialogState.copy(prefixValue = "monoeve_", selectedOption = input)
        }
    }

    fun saveQrCodeAndNavigateToPropertyScreen(
        qrCode: String,
        qrScannerInput: Boolean,
        navigateToPropertyScreen: () -> Unit
    ) {
        viewModelScope.launch {
            if(qrScannerInput) {
                setQrCode(qrCode)
            } else {
                setQrCode(dialogState.prefixValue + qrCode)
            }
            qrPreferencesRepository.saveProQrPreference(qrState.qrCode)
            navigateToPropertyScreen()
        }
    }

    fun saveQrCodeAndNavigateToEventScreen(
        qrCode: String,
        qrScannerInput: Boolean,
        navigateToEventScreen: () -> Unit
    ) {
        viewModelScope.launch {
            if(qrScannerInput) {
                setQrCode(qrCode)
            } else {
                setQrCode(dialogState.prefixValue + qrCode)
            }
            qrPreferencesRepository.saveEveQrPreference(qrState.qrCode)
            navigateToEventScreen()
        }
    }

    fun unbindCameraController() {
        cameraCtrlState.cameraController.unbind()
    }

    fun showWrongQrCodeSnackbar() {
        showSnackBar("QR Code not supported. Please Try Again!")
    }


    private fun showSnackBar(message: String) {
        viewModelScope.launch {
            SnackbarController.sendEvent(
                event = SnackbarEvent(
                    message = message
                )
            )
        }
    }
}