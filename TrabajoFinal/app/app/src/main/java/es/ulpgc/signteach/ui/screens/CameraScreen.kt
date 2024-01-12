package es.ulpgc.signteach.ui.screens

import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import es.ulpgc.signteach.MainActivity
import es.ulpgc.signteach.R
import es.ulpgc.signteach.ServerInteraction

@Composable
fun CameraScreen(activity: MainActivity, serverInteraction: ServerInteraction) {
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_FRONT) }
    val executor = ContextCompat.getMainExecutor(context)
    val previewView = remember { PreviewView(context) }
    val cameraProvider = cameraProviderFuture.get()

    LaunchedEffect(lensFacing) {
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                activity,
                cameraSelector,
                Preview.Builder().build()
                    .also { it.setSurfaceProvider(previewView.surfaceProvider) }
            )
        } catch (exc: Exception) {
            // Handle any errors (for example, if the camera is in use or does not exist).
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )

        Button(onClick = {
            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
                CameraSelector.LENS_FACING_BACK
            } else {
                CameraSelector.LENS_FACING_FRONT
            }
        }) {
            Text(text = "Cambiar Cámara")
        }
        // Botón para enviar la imagen almacenada en recursos
        Button(
            onClick = {
                // Llama a la función para enviar la imagen al servidor
                serverInteraction.sendImageResourceToServer(context, R.drawable.test_image_l, "A")

            },
            modifier = Modifier.padding(16.dp) // Añade margen al botón
        ) {
            Text(text = "Enviar Imagen de Test")
        }
    }
}
