package com.example.wormdetector.repo

import com.example.wormdetector.data.remote.MLService
import com.example.wormdetector.domain.item.MLItem
import com.example.wormdetector.domain.item.toMLItem
import javax.inject.Inject


class MLRepository @Inject constructor(private val mlService: MLService){

    suspend fun getML(): List<MLItem>{
        return mlService.getML().map {
            it.toMLItem()
        }
    }

}