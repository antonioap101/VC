package es.ulpgc.signteach.ui.screens
// Añade importaciones necesarias
import android.content.Context
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Surface
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OutputFileOptions
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.camera.view.RotationProvider
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
import es.ulpgc.signteach.ui.theme.toggleCameraRectangleColor
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
import kotlin.random.Random

// Función para seleccionar una letra aleatoria
private fun  selectRandomLetter(): Char {
    val letters = ('A'..'Z').toList()
    return letters[Random.nextInt(letters.size)]
}

@Composable
fun CameraModeToggle(mode: MutableState<String>) {
    // Estilos para el botón activo e inactivo
    val iconSize = 48.dp // Tamaño de los iconos

    Box(
        modifier = Modifier
            .padding(0.dp)
            .background(
                color = toggleCameraRectangleColor, // Cambia el color de fondo según tus preferencias
                shape = RoundedCornerShape(32.dp) // Define el radio de los bordes redondeados
            )
    ) {
        Row(modifier = Modifier.padding(2.dp)) {
            // Botón Modo Foto
            IconButton(onClick = { mode.value = "photo" }) {
                Icon(
                    painter = painterResource(id = if (mode.value == "photo") R.drawable.ic_selected_photo else R.drawable.ic_unselected_photo),
                    contentDescription = "Modo Foto",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(iconSize) // Tamaño del icono
                )
            }

            // Botón Modo Video
            IconButton(onClick = { mode.value = "video" }) {
                Icon(
                    painter = painterResource(id = if (mode.value == "video") R.drawable.ic_selected_video else R.drawable.ic_unselected_video),
                    contentDescription = "Modo Video",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(iconSize) // Tamaño del icono
                )
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(activity: MainActivity, serverInteraction: ServerInteraction) {
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_FRONT) }
    val executor = ContextCompat.getMainExecutor(context)
    val previewView = remember { PreviewView(context) }
    val cameraProvider = cameraProviderFuture.get()

    // Estado para manejar el modo actual (foto o video)
    val mode = remember { mutableStateOf("photo") } // "photo" o "video"

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

    var score by remember { mutableIntStateOf(0) } // Estado para la puntuación


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
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Barra superior con la puntuación
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_score), // Reemplaza con el icono que desees
                        contentDescription = "Puntuación",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "$score",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 12.dp) // Espacio entre el icono y el texto
                    )
                }
            },

            modifier = Modifier.fillMaxWidth()
        )

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


        Row(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,

        ) {
            IconButton(onClick = {
                lensFacing = if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
                    CameraSelector.LENS_FACING_BACK
                } else {
                    CameraSelector.LENS_FACING_FRONT
                }
            }, modifier = Modifier.size(70.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_rotate_camera),
                    contentDescription = "Rotar Cámara",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(70.dp)
                )
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
                            messageAndBackgroundColor =
                                Pair(countdownMessage, Color.Blue.copy(alpha = 0.5f))
                            delay(2000)
                            for (i in 3 downTo 1) {
                                countdownMessage = "$i..."
                                messageAndBackgroundColor =
                                    Pair(countdownMessage, Color.Blue.copy(alpha = 0.5f))
                                delay(1000) // Espera 1 segundo
                            }

                            val analysisResult: Int = if (mode.value == "video") {
                                recordAndSendVideo(
                                    activity,
                                    context,
                                    serverInteraction,
                                    previewView,
                                    lensFacing,
                                    currentRecording,
                                    onRecordingStarted = {
                                        isRecording = true
                                        messageAndBackgroundColor =
                                            Pair("Capturando...", Color.Black.copy(0.5f))
                                    },
                                    onRecordingStopped = {
                                        isRecording = false
                                        messageAndBackgroundColor =
                                            Pair("Analizando...", Color.Black.copy(0.5f))
                                    }
                                )
                            } else {
                                takeAndSendPicture(
                                    activity,
                                    context,
                                    serverInteraction,
                                    previewView,
                                    lensFacing
                                )
                            }
                            // Configura el mensaje y el color del fondo en función del resultado del análisis
                            Log.d("ServerResponse", "AnalysisResult: $analysisResult")

                            if (analysisResult == 0) {score +=1}
                            else if (analysisResult == 1 && score > 0) {score -=1}

                            messageAndBackgroundColor = when (analysisResult) {
                                0 -> Pair("Correcto!", Color.Green.copy(alpha = 0.5f))
                                1 -> Pair("Incorrecto!", Color.Red.copy(alpha = 0.5f))
                                else -> Pair(
                                    "Error del servidor!",
                                    serverErrorColor.copy(alpha = 0.5f)
                                )
                            }
                            delay(2000)
                            messageAndBackgroundColor = Pair("", Color.Green.copy(alpha = 0f))

                        }
                    }
                },

                // Cambiar el color del botón según el estado de grabación
                colors = ButtonDefaults.buttonColors(if (recordingJob.value?.isActive == true) recordingColor else notRecordingColor),
            ) {
                Text(text = if (recordingJob.value?.isActive == true) "Cancelar" else "Jugar",
                    fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            // Botón para enviar la imagen almacenada en recursos
            IconButton(
                onClick = {
                    // Llama a la función para enviar la imagen al servidor
                    /* serverInteraction.sendImageResourceToServer(
                        context,
                        R.drawable.test_image_l,
                        "A"
                    )*/
                    score = 0 // Resetea la puntuación
                },
                modifier = Modifier.size(65.dp)

            ) {
                Icon(painter = painterResource(id = R.drawable.ic_reset),
                    contentDescription = "Reset/Send Test Image",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(65.dp)
                )
            }
        }

        CameraModeToggle(mode)
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



suspend fun takeAndSendPicture(
    activity: MainActivity,
    context: Context,
    serverInteraction: ServerInteraction,
    previewView: PreviewView,
    lensFacing: Int
): Int {
    // Creamos una instancia de ImageCapture
    //val imageCapture = ImageCapture.Builder().build()

    val imageCapture = ImageCapture.Builder()
        .setResolutionSelector(ResolutionSelector.Builder()
            .setAspectRatioStrategy(AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY)
            .build())
        .setTargetRotation(Surface.ROTATION_0)
        .build()

    val resultDeferred = CompletableDeferred<Int>()
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        try {
            // Desvincula cualquier uso de la cámara en uso
            cameraProvider.unbindAll()

            // Vincula el uso de la cámara para captura de imagen
            cameraProvider.bindToLifecycle(
                activity,
                cameraSelector,
                preview,
                imageCapture
            )

            // Crear archivo para guardar la imagen
            val photoFile = createImageFile(context)
            val outputOptions = OutputFileOptions.Builder(photoFile).build()

            // Tomar la foto y guardarla en el archivo
            imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        // La imagen se ha guardado correctamente
                        CoroutineScope(Dispatchers.IO).launch {
                            val result = serverInteraction.sendImageToServer(photoFile, "A")
                            resultDeferred.complete(result)
                        }
                    }

                    override fun onError(exception: ImageCaptureException) {
                        // Manejar el error
                        resultDeferred.complete(-1)
                        Log.e("CameraXApp", "Photo capture failed: ${exception.message}", exception)
                    }
                }
            )
        } catch (exc: Exception) {
            Log.e("CameraXApp", "Use case binding failed", exc)
            resultDeferred.complete(-1)
        }
    }, ContextCompat.getMainExecutor(context))

    return resultDeferred.await()

}

fun createImageFile(context: Context): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", // prefix
            ".jpg", // suffix
            storageDir // directory
        )
}


