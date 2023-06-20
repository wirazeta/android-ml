package com.example.wormdetector

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument


@Composable
fun Navigator(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.TakePicturePage.route){
        composable(
            route = Screen.TakePicturePage.route,
            arguments = listOf(
                navArgument("imageNameCamera"){
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                },
            )
        ){
            it.arguments?.getString("imageNameCamera")
                ?.let { it1 ->
                    Log.d("imageNameNavigator","$it1")
                    TakePicturePage(navController = navController, imageNameCamera = it1)
                }
        }
        composable(
            route = Screen.GetPicturePage.route + "/{fileName}",
            arguments = listOf(
                navArgument("fileName") {
                    type = NavType.StringType
                    defaultValue = null
                    nullable = true
            })
        ){entry ->
//            Log.d("Get Picture Route","${Screen.GetPicturePage.route}/{${entry.arguments?.getString("filename")}}")
            GetPicturePage(navController = navController,fileName = entry.arguments?.getString("fileName"))
        }
        composable(
            route = Screen.CameraScreen.route,
        ){
            CameraScreen(navController = navController, navigateToTakePicture = { imageNameCamera ->
                navController.navigate(Screen.TakePicturePage.createRoute(imageNameCamera))
            })
        }
    }
}