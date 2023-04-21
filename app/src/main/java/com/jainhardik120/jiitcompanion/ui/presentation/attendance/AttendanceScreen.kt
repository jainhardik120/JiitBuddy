package com.jainhardik120.jiitcompanion.ui.presentation.attendance

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import com.jainhardik120.jiitcompanion.ui.presentation.grades.ResultItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    viewModel: AttendanceViewModel = hiltViewModel()
) {
    var expanded by remember { mutableStateOf(false) }
    var dropMenuSize by remember {
        mutableStateOf(Size.Zero)
    }
    val state = viewModel.state
    val icon = if(expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(12.dp)) {
            OutlinedTextField(
                value = viewModel.state.selectedSemesterCode,
                onValueChange = {
//                viewModel.onEvent(AttendanceScreenEvent.OnSemesterChanged(it))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { layoutCoordinates ->
                        dropMenuSize = layoutCoordinates.size.toSize()
                    }
                    .clickable(enabled = true) {
                        expanded = !expanded
                    },
                label = { Text(text = "Semester") },
                trailingIcon = {
                    Icon(icon, "Expand/Collapse Menu", Modifier.clickable {
                        expanded = !expanded
                    })
                }, readOnly = true
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .width(with(LocalDensity.current) { dropMenuSize.width.toDp() })
            ) {
                viewModel.state.registrations.forEach{it->
                    DropdownMenuItem(text = { Text(text = it.registrationcode) }, onClick = {
                        viewModel.onEvent(AttendanceScreenEvent.OnSemesterChanged(it))
                        expanded = false
                    })
                }
            }
        }

        LazyColumn {
            items(state.attendanceData.size) { it ->
                Text(text = state.attendanceData[it].toString())
                Divider()
            }
        }
    }
}