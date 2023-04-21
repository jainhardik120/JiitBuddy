package com.jainhardik120.jiitcompanion.ui.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity
import com.jainhardik120.jiitcompanion.util.BottomBarScreen
import com.jainhardik120.jiitcompanion.util.HomeNavGraph

private const val TAG = "HomeScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userInfo : UserEntity,
    viewModel: HomeViewModel = hiltViewModel(),
    token: String? = null,
    navController: NavHostController = rememberNavController()
) {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("Songs", "Artists", "Playlists")
    Scaffold(bottomBar = {
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
                        navController.navigate(screen.route){
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

@Composable
fun ProfileCard(userEntity: UserEntity){
    ElevatedCard(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
            Text(text = userEntity.name)
            Text(text = userEntity.enrollmentno)
            Text(text = userEntity.instituteLabel)
            Text(text = "${userEntity.programcode} ${userEntity.branch} ${userEntity.admissionyear} ${userEntity.batch}")
            Text(text = "${userEntity.userDOB} ${userEntity.gender}")
            Text(text = userEntity.studentcellno)
            Text(text = userEntity.studentpersonalemailid)
//            Spacer(modifier = Modifier.height(20.dp))
//            Button(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth()) {
//                Text(text = "View Attendance")
//            }
//            Spacer(modifier = Modifier.height(4.dp))
//            Button(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth()) {
//                Text(text = "View Registered Subject")
//            }
//            Spacer(modifier = Modifier.height(4.dp))
//            Button(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth()) {
//                Text(text = "View Result")
//            }
//            Spacer(modifier = Modifier.height(4.dp))
//            Button(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth()) {
//                Text(text = "Grade Card")
//            }
//            Spacer(modifier = Modifier.height(4.dp))
//            Button(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth()) {
//                Text(text = "Subject Marks")
//            }
//            Spacer(modifier = Modifier.height(4.dp))
//            Button(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth()) {
//                Text(text = "Examination Schedule")
//            }
        }
    }
}
