package es.ulpgc.signteach

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import androidx.annotation.DrawableRes
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ServerInteraction(private val context: Context) {

    // Define la URL base del servidor
    private val BASE_URL = "http://10.22.146.19:5000"  // Wifi Principal: 10.22.146.19
    // Wifi Movil: 192.168.194.99

    // Crea una instancia de Retrofit con el cliente HTTP y el convertidor Gson
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Crea la instancia de ApiService utilizando Retrofit
    private val apiService = retrofit.create(ApiService::class.java)


    // Función para enviar una imagen almacenada en recursos al servidor
    fun sendImageResourceToServer(
        context: Context,
        @DrawableRes imageResource: Int,
        targetLetter: String
    ) {
        val imageBitmap = BitmapFactory.decodeResource(context.resources, imageResource)
        val imageFile = saveBitmapToFile(context, imageBitmap, "test_image.jpg")

        // Llama a la función para enviar la imagen al servidor
        sendImageToServer(imageFile, targetLetter)
    }

    private fun saveBitmapToFile(context: Context, bitmap: Bitmap, fileName: String): File {
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName)

        try {
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return file
    }

    // Define una función para enviar una imagen al servidor
// Define una función para enviar una imagen al servidor
    private fun sendImageToServer(imageFile: File, targetLetter: String) {
        val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)
        val targetLetterPart = targetLetter.toRequestBody("text/plain".toMediaTypeOrNull())

        apiService.uploadImage(imagePart, targetLetterPart).enqueue(object :
            Callback<ResponseModel> {
            override fun onResponse(
                call: Call<ResponseModel>,
                response: Response<ResponseModel>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()

                    if (result != null) {
                        if (result.result) {
                            Log.d("ServerResponse", "Letra correcta detectada.") // Éxito: La letra detectada se corresponde con la de la imagen
                        } else {
                            Log.d("ServerResponse", "Letra incorrecta detectada." )// Fallo:  La letra detectada NO se corresponde con la de la imagen
                        }
                    } else {
                        Log.d("ServerResponse", "Respuesta inesperada (null) del servidor") // El servidor respondió con un resultado inesperado (valor nulo)
                    }
                } else {
                    // Respuesta no exitosa (código de estado HTTP diferente de 200)
                    Log.e(
                        "ServerResponse",
                        "Error en la respuesta del servidor. Código: ${response.code()}"
                    )
                }
            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                // Manejar errores de red
                Log.e(
                    "ServerResponse",
                    "Error de red al enviar la imagen al servidor: ${t.message}"
                )
            }
        })
    }
}