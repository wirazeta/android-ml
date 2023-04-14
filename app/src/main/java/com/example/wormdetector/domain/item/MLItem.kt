package com.example.wormdetector.domain.item

import com.example.wormdetector.data.remote.model.Data
import com.example.wormdetector.data.remote.model.MLData

data class MLItem(
    val `data`: List<Data>,
    val image: String,
    val message: String,
    val status: Int
)

fun MLData.toMLItem() = MLItem(data, image, message, status)

