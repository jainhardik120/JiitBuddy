package com.jainhardik120.jiitcompanion.ui.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jainhardik120.jiitcompanion.ui.presentation.root.Screen
import com.jainhardik120.jiitcompanion.util.UiEvent


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateUp: (UiEvent.Navigate) -> Unit,
    navController: NavHostController = rememberNavController(),
    userInfo :String,
    token:String,
    onReview: ()-> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    LaunchedEffect(key1 = true, block = {
        viewModel.uiEvent.collect {
            when (it) {
                is UiEvent.Navigate -> {
                    if(it.route.contains(Screen.LoginScreen.route)){
                        onNavigateUp(it)
                    }else{
                        navController.navigate(it.route) {
                            popUpTo(navController.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                    }
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
            if(token == "offline"){
                IconButton(onClick = {
                    viewModel.onEvent(HomeScreenEvent.onOfflineAlertClicked)
                }) {
                    Icon(Icons.Outlined.Warning, contentDescription = "Warning Icon")
                }
            }
            IconButton(onClick = {
                viewModel.onEvent(HomeScreenEvent.onLogOutClicked)
            }) {
                Icon(Icons.Outlined.Logout, contentDescription = "Logout Icon")
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
                        viewModel.onEvent(HomeScreenEvent.bottomNavItemClicked(screen))
                    }
                )
            }
        }
    }) {
        HomeNavGraph(
            navController = navController,
            paddingValues = it,
            userInfo, token,
            onReview = {onReview()}
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

    if (viewModel.state.offlineDialogOpened) {
        AlertDialog(
            icon={
                Icon(Icons.Filled.Warning, contentDescription = "Warning Icon")
            },
            onDismissRequest = {
                viewModel.onEvent(HomeScreenEvent.offlineDialogDismisse)
            },
            text = {
                Text(text = "The app is working in offline mode because servers were not reachable at time of login. Click below button to retry connection.")
            },
            confirmButton = {
                TextButton(onClick = { viewModel.onEvent(HomeScreenEvent.offlineDialogConfirmed) }) {
                    Text(text = "Retry")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onEvent(HomeScreenEvent.offlineDialogDismisse) }) {
                    Text(text = "Cancel")
                }
            })
    }
}
