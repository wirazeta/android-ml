package com.example.wormdetector

import android.media.Image
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.wormdetector.domain.item.MLItem
import com.example.wormdetector.ui.GetPictureViewModel

@Composable
fun GetPicturePage(navController: NavController){

    val getPictureViewModel = viewModel(modelClass = GetPictureViewModel::class.java)
    val ml by getPictureViewModel.ml.collectAsState()
    LazyColumn{
        items(ml){ ml:MLItem ->
            MLCard(ml = ml)
        }

    }

}

@Composable
fun MLCard(ml: MLItem){
    val image = rememberImagePainter(data = ml.image)
    Card(
        elevation = 5.dp,
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier
            .padding(top = 5.dp, bottom = 5.dp, start = 10.dp, end = 10.dp)
            .fillMaxSize()
    ){

        Column{
            Image(
                painter = image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
            Column(modifier = Modifier.fillMaxWidth()){
                ml.data.forEach {
                    Text(text = "Nama cacing : "+it.name)
                    Spacer(modifier = Modifier.padding(15.dp))
                    Text(text = "Jumlah cacing : "+it.count)
                    Spacer(modifier = Modifier.padding(15.dp))
                    val counter = 1
                    it.`object`.forEach {
                        Text(text = "Cacing " + counter + " : "+it.name)
                        Spacer(modifier = Modifier.padding(15.dp))
                        Text(text = "Kelas : "+it.`class`)
                        Spacer(modifier = Modifier.padding(15.dp))
                        Text(text = "Berapa persen objek benar : " + (it.confidence * 100))
                    }
                }
            }
        }
    }
}
