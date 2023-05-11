package com.jainhardik120.jiitcompanion.ui.presentation.home

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jainhardik120.jiitcompanion.data.remote.model.MarksRegistration
import com.jainhardik120.jiitcompanion.ui.components.icons.FileDownload
import com.jainhardik120.jiitcompanion.ui.components.icons.Logout
import com.jainhardik120.jiitcompanion.ui.presentation.root.Screen
import com.jainhardik120.jiitcompanion.util.UiEvent
import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateUp: (UiEvent.Navigate) -> Unit,
    navController: NavHostController = rememberNavController(),
    userInfo: String,
    token: String,
    onReview: () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
        })
    val context = LocalContext.current
    LaunchedEffect(key1 = true, block = {
        viewModel.uiEvent.collect {
            when (it) {
                is UiEvent.Navigate -> {
                    if (it.route.contains(Screen.LoginScreen.route)) {
                        onNavigateUp(it)
                    } else {
                        navController.navigate(it.route) {
                            popUpTo(navController.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                    }
                }

                is UiEvent.OpenPdf -> {
                    try {
                        val pdfOpenIntent = Intent(Intent.ACTION_VIEW)
                        pdfOpenIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        pdfOpenIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        pdfOpenIntent.setDataAndType(
                            FileProvider.getUriForFile(
                                context,
                                context.applicationContext.packageName + ".provider",
                                File(it.path)
                            ), "application/pdf"
                        )
                        launcher.launch(pdfOpenIntent)
                    } catch (e: Exception) {
                        Log.d("myApp", "onCreateView: $e")
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
            if (token == "offline") {
                IconButton(onClick = {
                    viewModel.onEvent(HomeScreenEvent.OnOfflineAlertClicked)
                }) {
                    Icon(Icons.Filled.Warning, contentDescription = "Warning Icon")
                }
            } else {
                if (currentDestination?.route?.contains(BottomBarScreen.Performance.route) == true) {
                    IconButton(
                        onClick = {
                            viewModel.onEvent(HomeScreenEvent.ButtonViewMarksClicked)
                        }
                    ) {
                        Icon(
                            Icons.Filled.FileDownload,
                            contentDescription = "Download Marks Button"
                        )
                    }
                }
            }
            IconButton(onClick = {
                viewModel.onEvent(HomeScreenEvent.OnLogOutClicked)
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
                        viewModel.onEvent(HomeScreenEvent.BottomNavItemClicked(screen))
                    }
                )
            }
        }
    }) {
        HomeNavGraph(
            navController = navController,
            paddingValues = it,
            userInfo, token,
            onReview = { onReview() }
        )
    }

    if (viewModel.state.logOutDialogOpened) {
        AlertDialog(
            icon = {
                Icon(Icons.Filled.Logout, contentDescription = "Logout Icon")
            },
            onDismissRequest = {
                viewModel.onEvent(HomeScreenEvent.OnLogOutDismissed)
            },
            text = {
                Text(text = "Are you sure you want to log out?")
            },
            confirmButton = {
                TextButton(onClick = { viewModel.onEvent(HomeScreenEvent.OnLogOutConfirmed) }) {
                    Text(text = "Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onEvent(HomeScreenEvent.OnLogOutDismissed) }) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    if (viewModel.state.isMarksDialogOpened && viewModel.state.isMarksRegistrationsLoaded) {
        SubjectMarksDialog(registrations = viewModel.state.marksRegistrations, onClick = {
            viewModel.onEvent(HomeScreenEvent.MarksClicked(it))
        }, onDismiss = {
            viewModel.onEvent(HomeScreenEvent.MarksDialogDismissed)
        })
    }

    if (viewModel.state.offlineDialogOpened) {
        AlertDialog(
            icon = {
                Icon(Icons.Filled.Warning, contentDescription = "Warning Icon")
            },
            onDismissRequest = {
                viewModel.onEvent(HomeScreenEvent.OfflineDialogDismissed)
            },
            text = {
                Text(text = "The app is working in offline mode because servers were not reachable at time of login. Click below button to retry connection.")
            },
            confirmButton = {
                TextButton(onClick = { viewModel.onEvent(HomeScreenEvent.OfflineDialogConfirmed) }) {
                    Text(text = "Retry")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onEvent(HomeScreenEvent.OfflineDialogDismissed) }) {
                    Text(text = "Cancel")
                }
            })
    }
}

@Composable
fun SubjectMarksDialog(
    registrations: List<MarksRegistration>,
    onClick: (registration: MarksRegistration) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        title = {
            Text(
                text = "Subject Marks",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth(), content = {
                itemsIndexed(registrations) { _, registration ->
                    DropdownMenuItem(text = {
                        Text(
                            text = registration.registrationcode,
                            modifier = Modifier
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }, onClick = {
                        Toast.makeText(context, "Downloading...", Toast.LENGTH_LONG).show()
                        onClick(registration)
                    })
                }
            })
        },
        confirmButton = {

        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(text = "Cancel")
            }
        }
    )
}