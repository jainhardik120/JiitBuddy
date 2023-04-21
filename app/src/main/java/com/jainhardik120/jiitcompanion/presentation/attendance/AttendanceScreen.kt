package com.jainhardik120.jiitcompanion.presentation.attendance

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity
import com.jainhardik120.jiitcompanion.presentation.login.LoginViewModel

@Composable
fun AttendanceScreen(
    userInfo : UserEntity,
    viewModel: AttendanceViewModel = hiltViewModel(),
    token: String? = null
){

}