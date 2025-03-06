package com.example.monopolyultimatebanker.ui.screens.qrcodescanner

import android.content.Context
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val codeDialog: Boolean = false
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
        qrState = qrState.copy(qrCode = input)
        viewModelScope.launch {
            qrPreferencesRepository.saveProQrPreference(qrState.qrCode)
        }
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

    fun saveQrCodeAndNavigateToPropertyScreen(
        qrCode: String,
        navigateToPropertyScreen: () -> Unit
    ) {
        viewModelScope.launch {
            setQrCode(qrCode)
            qrPreferencesRepository.saveProQrPreference(qrState.qrCode)
            navigateToPropertyScreen()
        }
    }

    fun saveQrCodeAndNavigateToEventScreen(
        qrCode: String,
//        navigateToEventScreen: () -> Unit
    ) {
        viewModelScope.launch {
            setQrCode(qrCode)
            qrPreferencesRepository.saveEveQrPreference(qrState.qrCode)
//            navigateToEventScreen()
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