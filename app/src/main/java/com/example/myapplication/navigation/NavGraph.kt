package com.example.myapplication


import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable


/**
 * @author Coding Meet
 * Created 17-01-2024 at 02:22 pm
 */

@Composable
fun SetUpNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues
) {
    NavHost(navController = navController,
        startDestination = Screens.LevelGroup.route){
        composable(Screens.Home.route){
            HomeScreen(innerPadding = innerPadding)
        }
        composable(Screens.Notification.route){
            NotificationScreen(innerPadding = innerPadding)
        }
        composable(Screens.Profile.route){
            ProfileScreen(innerPadding = innerPadding)
        }

        composable(Screens.Setting.route){
            SettingScreen(innerPadding = innerPadding)
        }
        composable(Screens.HomeAdmins.route){
            HomeAdmins(innerPadding = innerPadding)
        }
        composable(Screens.Room.route){
            RoomScreen(innerPadding = innerPadding)
        }
        composable(Screens.Teacher.route){
            TeacherScreen(innerPadding = innerPadding)
        }
        composable(Screens.LevelGroup.route){
            LevelGroup(innerPadding = innerPadding)
        }
    }
}