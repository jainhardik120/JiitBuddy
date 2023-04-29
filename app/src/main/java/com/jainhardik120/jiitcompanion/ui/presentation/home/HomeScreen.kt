package com.jainhardik120.jiitcompanion.ui.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity
import com.jainhardik120.jiitcompanion.util.UiEvent
import com.squareup.moshi.Moshi

private const val TAG = "HomeScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    userInfo: UserEntity,
    token: String? = null,
    onNavigateUp: (UiEvent.Navigate) -> Unit,
    navController: NavHostController = rememberNavController()
) {
    val json = Moshi.Builder().build().adapter(UserEntity::class.java).lenient().toJson(
        userInfo
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    LaunchedEffect(key1 = true, block = {
        viewModel.uiEvent.collect {
            when (it) {
                is UiEvent.Navigate -> {
                    onNavigateUp(it)
                }

                else -> {

                }
            }
        }
    })
    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            Text(text = "JIIT Buddy")
        }, actions = {
            IconButton(onClick = {
                viewModel.onEvent(HomeScreenEvent.onLogOutClicked)
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
            screens.forEachIndexed { _, screen ->
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

    if (viewModel.state.logOutDialogOpened) {
        AlertDialog(
            icon={
                 Icon(Icons.Filled.Logout, contentDescription = "Logout Icon")
            },
            onDismissRequest = {
                viewModel.onEvent(HomeScreenEvent.onLogOutDismissed)
            },
            text = {
                Text(text = "Are you sure you want to log out?")
            },
            confirmButton = {
                TextButton(onClick = { viewModel.onEvent(HomeScreenEvent.onLogOutConfirmed) }) {
                    Text(text = "Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onEvent(HomeScreenEvent.onLogOutDismissed) }) {
                    Text(text = "Cancel")
                }
            })
    }
}
