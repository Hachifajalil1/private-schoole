
package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth


import kotlinx.coroutines.launch

class TeacherMainActivity: ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyApplicationTheme {
                val items = listOf(
                    NavigationItem(
                        title = "Home",
                        route = Screens.Home.route,
                        selectedIcon = Icons.Filled.Home,
                        unSelectedIcon = Icons.Outlined.Home,
                    ),


                    NavigationItem(
                        title = "Timetable",
                        route = Screens.TimetableTeachers.route,
                        selectedIcon = Icons.Filled.Settings,
                        unSelectedIcon = Icons.Outlined.Settings,
                    ),
                    NavigationItem(
                        title = "Students Attendance",
                        route = Screens.StudentsAttendanceTeachers.route,
                        selectedIcon = Icons.Filled.Settings,
                        unSelectedIcon = Icons.Outlined.Settings,
                    ),
                    NavigationItem(
                        title = "Grades",
                        route = Screens.TeacherGradeEntryScreen.route,
                        selectedIcon = Icons.Filled.Settings,
                        unSelectedIcon = Icons.Outlined.Settings,
                    ),


                    NavigationItem(
                        title = "Share",
                        route = "share",
                        selectedIcon = Icons.Filled.Share,
                        unSelectedIcon = Icons.Outlined.Share,
                    ),
                )
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                val navController = rememberNavController()
                val context = LocalContext.current
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val topBarTitle =
                    if (currentRoute != null) {
                        items[items.indexOfFirst {
                            it.route == currentRoute
                        }].title
                    } else {
                        items[0].title
                    }
                ModalNavigationDrawer(
                    gesturesEnabled = drawerState.isOpen, drawerContent = {
                        ModalDrawerSheet() {
                            NavBarHeader()
                            Spacer(modifier = Modifier.height(8.dp))
                            NavBarBody(items = items, currentRoute = currentRoute) { currentNavigationItem ->
                                if (currentNavigationItem.route == "share") {
                                    Toast.makeText(context, "Share Clicked", Toast.LENGTH_LONG).show()
                                } else {
                                    navController.navigate(currentNavigationItem.route) {
                                        navController.graph.startDestinationRoute?.let { startDestinationRoute ->
                                            popUpTo(startDestinationRoute) {
                                                saveState = true
                                            }
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                                scope.launch {
                                    drawerState.close()
                                }
                            }
                        }
                    }, drawerState = drawerState
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(title = {
                                Text(text = topBarTitle)
                            }, navigationIcon = {
                                IconButton(onClick = {
                                    scope.launch {
                                        drawerState.open()
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "menu"
                                    )
                                }
                            },
                                actions = {
                                    IconButton(
                                        onClick = {
                                            // Handle log out action
                                            logout(context)
                                            val intent = Intent(context, LoginActivity::class.java)
                                            context.startActivity(intent)
                                        },
                                        modifier = Modifier.padding(end = 0.dp)
                                    ) {
                                        Icon(Icons.Default.ExitToApp, contentDescription = "Log Out")
                                    }
                                })
                        }
                    ) { innerPadding ->
                        SetUpNavGraph(navController = navController, innerPadding = innerPadding)
                    }
                }
            }
        }
    }

    private fun logout(context: Context) {
        FirebaseAuth.getInstance().signOut()
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("isLoggedIn", false)
            apply()
        }
        Toast.makeText(context, "Logged Out", Toast.LENGTH_SHORT).show()
    }
}
