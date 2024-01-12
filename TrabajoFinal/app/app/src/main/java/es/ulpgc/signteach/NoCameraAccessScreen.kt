package es.ulpgc.signteach

import android.Manifest
import android.app.Activity
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat

@Composable
fun NoCameraAccessScreen(requestPermissionLauncher: ActivityResultLauncher<String>) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            context as Activity,
            Manifest.permission.CAMERA
        )

        if (showRationale) {
            // El usuario no ha seleccionado "No preguntar de nuevo"
            Text("Se requiere acceso a la cámara para usar la aplicación.")
            Button(onClick = {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }) {
                Text(text = "Solicitar Permiso de Cámara")
            }
        } else {
            // El usuario seleccionó "No preguntar de nuevo"
            Text(
                "El acceso a la cámara es necesario para esta aplicación.\n\n" +
                        "Por favor, habilita el permiso de cámara en la configuración de la aplicación."
            )
        }
    }
}
