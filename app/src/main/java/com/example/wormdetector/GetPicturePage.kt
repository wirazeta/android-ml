package com.example.wormdetector

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.wormdetector.domain.item.MLItem
//import com.example.wormdetector.ui.GetPictureViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.wormdetector.ui.LoadingViewModel
import java.util.Timer
import kotlin.concurrent.timerTask

@Composable
fun GetPicturePage(navController: NavController, fileName:String?){
    val getPictureViewModel: LoadingViewModel = hiltViewModel()
    val ml by getPictureViewModel.ml.collectAsState()

    LaunchedEffect(fileName.toString()){
        Timer().schedule(timerTask { getPictureViewModel.getML(fileName.toString()) }, 1000)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .wrapContentSize(Alignment.Center)
    ){
        TopAppBar(modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
        ){
            Text(text = "Worm App", modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally))
        }
        Spacer(modifier = Modifier.padding(20.dp))
        Text(modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .padding(10.dp),
            text = "Hasil Deteksi Gambar",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp)
        Log.d("ML Item","You get ml item")
        MLCard(ml = ml)
        Spacer(modifier = Modifier.padding(20.dp))
    }

}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MLCard(ml: MLItem){
    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .wrapContentSize(Alignment.Center)
    ){
        item{
            Card(
                elevation = 5.dp,
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier
                    .padding(top = 5.dp, bottom = 5.dp, start = 10.dp, end = 10.dp)
                    .fillMaxSize()
            ){
                Spacer(modifier = Modifier.padding(20.dp))
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
                ){
                    Log.d("getMLImage", ml.image)
                    GlideImage(
                        model = ml.image,
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(500.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.padding(16.dp))
            Card(elevation = 5.dp,
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier
                    .padding(top = 5.dp, bottom = 5.dp, start = 10.dp, end = 10.dp)
                    .fillMaxSize()
            ){
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
                ){
                    ml.data.map { data ->
                        Text(text = "Nama Cacing : "+data.name)
                        Spacer(modifier = Modifier.padding(15.dp))
                        Text(text = "Jumlah Cacing : "+data.count)
                        Spacer(modifier = Modifier.padding(15.dp))
                        var counter:Int = 1
                        data.`object`.map{ `object` ->
                            Text(text = "Cacing " + `object`.name +" "+counter++)
                            Text(text = "Kelas : "+`object`.`class`)
                            Text(text = "Berapa persen objek benar : " + "%.2f".format((`object`.confidence * 100)) + "%")
                            Spacer(modifier = Modifier.padding(15.dp))
                        }
                    }
                }
            }
        }
    }
}
