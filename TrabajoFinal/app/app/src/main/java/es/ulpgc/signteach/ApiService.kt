package es.ulpgc.signteach

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("/process-image")
    fun uploadImage(
        @Part image: MultipartBody.Part,
        @Part("target_letter") targetLetter: RequestBody
    ): Call<YourResponseModel>
}