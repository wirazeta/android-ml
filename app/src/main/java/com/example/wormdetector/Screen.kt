package com.example.wormdetector

sealed class Screen(val route: String){
//    object MainScreen : Screen("main_screen")
    object TakePicturePage : Screen("take_picture_page/{alreadyOpenCamera}"){
        fun createRoute(alreadyOpenCamera:Boolean) = "take_picture_page/$alreadyOpenCamera"
    }
    object GetPicturePage : Screen("get_picture_page")
    object CameraScreen : Screen("camera_screen")
    fun withArgs(vararg args : String) : String{
        return buildString {
            append(route)
            args.forEach { arg->
                append("/$arg")
            }
        }
    }
}
