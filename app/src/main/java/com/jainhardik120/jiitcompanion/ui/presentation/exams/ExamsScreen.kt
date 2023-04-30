package com.jainhardik120.jiitcompanion.ui.presentation.exams

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import com.jainhardik120.jiitcompanion.util.UiEvent

@Composable
fun ExamsScreen(
    viewModel: ExamsViewModel = hiltViewModel()
) {
    val snackBarHostState = remember { SnackbarHostState() }
    LaunchedEffect(key1 = true, block = {
        viewModel.getRegistrations()
        viewModel.uiEvent.collect {
            when (it) {
                is UiEvent.ShowSnackbar -> {
                    snackBarHostState.showSnackbar(it.message)
                }
                else -> {

                }
            }
        }
    })
    var expanded1 by remember { mutableStateOf(false) }
    var dropMenuSize1 by remember {
        mutableStateOf(Size.Zero)
    }
    val icon1 = if (expanded1)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown
    var expanded2 by remember { mutableStateOf(false) }
    var dropMenuSize2 by remember {
        mutableStateOf(Size.Zero)
    }
    val icon2 = if (expanded2)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown
    Scaffold(snackbarHost = { SnackbarHost(hostState = snackBarHostState) }) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                OutlinedTextField(
                    value = viewModel.state.selectedSemesterCode,
                    onValueChange = {

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { layoutCoordinates ->
                            dropMenuSize1 = layoutCoordinates.size.toSize()
                        }
                        .clickable(enabled = true) {
                            expanded1 = !expanded1
                        },
                    label = { Text(text = "Semester") },
                    trailingIcon = {
                        IconButton(onClick = {
                            expanded1 = !expanded1
                            expanded2 = false
                        }) {
                            Icon(icon1, "Expand/Collapse Menu")
                        }
                    }, readOnly = true
                )
                DropdownMenu(
                    expanded = expanded1,
                    onDismissRequest = { expanded1 = false },
                    modifier = Modifier
                        .width(with(LocalDensity.current) { dropMenuSize1.width.toDp() })
                ) {
                    viewModel.state.registrations.forEach {
                        DropdownMenuItem(text = { Text(text = it.registrationcode) }, onClick = {
                            viewModel.onEvent(ExamScreenEvent.OnSemesterChanged(it))
                            expanded1 = false
                        })
                    }
                }
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                OutlinedTextField(
                    value = viewModel.state.selectedEventDesc,
                    onValueChange = {

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { layoutCoordinates ->
                            dropMenuSize2 = layoutCoordinates.size.toSize()
                        }
                        .clickable(enabled = true) {
                            expanded2 = !expanded2
                        },
                    label = { Text(text = "Exam Event") },
                    trailingIcon = {
                        IconButton(onClick = {
                            expanded2 = !expanded2
                            expanded1 = false}) {
                            Icon(icon2, "Expand/Collapse Menu")
                        }
                    }, readOnly = true
                )
                DropdownMenu(
                    expanded = expanded2,
                    onDismissRequest = { expanded2 = false },
                    modifier = Modifier
                        .width(with(LocalDensity.current) { dropMenuSize2.width.toDp() })
                ) {
                    viewModel.state.events.forEach {
                        DropdownMenuItem(text = { Text(text = it.exameventdesc) }, onClick = {
                            viewModel.onEvent(ExamScreenEvent.OnExamEventChanged(it))
                            expanded2 = false
                        })
                    }
                }
            }
        }
    }
}