package com.example.wormdetector.domain

import com.example.wormdetector.domain.item.MLItem
import com.example.wormdetector.repo.MLRepository
import javax.inject.Inject

class GetMLUseCase @Inject constructor(private val mlRepository: MLRepository){

    suspend operator fun invoke():List<MLItem>{
        return mlRepository.getML().shuffled()
    }

}