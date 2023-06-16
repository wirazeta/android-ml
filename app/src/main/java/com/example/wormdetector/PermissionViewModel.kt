package com.example.wormdetector

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class PermissionViewModel:ViewModel() {

    //[WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE]
    val visiblePermissionDialogQueue = mutableStateListOf<String>()

    fun dismissDialog(){
        visiblePermissionDialogQueue.removeLast()
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ){
        if(!isGranted){
            visiblePermissionDialogQueue.add(0,permission)
        }
    }
}