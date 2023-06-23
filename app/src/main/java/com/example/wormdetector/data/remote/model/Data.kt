package com.example.wormdetector.data.remote.model

import android.os.Parcelable

data class Data(
    val count: Int,
    val name: String,
    val `object`: List<Object>
)