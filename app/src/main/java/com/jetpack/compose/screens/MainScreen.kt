package com.jetpack.compose.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
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
    pickerButton: @Composable (onClick: () -> Unit) -> Unit,
) {
    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                onImageSelected(uri)
            }
            if (uri != null) {
                Timber.e("Media selected: $uri")
            } else {
                Timber.e("No media selected")
            }
        }

    pickerButton {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}

@Composable
fun CaptureImage(
    onCaptured: (Uri) -> Unit = {},
    captureButton: @Composable (onClick: () -> Unit) -> Unit,
) {
    val context = LocalContext.current
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoUri?.let(onCaptured)
        } else {
            photoUri = null
        }
    }

    captureButton {
        val uri = createImageUri(context)
        photoUri = uri
        takePictureLauncher.launch(uri)
    }
}

fun createImageUri(context: Context): Uri {
    val dir = File(context.cacheDir, "images").apply { mkdirs() }
    val file = File(dir, "camera_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}

@Composable
fun UriPreview(uri: Uri?) {
    val context = LocalContext.current
    var img by remember(uri) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(uri) {
        img = uri?.let { uriToImageBitmap(context, it) }
    }

    img?.let {
        Image(
            modifier = Modifier.size(200.dp),
            bitmap = it,
            contentDescription = "Captured image",
        )
    }
}

suspend fun uriToImageBitmap(
    context: Context,
    uri: Uri,
): ImageBitmap? = withContext(Dispatchers.IO) {
    runCatching {
        val bitmap: Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.isMutableRequired = false
            }
        } else {
            context.contentResolver.openInputStream(uri)?.use { input ->
                BitmapFactory.decodeStream(input)
            } ?: return@withContext null
        }
        bitmap.asImageBitmap()
    }.getOrNull()
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    MainScreen()
}