package com.jetpack.compose.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.jetpack.compose.screens.animation.WhatsAppVoiceRecordingUi

@Composable
fun MainScreenRoute(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.action(UiAction.FetchItems)
    }
//    MainScreen(
//        modifier = modifier,
//        uiState = uiState,
//        action = {
//            viewModel.action(it)
//        }
//    )

    //AnimatedExpandCollapsed(modifier)

    //VerticalBouncingAnimation()
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        MicUi()
    }
}

@Composable
private fun MicUi(
    modifier: Modifier = Modifier,
) {
    WhatsAppVoiceRecordingUi(
        onStartRecording = { /* recorder.start() */ },
        onCancelRecording = { /* recorder.cancelAndDelete() */ },
        onStopAndSend = { /* recorder.stop(); send() */ },
        onLocked = { /* keep recording, show stop/send UI */ },
        onStopLocked = { /* recorder.stop(); send or discard */ }
    )

}