package com.jainhardik120.jiitcompanion.ui.presentation.grades

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity

@Composable
fun GradesScreen(
    userInfo: UserEntity,
    viewModel: GradesViewModel = hiltViewModel(),
    token: String? = null
) {

}