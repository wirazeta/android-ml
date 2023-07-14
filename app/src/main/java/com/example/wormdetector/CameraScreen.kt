package com.example.wormdetector

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.AddAPhoto
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.data.ContextCache
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.wormdetector.util.getCameraProvider
import okio.Path.Companion.toPath
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private var imageNameCamera:String? = ""
private var statusWriteFile:Boolean = false
@Composable
fun CameraScreen(navController:NavController, navigateToTakePicture: (String?) -> Unit){
    val context = LocalContext.current
    val cameraExecutor:ExecutorService = Executors.newSingleThreadExecutor()
    val outputDirectory: File = getOutputDirectory(context)
    CameraView(
        outputDirectory = outputDirectory,
        executor = cameraExecutor,
        onImageCapture = ::handleImageCapture,
        onError = { Log.e("View Error", "View Error : ",it) },
        navController = navController,
        navigateToTakePicture = navigateToTakePicture,
        context = context
    )
}

@Composable
fun CameraView(
    outputDirectory: File,
    executor: Executor,
    onImageCapture: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit,
    navController: NavController,
    navigateToTakePicture: (String?) -> Unit,
    context:Context
){
    val lensFacing = CameraSelector.LENS_FACING_BACK
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val preview = Preview.Builder().build()
    val previewView = remember{ PreviewView(context) }
    val imageCapture: ImageCapture = remember{ ImageCapture.Builder().build() }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()
    LaunchedEffect(lensFacing){
        try{
            val cameraProvider = context.getCameraProvider()
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
            preview.setSurfaceProvider(previewView.surfaceProvider)
        }catch(e:Exception){
            Log.e("Launch Camera Error","$e")
        }
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .wrapContentSize(Alignment.Center))
    {
        AndroidView(factory = {previewView}, modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally))
        IconButton(onClick = {
            Log.i("Open Camera", "Your Camera Is Open")
            takePhoto(
                filenameFormat = "yyyy-MM-dd-HH-mm-ss-SSS",
                imageCapture = imageCapture,
                outputDirectory = outputDirectory,
                executor = executor,
                onImageCapture = onImageCapture,
                onError = onError,
                context = context
            )
            Log.d("Image Photo", outputDirectory.path.toString())
        },
            content = {
                Icon(
                    imageVector = Icons.Sharp.AddAPhoto,
                    contentDescription = "Take Picture",
                    tint = Color.White,
                    modifier = Modifier
                        .fillMaxHeight()
                        .wrapContentHeight(Alignment.CenterVertically)
                        .size(30.dp)
                )
            }
        )
        Button(onClick = {
            Log.d("imageNameCameraTaken","$imageNameCamera")
            navigateToTakePicture(imageNameCamera)
        }) {
            Text(text = "Done")
        }
    }
}

private fun takePhoto(
    filenameFormat: String,
    imageCapture: ImageCapture,
    outputDirectory: File,
    executor: Executor,
    onImageCapture: (Uri) -> Unit,
    onError:(ImageCaptureException) -> Unit,
    context:Context
){
    val photoFile = File(
        outputDirectory,
        SimpleDateFormat(filenameFormat, Locale.getDefault()).format(System.currentTimeMillis()) + ".jpg"
    )
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(outputOptions, executor, object : ImageCapture.OnImageSavedCallback{
        override fun onError(exception: ImageCaptureException) {
            Log.e("Take Picture Error", "Take Photo Error : ",exception)
            onError(exception)
        }
        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            val savedUri = Uri.fromFile(photoFile)
            imageNameCamera = getFileName(savedUri, context)
            Log.d("imageNameCamera","$imageNameCamera")
            Image(savedUri)
            onImageCapture(savedUri)
        }
    })
}

private fun handleImageCapture(uri: Uri){
    Log.i("Image Capture", "Image Capture $uri")
    shouldShowCamera.value = false
}

private fun getOutputDirectory(context: Context):File{
    val mediaDir = context.getExternalFilesDirs("").firstOrNull()?.let{
        File(it.path.toString()).apply {
            mkdirs()
        }
    }
    return if(mediaDir != null && mediaDir.exists()) mediaDir else File("")
}

@SuppressLint("Range")
private fun getFileName(uri:Uri, context: Context):String? {
    var res: String? = null
    Log.d("imageName","${uri.scheme?.equals("file")}")

    if(uri.scheme?.equals("file") == true){
        var cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        try{
            if(cursor != null && cursor.moveToFirst()){
                res = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                Log.d("imageName","$res")
            }
        }finally{
            cursor?.close()
        }
        if(res == null){
            res = uri.path.toString()
            var cutt: Int = res.lastIndexOf('/')
            if(cutt != -1){
                res = res.substring(cutt+1)
            }
        }
    }
    return res
}

private fun isWritted(alreadyWriteFile:Boolean){
    statusWriteFile = alreadyWriteFile
}