package com.example.wormdetector.data.remote.model

data class MLData(
    val `data`: List<Data>,
    val image: String,
    val message: String,
    val status: Int
)