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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
fun TakePicturePage(navController: NavController, alreadyOpenCamera:Boolean){
//    var alreadyOpenCamera:Boolean = alreadyOpenCamera
    var context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val fileViewModel:FileViewModel = hiltViewModel()
    var imageName by remember {mutableStateOf<String?>("")}
    val imageBitmap = remember { mutableStateOf<Bitmap?>(null) }
    var hasImage by remember{
        mutableStateOf(false)
    }
//    val requestPermissionLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission()
//    ){isGranted ->
//        if(isGranted){
//            Log.i("Camera Permission", "Permission Granted")
//        }else{
//            Log.i("Camera Permission", "Permission Granted")
//        }
//    }
    val getImageLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()){uri:Uri? ->
        if(uri != imageUri){
            imageUri = uri
            Log.d("PhotoPicker","Selected Uri: $imageUri")
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
    var cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult ={
            hasImage = it
        }
    )
    Column(modifier = Modifier
        .fillMaxSize()
        .wrapContentSize(Alignment.Center)
    ){
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
//        Button(onClick = {
//            navController.navigate(Screen.CameraScreen.route)
//        },
//            modifier = Modifier
//                .fillMaxWidth()
//                .wrapContentWidth(Alignment.CenterHorizontally)
//        ) {
//            Text(text="Open Camera")
//        }
        Spacer(modifier = Modifier.padding(20.dp))
        Button(onClick = {
            getImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
        ) {
            Text( text = "Get Image")
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

//@Composable
//fun CameraView(
//    outputDirectory: File,
//    executor: Executor,
//    onImageCapture: (Uri) -> Unit,
//    onError: (ImageCaptureException) -> Unit
//){
//    val lensFacing = CameraSelector.LENS_FACING_BACK
//    val context = LocalContext.current
//    val lifecycleOwner = LocalLifecycleOwner.current
//
//    val preview = Preview.Builder().build()
//    val previewView = remember{ PreviewView(context) }
//    val imageCapture: ImageCapture = remember{ ImageCapture.Builder().build() }
//    val cameraSelector = CameraSelector.Builder()
//        .requireLensFacing(lensFacing)
//        .build()
//    LaunchedEffect(lensFacing){
//        try{
//            val cameraProvider = context.getCameraProvider()
//            cameraProvider.unbindAll()
//            cameraProvider.bindToLifecycle(
//                lifecycleOwner,
//                cameraSelector,
//                preview,
//                imageCapture
//            )
//            preview.setSurfaceProvider(previewView.surfaceProvider)
//        }catch(e:Exception){
//            Log.e("Launch Camera Error","$e")
//        }
//    }
//    Box(modifier = Modifier
//        .fillMaxWidth()
//        .wrapContentWidth(Alignment.CenterHorizontally))
//    {
//        AndroidView(factory = {previewView}, modifier = Modifier.fillMaxWidth())
//        IconButton(onClick = {
//            Log.i("Open Camera", "Your Camera Is Open")
//            takePhoto(
//                filenameFormat = "yyyy-MM-dd-HH-mm-ss-SSS",
//                imageCapture = imageCapture,
//                outputDirectory = outputDirectory,
//                executor = executor,
//                onImageCapture = onImageCapture,
//                onError = onError
//            )
//
//        },
//            content = {
//                Icon(
//                    imageVector = Icons.Sharp.AddAPhoto,
//                    contentDescription = "Take Picture",
//                    tint = Color.White,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .size(100.dp)
//                        .padding(1.dp)
//                        .border(1.dp, Color.White, CircleShape)
//                )
//            }
//        )
//    }
//}

//private fun takePhoto(
//    filenameFormat: String,
//    imageCapture: ImageCapture,
//    outputDirectory: File,
//    executor: Executor,
//    onImageCapture: (Uri) -> Unit,
//    onError:(ImageCaptureException) -> Unit
//){
//    val photoFile = File(
//        outputDirectory,
//        SimpleDateFormat(filenameFormat, Locale.getDefault()).format(System.currentTimeMillis()) + ".jpg"
//    )
//    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
//
//    imageCapture.takePicture(outputOptions, executor, object : ImageCapture.OnImageSavedCallback{
//        override fun onError(exception: ImageCaptureException) {
//            Log.e("Take Picture Error", "Take Photo Error : ",exception)
//            onError(exception)
//        }
//
//        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
//            val savedUri = Uri.fromFile(photoFile)
//            onImageCapture(savedUri)
//        }
//    })
//}

//private fun handleImageCapture(uri: Uri){
//    Log.i("Image Capture", "Image Capture $uri")
//    shouldShowCamera.value = false
//}

private fun getOutputDirectory(context: Context):File{
    val mediaDir = context.getExternalFilesDirs("").firstOrNull()?.let{
        File(it.path.toString()).apply {
            mkdirs()
        }
    }
    return if(mediaDir != null && mediaDir.exists()) mediaDir else File("")
}

//private fun requestCameraPermission(requestPermissionLauncher: ActivityResultLauncher<String>, context: Context, activity: Activity){
//    when{
//        ContextCompat.checkSelfPermission(
//            context,
//            android.Manifest.permission.CAMERA
//        ) == PackageManager.PERMISSION_GRANTED -> {
//            Log.i("Camera Permission", "Permission previously granted")
//        }
//        ActivityCompat.shouldShowRequestPermissionRationale(
//            activity,
//            android.Manifest.permission.CAMERA
//        ) -> Log.i("Camera Permission","Show camera permission dialog")
//        else -> requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
//    }
//}

private fun checkCameraPermission(context:Context){
    if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
        Log.d("Camera Permission", "You Don't Have Camera Permission")
        ActivityCompat.requestPermissions(context as Activity, arrayOf(android.Manifest.permission.CAMERA) ,100)
    }
}

private fun readJSONFromFile(f:String): String? {
    var gson = Gson()
    val bufferedReader: BufferedReader = File(f).bufferedReader()
    val inputString = bufferedReader.use {
        it.readText()
    }
    var post = gson.fromJson(inputString, ImageClass::class.java)
    return post.imageUri
}



