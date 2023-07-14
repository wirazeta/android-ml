package com.example.wormdetector.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wormdetector.domain.GetMLUseCase
import com.example.wormdetector.repo.FileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FileViewModel@Inject constructor(private val fileRepository: FileRepository): ViewModel() {
    fun uploadImage(file: File){
        viewModelScope.launch{
            var uploadImage:Boolean?
            val requestDataFlow = flow{
                uploadImage = fileRepository.uploadImage(file)
                emit(uploadImage)
            }
            requestDataFlow.distinctUntilChanged().collectLatest {
                uploadImage = it
            }
//            fileRepository.uploadImage(file)
        }
    }
}