package com.jetpack.compose.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jetpack.compose.ImageUtils
import timber.log.Timber

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        GalleryImagPicker(
            onImageSelected = {
                imageUri = it
                Timber.e("Selected image: $it")
            },
            onError = {
                Timber.e("Failed to select image")
            }
        ) {
            Button(
                onClick = it,
            ) {
                Text("Pick from gallery")
            }
        }
        Spacer(modifier.height(15.dp))
        CaptureImage(
            onCaptured = {
                imageUri = it
                Timber.e("Captured image: $it")
            },
            onError = {
                Timber.e("Failed to capture image")
            }
        ) {
            Button(onClick = it) {
                Text("Capture image")
            }
        }

        UriPreview(imageUri)
    }
}

@Composable
private fun GalleryImagPicker(
    onImageSelected: (Uri) -> Unit,
    onError: () -> Unit,
    pickerButton: @Composable (onClick: () -> Unit) -> Unit,
) {
    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                onImageSelected(uri)
            }
            if (uri != null) {
                Timber.e("Media selected: $uri")
            } else onError.invoke()
        }

    pickerButton {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}

@Composable
fun CaptureImage(
    onCaptured: (Uri) -> Unit,
    onError: () -> Unit,
    captureButton: @Composable (onClick: () -> Unit) -> Unit,
) {
    val context = LocalContext.current
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoUri?.let(onCaptured)
        } else onError.invoke()
    }

    captureButton {
        val uri = ImageUtils.createImageUri(context)
        photoUri = uri
        takePictureLauncher.launch(uri)
    }
}

@Composable
fun UriPreview(uri: Uri?) {
    val context = LocalContext.current
    var img by remember(uri) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(uri) {
        img = uri?.let { ImageUtils.convertUriToImageBitmap(context, it) }
    }

    img?.let {
        Image(
            modifier = Modifier.size(200.dp),
            bitmap = it,
            contentDescription = "Captured image",
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    MainScreen()
}