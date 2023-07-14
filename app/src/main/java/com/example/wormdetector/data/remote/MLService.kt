package com.example.wormdetector.data.remote

import android.util.Log
import com.example.wormdetector.data.remote.model.MLData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MLService @Inject constructor(private val mlApi: MLApi) {

    suspend fun getML(filename:String) : MLData? {
        var ml:MLData? = null
        Log.d("getML", mlApi.getML(filename).raw().toString())
        if(mlApi.getML(filename).isSuccessful){
            ml = mlApi.getML(filename).body()
        }else{
            Log.d("getML","Hello ERRORS !")
            Log.e("getML", mlApi.getML(filename).errorBody().toString())
        }

        return withContext(Dispatchers.IO){
            ml
        }
    }

}