package com.jainhardik120.jiitcompanion.ui.presentation.home

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
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
    userInfo: UserEntity,
    token: String? = null,
    navController: NavHostController = rememberNavController()
) {
    val json = Moshi.Builder().build().adapter(UserEntity::class.java).lenient().toJson(
        userInfo
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            Text(text = "JIIT Buddy")
        }, actions = {
            IconButton(onClick = {
                Log.d(TAG, "HomeScreen: Clicked Exit")
            }) {
                Icon(Icons.Filled.Logout, contentDescription = "Logout Icon")
            }
        })
    }, bottomBar = {
        val screens = listOf(
            BottomBarScreen.Home,
            BottomBarScreen.Attendance,
            BottomBarScreen.Performance,
            BottomBarScreen.ExamSchedule,
            BottomBarScreen.Subjects
        )
        NavigationBar {
            screens.forEachIndexed { index, screen ->
                NavigationBarItem(
                    icon = { Icon(screen.icon, contentDescription = screen.title) },
                    label = {
                        Text(text = screen.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    },
                    selected = currentDestination?.hierarchy?.any {
                        it.route?.contains(screen.route) ?: false
                    } == true,
                    onClick = {
                        navController.navigate("${screen.route}/${json}/${token}") {
                            popUpTo(navController.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }) {
        HomeNavGraph(
            navController = navController,
            userEntity = userInfo,
            token = token,
            paddingValues = it
        )
    }
}
