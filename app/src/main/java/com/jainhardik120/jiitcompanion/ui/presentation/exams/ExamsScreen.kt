package com.jainhardik120.jiitcompanion.ui.presentation.exams

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity

@Composable
fun ExamsScreen(
    userInfo: UserEntity,
    viewModel: ExamsViewModel = hiltViewModel(),
    token: String? = null
) {

}