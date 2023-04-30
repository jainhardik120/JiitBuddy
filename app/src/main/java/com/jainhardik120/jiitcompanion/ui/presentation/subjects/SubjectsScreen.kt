package com.jainhardik120.jiitcompanion.ui.presentation.subjects

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import com.jainhardik120.jiitcompanion.data.repository.model.RegisteredSubject
import com.jainhardik120.jiitcompanion.domain.model.SubjectItem
import com.jainhardik120.jiitcompanion.ui.presentation.attendance.AttendanceScreenEvent

@Composable
fun SubjectsScreen(
    viewModel: SubjectsViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        viewModel.initialize()
    }
    var expanded by remember { mutableStateOf(false) }
    var dropMenuSize by remember {
        mutableStateOf(Size.Zero)
    }
    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown
    val state = viewModel.state
    if (!state.isOffline) {
        Scaffold(Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .padding(it)
                    .fillMaxSize()
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
                                dropMenuSize = layoutCoordinates.size.toSize()
                            }
                            .clickable(enabled = true) {
                                expanded = !expanded
                            },
                        label = { Text(text = "Semester") },
                        trailingIcon = {
                            IconButton(onClick = { expanded = !expanded }) {
                                Icon(icon, "Expand/Collapse Menu")
                            }
                        }, readOnly = true
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .width(with(LocalDensity.current) { dropMenuSize.width.toDp() })
                    ) {
                        viewModel.state.registrations.forEach {
                            DropdownMenuItem(
                                text = { Text(text = it.registrationcode) },
                                onClick = {
                                    viewModel.onEvent(SubjectsScreenEvent.OnSemesterChanged(it))
                                    expanded = false
                                })
                        }
                    }
                }
                LazyColumn() {
                    itemsIndexed(state.subjects) { index, item ->
                        SubjectItem(item)
                        if (index != state.subjects.size - 1) {
                            Divider()
                        }
                    }
                }
            }
        }
    } else {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Sorry, this content is not available in offline mode :-(",
                modifier = Modifier.fillMaxWidth(0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SubjectItem(item: SubjectItem) {
    Row(
        Modifier
            .padding(8.dp)
    ) {
        Text(text = item.toString())
    }
}