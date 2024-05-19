package com.example.myapplication


/**
 * @author Coding Meet
 * Created 17-01-2024 at 02:20 pm
 */

sealed class Screens(var route: String) {

    object  Home : Screens("home")
    object  Profile : Screens("profile")
    object  Notification : Screens("notification")
    object  Setting : Screens("setting")
    object  HomeAdmins : Screens("HomeAdmins")
    object  Room : Screens("room")
    object Teacher : Screens("teacher")
    object LevelGroup : Screens("LevelGroup")
    object Students : Screens("Students")
    object Parents : Screens("Parents")

}