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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QrCodeState(
    val qrCode: String = "",
)

data class CameraControlState(
    val cameraController: LifecycleCameraController,
    val qrScannerUtil: QrScanner = QrScanner
)

data class QrCodeDialogState(
    val codeDialogState: Boolean = false,
    val radioOptions: List<String> = listOf("Property Card", "Event Card", "Collect 200"),
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

    fun setQrCode(input: String){
        qrState = qrState.copy(qrCode = input.trim())
    }

    private val _uiCameraCtrl = MutableStateFlow(CameraControlState(LifecycleCameraController(appContext)))
    val uiCameraCtrl: StateFlow<CameraControlState> = _uiCameraCtrl.asStateFlow()

    fun getCameraController(context: Context): CameraControlState{
        _uiCameraCtrl.update { currentState ->
            currentState.copy(
                cameraController = LifecycleCameraController(context)
            )
        }
        return uiCameraCtrl.value
    }

    private val _uiDialog = MutableStateFlow(QrCodeDialogState())
    val uiDialog: StateFlow<QrCodeDialogState> = _uiDialog.asStateFlow()

    fun onClickCodeDialog(){
        _uiDialog.update { currentState ->
            currentState.copy(
                codeDialogState = !_uiDialog.value.codeDialogState
            )
        }
    }

    fun onOptionSelected(input: String) {
        _uiDialog.update { currentState ->
            if(input.startsWith("Property")) {
                currentState.copy(prefixValue = "monopro_", selectedOption = input)
            } else {
                currentState.copy(prefixValue = "monoeve_", selectedOption = input)
            }
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
                setQrCode(uiDialog.value.prefixValue + qrCode)
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
                setQrCode(uiDialog.value.prefixValue + qrCode)
            }
            qrPreferencesRepository.saveEveQrPreference(qrState.qrCode)
            navigateToEventScreen()
        }
    }

    fun unbindCameraController() {
        uiCameraCtrl.value.cameraController.unbind()
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