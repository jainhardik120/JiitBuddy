package com.jainhardik120.jiitcompanion.ui.presentation.home

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity
import com.squareup.moshi.Moshi

private const val TAG = "HomeScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userInfo : UserEntity,
    token: String? = null,
    navController: NavHostController = rememberNavController()
) {
    var selectedItem by remember { mutableStateOf(0) }
    val json = Moshi.Builder().build().adapter(UserEntity::class.java).lenient().toJson(
        userInfo
    )
    Log.d(TAG, "HomeScreen: Composed")
    Scaffold(topBar = {
        TopAppBar(title= {
            Text(text = "JIIT Buddy")
        })
    },bottomBar = {
        val screens = listOf(
            BottomBarScreen.Home,
            BottomBarScreen.Attendance,
            BottomBarScreen.Performance,
            BottomBarScreen.ExamSchedule,
            BottomBarScreen.Subjects
        )
        NavigationBar {
            screens.forEachIndexed{index, screen->
                NavigationBarItem(
                    icon = { Icon(screen.icon, contentDescription = screen.title) },
                    label = { Text(screen.title) },
                    selected = selectedItem == index,
                    onClick = {
                        selectedItem = index
                        navController.navigate("${screen.route}/${json}/${token}"){
                            popUpTo(navController.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }) {
        HomeNavGraph(navController = navController, userEntity = userInfo, token = token, paddingValues = it)
    }
}


