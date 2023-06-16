package com.example.wormdetector.domain

import com.example.wormdetector.domain.item.MLItem
import com.example.wormdetector.repo.MLRepository
import okhttp3.RequestBody
import javax.inject.Inject

class GetMLUseCase @Inject constructor(private val mlRepository: MLRepository){

    suspend operator fun invoke(filename:String):MLItem?{
        var mlItem:MLItem? = mlRepository.getML(filename)
        return mlItem?.let { mlRepository.getML(filename)?.copy(data = it.data, image = it.image, message = it.message, status = it.status) }
    }

}