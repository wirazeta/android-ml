package com.example.wormdetector.data.remote

import com.example.wormdetector.data.remote.model.MLData
import com.example.wormdetector.util.Constaint.Companion.BASE_URL
import retrofit2.Response
import com.example.wormdetector.util.Constaint.Companion.DOWNLOAD_ENDPOINT
import com.example.wormdetector.util.Constaint.Companion.UPLOAD_ENDPOINT
import okhttp3.MultipartBody
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.http.*

interface MLApi {
    @GET(DOWNLOAD_ENDPOINT)
    suspend fun getML(): Response<List<MLData>>

    @Multipart
    @POST(UPLOAD_ENDPOINT)
    suspend fun postML(
        @Part image: MultipartBody.Part
    )

    companion object {
        val instance by lazy{
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .build()
                .create(MLApi::class.java)
        }
    }
}