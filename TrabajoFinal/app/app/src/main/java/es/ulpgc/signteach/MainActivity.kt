package es.ulpgc.signteach

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import es.ulpgc.signteach.ui.screens.CameraScreen
import es.ulpgc.signteach.ui.screens.NoCameraAccessScreen
import es.ulpgc.signteach.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private lateinit var serverInteraction: ServerInteraction

    companion object {
        val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa la instancia de ServerInteraction pasando el contexto
        serverInteraction = ServerInteraction(this)

        // Inicializa el ActivityResultLauncher
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            isGranted: Boolean -> setupUI(isGranted)
        }

        // Comprueba si los permisos ya están concedidos
        if (allPermissionsGranted()) {
            setupUI(granted = true)
        } else { // Solicitar permiso de cámara
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }


    private fun setupUI(granted: Boolean) {
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (granted) {
                        CameraScreen(this@MainActivity, serverInteraction)
                    } else {
                        NoCameraAccessScreen(requestPermissionLauncher = requestPermissionLauncher)
                    }
                }
            }
        }
    }

}