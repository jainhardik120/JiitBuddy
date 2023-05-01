package com.jainhardik120.jiitcompanion.ui.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jainhardik120.jiitcompanion.ui.presentation.attendance.AttendanceScreen
import com.jainhardik120.jiitcompanion.ui.presentation.exams.ExamsScreen
import com.jainhardik120.jiitcompanion.ui.presentation.grades.GradesScreen
import com.jainhardik120.jiitcompanion.ui.presentation.profile.ProfileScreen
import com.jainhardik120.jiitcompanion.ui.presentation.subjects.SubjectsScreen
import com.jainhardik120.jiitcompanion.ui.presentation.root.PortalNavArguments

@Composable
fun HomeNavGraph(
    navController: NavHostController,
    paddingValues: PaddingValues,
    userInfo:String,
    token:String,
    onReview: ()-> Unit
) {
    NavHost(
        navController = navController,
        startDestination = BottomBarScreen.Home.route + "/{userInfo}/{token}"
    ){
        composable(route = BottomBarScreen.Home.route + "/{userInfo}/{token}",
            arguments = PortalNavArguments.arguments
        ){
            it.arguments?.putString("userInfo", userInfo)
            it.arguments?.putString("token", token)
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)) {
                ProfileScreen()
            }
        }
        composable(route = BottomBarScreen.Attendance.route + "/{userInfo}/{token}",
            arguments = PortalNavArguments.arguments
        ){
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)) {
                AttendanceScreen(onReview={onReview()})
            }
        }
        composable(route = BottomBarScreen.ExamSchedule.route + "/{userInfo}/{token}",
            arguments = PortalNavArguments.arguments
        ){
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)) {
                ExamsScreen()
            }
        }
        composable(route = BottomBarScreen.Performance.route + "/{userInfo}/{token}",
            arguments = PortalNavArguments.arguments
        ){
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)) {
                GradesScreen()
            }
        }
        composable(route = BottomBarScreen.Subjects.route + "/{userInfo}/{token}",
            arguments = PortalNavArguments.arguments
        ){
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)) {
                SubjectsScreen()
            }
        }
    }
}
