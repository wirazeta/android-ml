package com.example.wormdetector.ui

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wormdetector.domain.GetMLUseCase
import com.example.wormdetector.domain.item.MLItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
@HiltViewModel
class LoadingViewModel @Inject constructor(private val getMLUseCase: GetMLUseCase): ViewModel(){

    private val _ml = MutableStateFlow(MLItem(emptyList(),"","",0))
    val ml: StateFlow<MLItem> get() = _ml

//    init {
//        getML()
//    }

    fun getML(filename:String){
        viewModelScope.launch {
            try{
                val ml = getMLUseCase(filename)
                if (ml != null) {
                    _ml.value = ml
                }
            }catch(e:Exception){
                Log.e("getML Error","$e")
            }
        }
    }

}