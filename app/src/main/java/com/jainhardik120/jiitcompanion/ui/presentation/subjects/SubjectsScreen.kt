package com.jainhardik120.jiitcompanion.ui.presentation.subjects

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity
import com.jainhardik120.jiitcompanion.ui.presentation.grades.GradesViewModel

@Composable
fun SubjectsScreen(
    userInfo: UserEntity,
    viewModel: GradesViewModel = hiltViewModel(),
    token: String? = null
) {

}