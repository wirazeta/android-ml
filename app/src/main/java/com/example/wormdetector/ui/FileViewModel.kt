package com.example.wormdetector.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wormdetector.repo.FileRepository
import kotlinx.coroutines.launch
import java.io.File

class FileViewModel(
    private val repository: FileRepository = FileRepository()
): ViewModel() {
    fun uploadImage(file: File){
        viewModelScope.launch{
            repository.uploadImage(file)
        }
    }
}