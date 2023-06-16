package com.example.wormdetector.repo

import android.util.Log
import com.example.wormdetector.data.remote.MLService
import com.example.wormdetector.domain.item.MLItem
import com.example.wormdetector.domain.item.toMLItem
import dagger.hilt.android.scopes.ActivityScoped
import okhttp3.RequestBody
import javax.inject.Inject

@ActivityScoped
class MLRepository @Inject constructor(private val mlService: MLService){

    suspend fun getML(filename: String): MLItem?{
        return mlService.getML(filename)?.toMLItem()
    }

}