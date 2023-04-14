package com.example.wormdetector.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wormdetector.domain.GetMLUseCase
import com.example.wormdetector.domain.item.MLItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetPictureViewModel @Inject constructor(private val getMLUseCase: GetMLUseCase): ViewModel(){

    private val _ml = MutableStateFlow(emptyList<MLItem>())
    val ml: StateFlow<List<MLItem>> get() = _ml

    init {
        getML()
    }

    private fun getML(){
        viewModelScope.launch {
            try{
                val ml = getMLUseCase()
                _ml.value = ml
            }catch(_:Exception){

            }
        }
    }

}