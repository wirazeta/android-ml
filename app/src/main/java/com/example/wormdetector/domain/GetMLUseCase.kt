package com.example.wormdetector.domain

import com.example.wormdetector.domain.item.MLItem
import com.example.wormdetector.repo.MLRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import okhttp3.RequestBody
import javax.inject.Inject

class GetMLUseCase @Inject constructor(private val mlRepository: MLRepository){

    suspend operator fun invoke(filename:String):MLItem?{
        var mlItem:MLItem?
        val requestDataFlow = flow{
            mlItem = mlRepository.getML(filename)
            emit(mlItem)
        }
        var mlReturn:MLItem? = null
        requestDataFlow
            .distinctUntilChanged()
            .collectLatest {
                mlReturn = it.let { it?.let { it1 -> mlRepository.getML(filename)?.copy(data = it1.data, image = it1.image, message = it1.message, status = it1.status) } }
            }

//        return mlItem?.let { mlRepository.getML(filename)?.copy(data = it.data, image = it.image, message = it.message, status = it.status) }
        return mlReturn
    }

}