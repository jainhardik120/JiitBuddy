package com.jainhardik120.jiitcompanion.ui.presentation.attendance

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity

@Composable
fun AttendanceScreen(
    userInfo : UserEntity,
    viewModel: AttendanceViewModel = hiltViewModel(),
    token: String? = null
){

}