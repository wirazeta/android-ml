package com.example.wormdetector

import retrofit2.http.GET
import retrofit2.http.POST
interface AppAPI {

    @POST("/")
    suspend fun uploadPicture()
}