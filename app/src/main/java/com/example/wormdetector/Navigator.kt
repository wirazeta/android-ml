package com.example.wormdetector

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.core.net.toUri
import androidx.navigation.NavDeepLink
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink


@Composable
fun Navigator(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.TakePicturePage.route){
        composable(
            route = Screen.TakePicturePage.route,
            arguments = listOf(
                navArgument("alreadyOpenCamera"){
                    type = NavType.BoolType
                    defaultValue = false
                    nullable = false
                }
            )
        ){
            it.arguments?.getBoolean("alreadyOpenCamera")
                ?.let { it1 -> TakePicturePage(navController = navController, alreadyOpenCamera = it1) }
        }
        composable(
            route = Screen.GetPicturePage.route + "/{fileName}",
            arguments = listOf(
                navArgument("fileName") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
            })
        ){entry ->
//            Log.d("Get Picture Route","${Screen.GetPicturePage.route}/{${entry.arguments?.getString("filename")}}")
            GetPicturePage(navController = navController,fileName = entry.arguments?.getString("fileName"))
        }
        composable(
            route = Screen.CameraScreen.route,
        ){
            CameraScreen(navController = navController, navigateToTakePicture = {alreadyOpenCamera->
                navController.navigate(Screen.TakePicturePage.createRoute(alreadyOpenCamera))
            })
        }
    }
}