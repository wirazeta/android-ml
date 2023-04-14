package com.example.wormdetector

import android.content.Intent
import android.content.Intent.ACTION_PICK
import android.graphics.Bitmap
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.net.toFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.wormdetector.ui.FileViewModel
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.InputStream
import kotlin.contracts.contract

//@Composable
//fun TakePicturePage(navController: NavController){
//    var selectedImagedUri by remember{
//        mutableStateOf<Uri?>(null)
//    }
//
//    val viewModel = viewModel<FileViewModel>()
//
//    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.PickVisualMedia(),
//        onResult = {uri -> selectedImagedUri = uri}
//    )
//    LazyColumn(
//        modifier = Modifier
//            .fillMaxSize()
//    ) {
//        item {
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(start = 100.dp, top = 100.dp, bottom = 100.dp, end = 100.dp),
//                verticalArrangement = Arrangement.SpaceAround
//            ) {
//                Button(
//                    onClick = {
//                        singlePhotoPickerLauncher.launch(
//                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
//                        )
//                    },
//                    modifier = Modifier
//                        .wrapContentWidth(Alignment.CenterHorizontally)
//                ) {
//                    Text(text = "Take a Picture")
//                }
//                Spacer(
//                    modifier = Modifier
//                        .padding(10.dp)
//                )
//                AsyncImage(
//                    model = selectedImagedUri,
//                    contentDescription = null,
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                    contentScale = ContentScale.Crop
//                )
//                Button(onClick = {
////                    val file: File = selectedImagedUri!!.toFile()
//////                    val file = File(uri, "myImage.jpg")
//////                    file.createNewFile()
////                    viewModel.uploadImage(file)
//                }) {
//                    Text(text = "Upload Image")
//                }
//            }
//        }
//    }
//}


@Composable
fun TakePicturePage(navController: NavController){
    var selectedImage by remember{
        mutableStateOf<Uri?>(null)
    }
    var filePath by remember{
        mutableStateOf("")
    }
    val singlePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {uri -> selectedImage = uri}
    )
    val viewModel = viewModel<FileViewModel>()
    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .wrapContentWidth(Alignment.CenterHorizontally)
    )
    {
        item{
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 20.dp, 20.dp, 20.dp)
            ){
                AsyncImage(model = selectedImage, contentDescription = null)
            }
            Spacer(modifier = Modifier.padding(10.dp))
            Button(onClick = {
                singlePhotoLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
                filePath = selectedImage?.path.toString()
            }){
                Text(text = "Take Picture")
            }
            Spacer(modifier = Modifier.padding(10.dp))
            Button(onClick = {
                viewModel.uploadImage(File(filePath))
            }) {
                Text("Upload Image")
            }
        }
    }
}
