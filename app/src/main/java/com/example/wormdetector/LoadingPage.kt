package com.example.wormdetector

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.wormdetector.domain.item.MLItem
import com.example.wormdetector.ui.LoadingViewModel
import java.util.Timer
import kotlin.concurrent.timerTask

@Composable
fun LoadingPage(navigateToGetPicturePage: (ml:MLItem) -> Unit?, fileName:String?){
    Log.d("fileName", fileName.toString())
    val getPictureViewModel: LoadingViewModel = hiltViewModel()
    val ml by getPictureViewModel.ml.collectAsState()
    Timer().schedule(timerTask {getPictureViewModel.getML(fileName.toString())},1000)
    navigateToGetPicturePage(ml)
}