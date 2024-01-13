package es.ulpgc.signteach.ui.screens

import android.content.Context
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import es.ulpgc.signteach.MainActivity
import es.ulpgc.signteach.R
import es.ulpgc.signteach.ServerInteraction
import es.ulpgc.signteach.ui.theme.notRecordingColor
import es.ulpgc.signteach.ui.theme.recordingColor
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun CameraScreen(activity: MainActivity, serverInteraction: ServerInteraction) {
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_FRONT) }
    val executor = ContextCompat.getMainExecutor(context)
    val previewView = remember { PreviewView(context) }
    val cameraProvider = cameraProviderFuture.get()
    val isRecordingStoppedManually = remember { mutableStateOf(false) }
    val currentRecording = remember { mutableStateOf<Recording?>(null) }

    // Estado para controlar si la grabación está en curso
    var isRecording by remember { mutableStateOf(false) }

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

        // Botón para grabar y enviar video
        Button(
            onClick = {
                if (!isRecording) { // Si no se está grabando ya, comenzar a grabar
                    isRecordingStoppedManually.value = false
                    recordAndSendVideo(
                        activity, context, serverInteraction, previewView, lensFacing, currentRecording,
                        isRecordingStoppedManually = isRecordingStoppedManually,
                        onRecordingStarted = { isRecording = true },
                        onRecordingStopped = { isRecording = false },
                    )
                } else {
                    currentRecording.value?.stop()
                    currentRecording.value = null // Resetear la grabación actual
                    isRecording = false
                    isRecordingStoppedManually.value = true
                }
            },
            modifier = Modifier.padding(16.dp),
            // Cambiar el color del botón según el estado de grabación
            colors = ButtonDefaults.buttonColors(if (isRecording) recordingColor else notRecordingColor),
        ) {
            Text(text = if (isRecording) "Cancelar Grabación" else "Grabar y Enviar Vídeo")
        }

    }
}

@Composable
fun RecordButton(
    isRecording: Boolean,
    onRecordStart: () -> Unit,
    onRecordStop: () -> Unit
) {
    val buttonColors = if (isRecording) recordingColor else notRecordingColor

    Button(
        onClick = {
            if (!isRecording) {
                onRecordStart()
            } else {
                onRecordStop()
            }
        },
        modifier = Modifier.padding(16.dp),
        colors = ButtonDefaults.buttonColors(buttonColors)
    ) {
        Text(text = if (isRecording) "Cancelar Grabación" else "Grabar y Enviar Vídeo")
    }
}

fun recordAndSendVideo(activity: MainActivity,
                       context: Context,
                       serverInteraction: ServerInteraction,
                       previewView: PreviewView,
                       lensFacing: Int,
                       currentRecording: MutableState<Recording?>,
                       isRecordingStoppedManually: MutableState<Boolean>,
                       onRecordingStarted: () -> Unit,
                       onRecordingStopped: () -> Unit) {

    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()

        // Preparar el Recorder
        val recorder = Recorder.Builder()
            .setQualitySelector(QualitySelector.from(Quality.HD))
            .build()

        val videoCapture = VideoCapture.withOutput(recorder)

        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        try {
            // Desvincula cualquier uso de la cámara en uso
            cameraProvider.unbindAll()

            // Vincula el uso de la cámara para grabación de video
            cameraProvider.bindToLifecycle(
                activity,
                cameraSelector,
                preview,
                videoCapture
            )

            // Crear archivo para guardar el video
            val videoFile = createVideoFile(context)
            val outputOptions = FileOutputOptions.Builder(videoFile).build()

            // Preparar y comenzar la grabación
            val recording = videoCapture.output.prepareRecording(context,outputOptions)
                .apply {
                    // Aquí puedes configurar opciones adicionales si lo necesitas
                }
                .start(ContextCompat.getMainExecutor(context)) { recordEvent ->
                    // Manejar eventos de grabación
                    if (recordEvent is VideoRecordEvent.Finalize) {
                        if (!recordEvent.hasError() && !isRecordingStoppedManually.value) { // Video grabado exitosamente -> Enviar el video al servidor
                            Log.d("RecorderMsg: ", "Vídeo grabado. Enviando....")
                            serverInteraction.sendVideoToServer(videoFile, "A") // Asumiendo "A" como targetLetter
                        } else {
                            Log.e("RecorderMsg: ", "Error o Cancelación de grabación")
                        }

                    }

                }
            // Actualiza el estado de la grabación actual
            currentRecording.value = recording
            onRecordingStarted()

            // Detener la grabación después de 5 segundos
            Handler(Looper.getMainLooper()).postDelayed({
                onRecordingStopped()
                currentRecording.value?.stop()
            }, 5000) // 5000 ms = 5 segundos

        } catch (e: Exception) {
            // Manejar excepciones
        }
    }, ContextCompat.getMainExecutor(context))
}
fun createVideoFile(context: Context): File {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)!!
    return File.createTempFile(
        "VIDEO_${timeStamp}_", /* prefix */
        ".mp4", /* suffix */
        storageDir /* directory */
    )
}

