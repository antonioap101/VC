package es.ulpgc.signteach.ui.screens
// Añade importaciones necesarias
import kotlin.random.Random
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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import es.ulpgc.signteach.MainActivity
import es.ulpgc.signteach.R
import es.ulpgc.signteach.ServerInteraction
import es.ulpgc.signteach.ui.theme.notRecordingColor
import es.ulpgc.signteach.ui.theme.recordingColor
import es.ulpgc.signteach.ui.theme.serverErrorColor
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Función para seleccionar una letra aleatoria
private fun  selectRandomLetter(): Char {
    val letters = ('A'..'Z').toList()
    return letters[Random.nextInt(letters.size)]
}

@Composable
fun CameraScreen(activity: MainActivity, serverInteraction: ServerInteraction) {
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_FRONT) }
    val executor = ContextCompat.getMainExecutor(context)
    val previewView = remember { PreviewView(context) }
    val cameraProvider = cameraProviderFuture.get()

    val currentRecording = remember { mutableStateOf<Recording?>(null) }

    // Estado para controlar si la grabación está en curso
    var isRecording by remember { mutableStateOf(false) }

    // Estado para controlar si se muestra el mensaje de "Correcto" o "Incorrecto" y el color del fondo
    var messageAndBackgroundColor by remember { mutableStateOf<Pair<String, Color>>(Pair("", Color.Black.copy(alpha = 0f))) }

    // Estado para mantener la letra seleccionada y el mensaje de cuenta regresiva
    var selectedLetter by remember { mutableStateOf('A') }
    var countdownMessage by remember { mutableStateOf("") }

    // Job para la corrutina de grabación
    val recordingJob = remember { mutableStateOf<Job?>(null) }

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            AndroidView(
                factory = { previewView },
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background((messageAndBackgroundColor.second)), // Fondo semitransparente
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isRecording) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                    Text(
                        text = messageAndBackgroundColor.first,
                        color = Color.White,
                        fontSize = 20.sp
                    )
                }
            }
        }

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
                if (recordingJob.value?.isActive == true) {
                    // Si ya hay una grabación en curso, la cancela
                    recordingJob.value?.cancel()
                    isRecording = false
                    messageAndBackgroundColor = Pair("", Color.Transparent)
                } else {
                    selectedLetter = 'A' // selectRandomLetter()
                    recordingJob.value = CoroutineScope(Dispatchers.Default).launch {
                        countdownMessage = "Prepárate para $selectedLetter en..."
                        messageAndBackgroundColor = Pair(countdownMessage, Color.Blue.copy(alpha = 0.5f))
                        delay(2000)
                        for (i in 3 downTo 1) {
                            countdownMessage = "$i..."
                            messageAndBackgroundColor = Pair(countdownMessage, Color.Blue.copy(alpha = 0.5f))
                            delay(1000) // Espera 1 segundo
                        }

                        val analysisResult: Int = recordAndSendVideo(
                            activity,
                            context,
                            serverInteraction,
                            previewView,
                            lensFacing,
                            currentRecording,
                            onRecordingStarted = { isRecording = true
                                                 messageAndBackgroundColor = Pair("Capturando...", Color.Black.copy(0.5f))
                                                 },
                            onRecordingStopped = { isRecording = false
                                                messageAndBackgroundColor = Pair("Analizando...", Color.Black.copy(0.5f))}
                        )
                        // Configura el mensaje y el color del fondo en función del resultado del análisis
                        Log.d("ServerResponse", "AnalysisResult: $analysisResult")

                        messageAndBackgroundColor = when (analysisResult) {
                            0 -> Pair("Correcto!", Color.Green.copy(alpha = 0.5f))
                            1 -> Pair("Incorrecto!", Color.Red.copy(alpha = 0.5f))
                            else -> Pair("Error del servidor!", serverErrorColor.copy(alpha = 0.5f))
                        }
                        delay(2000)
                        messageAndBackgroundColor = Pair("", Color.Green.copy(alpha = 0f))

                    }
                }
            },
            modifier = Modifier.padding(16.dp),
            // Cambiar el color del botón según el estado de grabación
            colors = ButtonDefaults.buttonColors(if (recordingJob.value?.isActive == true) recordingColor else notRecordingColor),
        ) {
            Text(text = if (recordingJob.value?.isActive == true) "Cancelar Grabación" else "Grabar y Enviar Vídeo")
        }
    }
}

suspend fun recordAndSendVideo(activity: MainActivity,
                       context: Context,
                       serverInteraction: ServerInteraction,
                       previewView: PreviewView,
                       lensFacing: Int,
                       currentRecording: MutableState<Recording?>,
                       onRecordingStarted: () -> Unit,
                       onRecordingStopped: () -> Unit): Int {


    val resultDeferred = CompletableDeferred<Int>()
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
                        if (!recordEvent.hasError()) { // Video grabado exitosamente -> Enviar el video al servidor
                            CoroutineScope(Dispatchers.Main).launch {
                                Log.d("RecorderMsg: ", "Vídeo grabado. Enviando....")
                                val result = withContext(Dispatchers.IO) {
                                    serverInteraction.sendVideoToServer(videoFile, "A")
                                }
                                Log.d("ServerResponse", "result = {$result}")
                                resultDeferred.complete(result)
                            }
                        } else {
                            Log.e("RecorderMsg: ", "Error o Cancelación de grabación")
                            resultDeferred.complete(-1)
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

    return resultDeferred.await()
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
