package com.jainhardik120.jiitcompanion.ui.presentation.grades

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun GradesScreen(
    viewModel: GradesViewModel = hiltViewModel()
) {
    val state = viewModel.state

}

@Composable
fun ResultItem(){

}