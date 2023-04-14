package com.example.wormdetector.data.remote

import com.example.wormdetector.data.remote.model.MLData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MLService @Inject constructor(private val mlApi: MLApi) {

    suspend fun getML() : List<MLData> {
        return withContext(Dispatchers.IO){
            val ml = mlApi.getML()
            ml.body() ?: emptyList()
        }
    }

}