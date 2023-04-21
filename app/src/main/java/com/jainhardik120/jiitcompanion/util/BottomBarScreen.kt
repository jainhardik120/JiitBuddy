package com.jainhardik120.jiitcompanion.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
){
    object Home : BottomBarScreen(
        route = "home",
        title = "Home",
        icon = Icons.Default.Home
    )

    object Attendance : BottomBarScreen(
        route = "attendance",
        title = "Attendance",
        icon = Icons.Default.Person
    )

    object Performance : BottomBarScreen(
        route = "performance",
        title = "Grades",
        icon = Icons.Default.Settings
    )

    object ExamSchedule : BottomBarScreen(
        route = "exam_schedule",
        title = "Exams",
        icon = Icons.Default.Settings
    )

    object Subjects : BottomBarScreen(
        route = "subjects",
        title = "Subjects",
        icon = Icons.Default.Settings
    )
}
