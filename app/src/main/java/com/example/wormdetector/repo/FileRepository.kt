package com.example.wormdetector.repo

import android.util.Log
import com.example.wormdetector.data.remote.MLApi
import com.example.wormdetector.util.Constaint
import com.example.wormdetector.util.Constaint.Companion.BASE_URL
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import retrofit2.Retrofit
import java.io.File
import java.io.IOException
import javax.inject.Inject

class FileRepository @Inject constructor(private val MLApi:MLApi) {
    suspend fun uploadImage(file: File): Boolean{
        return try{
            MLApi.postML(
                image = MultipartBody.Part
                    .createFormData(
                        name = "image",
                        file.name,
                        file.asRequestBody()
                    )
            )
            Log.d("Send Image", "Image already send")
            true
        }catch(e: IOException){
            e.printStackTrace()
            false
        }catch(e: HttpException){
            e.printStackTrace()
            false
        }
    }
}