package com.example.wormdetector

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider.getUriForFile
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.wormdetector.ui.FileViewModel
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.File


//private lateinit var outputDirectory: File
//private lateinit var cameraExecutor: ExecutorService

var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)

@Composable
fun TakePicturePage(navController: NavController, imageNameCamera: String){
    var context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val fileViewModel:FileViewModel = hiltViewModel()
    var imageName by remember {mutableStateOf<String?>("")}
    val imageBitmap = remember { mutableStateOf<Bitmap?>(null) }
    if(imageNameCamera != ""){
        imageName = imageNameCamera
        imageUri = Uri.parse("file:///storage/emulated/0/Android/data/com.example.wormdetector/files/$imageName")
    }
    val getImageLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()){uri:Uri? ->
        if(uri != imageUri){
            imageUri = uri
        }else{
            Log.d("PhotoPicker","Image not found")
        }
    }
    imageUri?.let{
        if(Build.VERSION.SDK_INT > 28){
            val source = ImageDecoder.createSource(context.contentResolver, it)
            imageBitmap.value = ImageDecoder.decodeBitmap(source)
        }else{
            imageBitmap.value = MediaStore.Images.Media.getBitmap(context.contentResolver,it)
        }
    }
    LazyColumn{
        item{
            TopAppBar(modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
            ){
                Text(text = "Worm App", modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally))
            }
            Spacer(modifier = Modifier.padding(20.dp))
            Column(modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
            ){
                if(imageUri == null){
                    Text(modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .padding(10.dp),
                        text = "Ambil Gambar Terlebih Dahulu !",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }else{
                    Text(modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .padding(10.dp),
                        text = "Gambar Telah Diambil",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
                Spacer(modifier = Modifier.padding(20.dp))
                imageBitmap.value?.let{
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(400.dp)
                            .padding(20.dp)
                    )
                }
                Spacer(modifier = Modifier.padding(20.dp))
                shouldShowCamera.value = ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                checkCameraPermission(context)
                Button(onClick = {
                    navController.navigate(Screen.CameraScreen.route)
                },
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {
                    Text(text="Open Camera")
                }
                Spacer(modifier = Modifier.padding(10.dp))
                Button(onClick = {
                    getImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {
                    Text( text = "Open Gallery")
                }
                Spacer(modifier = Modifier.padding(10.dp))
                Button(onClick = {
                    var image:File? = imageUri?.let { File(getImagePath(it, context.contentResolver)) }
                    Log.d("filepath","${image?.path.toString()}")
                    image?.let{fileViewModel.uploadImage(it)}
                    imageName = image?.name.toString()
                    Log.d("Image name",imageName.toString())
                    imageName?.let{navController.navigate(Screen.GetPicturePage.withArgs(it))}
                },
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {
                    Text( text = "Upload Image")
                }
            }
        }
    }
}

private fun getImagePath(uri:Uri, contentResolver: ContentResolver): String{
    val path:String?
    val projection = Array(1){MediaStore.Files.FileColumns.DATA}
    val cursor = contentResolver.query(uri, projection, null, null, null)

    if(cursor == null){
        path = uri.path
    }else{
        cursor.moveToFirst()
        val columnIndex = cursor.getColumnIndex(projection[0])
        path = cursor.getString(columnIndex)
        cursor.close()
    }
    return if(path.isNullOrEmpty()) uri.path.toString() else path
}

private fun getImageUri(context: Context): Uri{
    val directory = File(context.cacheDir, "images")
    directory.mkdirs()
    val file = File.createTempFile(
        "selected_image_",
        ".jpg",
        directory,
    )
    val authority = context.packageName + ".file-provider"
    return getUriForFile(
        context,
        authority,
        file,
    )
}
private fun checkCameraPermission(context:Context){
    if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
        Log.d("Camera Permission", "You Don't Have Camera Permission")
        ActivityCompat.requestPermissions(context as Activity, arrayOf(android.Manifest.permission.CAMERA) ,100)
    }
}


