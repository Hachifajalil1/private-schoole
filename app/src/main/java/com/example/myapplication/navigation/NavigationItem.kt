package com.example.myapplication


import androidx.compose.ui.graphics.vector.ImageVector

/**
 * @author Coding Meet
 * Created 17-01-2024 at 01:53 pm
 */

data class NavigationItem(
    val title : String,
    val route : String,
    val selectedIcon : ImageVector,
    val unSelectedIcon : ImageVector,
    val badgeCount : Int? = null
)