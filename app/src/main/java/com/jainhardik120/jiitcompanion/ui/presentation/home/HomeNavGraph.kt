package com.jainhardik120.jiitcompanion.ui.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity
import com.jainhardik120.jiitcompanion.ui.presentation.attendance.AttendanceScreen
import com.jainhardik120.jiitcompanion.ui.presentation.exams.ExamsScreen
import com.jainhardik120.jiitcompanion.ui.presentation.grades.GradesScreen
import com.jainhardik120.jiitcompanion.ui.presentation.subjects.SubjectsScreen
import com.jainhardik120.jiitcompanion.util.root.PortalNavArguments

@Composable
fun HomeNavGraph(
    navController: NavHostController,
    userEntity: UserEntity,
    token:String?,
    paddingValues: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = BottomBarScreen.Home.route + "/{userInfo}/{token}"
    ){
        composable(route = BottomBarScreen.Home.route + "/{userInfo}/{token}",
            arguments = PortalNavArguments.arguments
        ){
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)) {
                ProfileCard(userEntity = userEntity)
            }
        }
        composable(route = BottomBarScreen.Attendance.route + "/{userInfo}/{token}",
            arguments = PortalNavArguments.arguments
        ){
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)) {
                AttendanceScreen()
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
        }
    }
}