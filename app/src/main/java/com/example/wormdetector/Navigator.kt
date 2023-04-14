package com.example.wormdetector

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


@Composable
fun Navigator(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.TakePicturePage.route){
        composable(route = Screen.TakePicturePage.route){
            TakePicturePage(navController = navController)
        }
        composable(route = Screen.LoadingPage.route){

        }
        composable(route = Screen.GetPicturePage.route){
            GetPicturePage(navController = navController)
        }
    }
}