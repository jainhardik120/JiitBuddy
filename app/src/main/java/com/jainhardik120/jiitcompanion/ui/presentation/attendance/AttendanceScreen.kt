package com.jainhardik120.jiitcompanion.ui.presentation.attendance

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import com.jainhardik120.jiitcompanion.data.repository.model.AttendanceEntry
import com.jainhardik120.jiitcompanion.domain.model.AttendanceItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    viewModel: AttendanceViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        viewModel.getRegistrations()
    }
    var expanded by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var dropMenuSize by remember {
        mutableStateOf(Size.Zero)
    }
    val state = viewModel.state
    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Column(Modifier.fillMaxSize()) {
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
                    DropdownMenuItem(text = { Text(text = it.registrationcode) }, onClick = {
                        viewModel.onEvent(AttendanceScreenEvent.OnSemesterChanged(it))
                        expanded = false
                    })
                }
            }
        }

        LazyColumn {
            items(state.attendanceData.size) {
                AttendanceItem(
                    attendanceItem = state.attendanceData[it],
                    enabled = true,
                    onClick = {
                        viewModel.onEvent(AttendanceScreenEvent.OnAttendanceItemClicked(state.attendanceData[it]))
                    })
                if (it != state.attendanceData.size - 1) {
                    Divider(Modifier.padding(horizontal = 12.dp))
                }
            }
        }
    }

    if (state.isBottomSheetExpanded && state.isDetailDataReady) {
        ModalBottomSheet(onDismissRequest = {
            viewModel.onEvent(AttendanceScreenEvent.DismissBottomSheet)
        }, sheetState = bottomSheetState) {
            LazyColumn{
                items(state.attendanceEntries.size){
                    AttendanceEntryItem(state.attendanceEntries[it])
                    Divider()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AttendanceEntryPreview() {
    AttendanceEntryItem(attendanceEntry = AttendanceEntry(
        attendanceby = "ARADHANA NARANG",
        classtype = "REGULAR",
        datetime = "21/04/2023 (09:00:AM - 09:50 AM)",
        present = "Present",
    ))
}

@Composable
fun AttendanceEntryItem(attendanceEntry: AttendanceEntry) {
    Row(Modifier.padding(8.dp)) {
        Column(Modifier.weight(1f).padding(start = 8.dp)){
            Text(text = attendanceEntry.attendanceby)
            Text(text = attendanceEntry.classtype)
            Text(text = attendanceEntry.datetime)
        }
        Column(Modifier.align(Alignment.CenterVertically).padding(end = 8.dp)) {
            Text(text = attendanceEntry.present)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AttendanceItemPreview() {
    Column(Modifier.fillMaxWidth()) {
        AttendanceItem(
            attendanceItem = AttendanceItem(
                subjectId = "JISUB22020000001",
                subjectDesc = "PRINCIPLES OF MANAGEMENT(15B1NHS434)",
                attendancePercentage = 82,
                attendanceFractionText = "24\n----\n29",
                attendanceDetailText = "Lecture : 58.3 \nTutorial : 75.0\n",
                componentIdText = ArrayList(),
                warningNumber = 37
            ), enabled = true, onClick = {

            }
        )
    }
}

@Composable
fun AttendanceItem(attendanceItem: AttendanceItem, enabled: Boolean, onClick: () -> Unit) {
    Row(Modifier
        .clickable(
            enabled = enabled,
            onClick = {
                onClick()
            }
        )
        .padding(8.dp)) {
        Column(Modifier.fillMaxWidth(0.7f)) {
            Text(
                text = attendanceItem.subjectDesc.substringBefore("(", "("),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = attendanceItem.attendanceDetailText.substringBeforeLast("\n"))
        }
        Column(
            Modifier
                .fillMaxWidth(0.25f)
                .align(Alignment.CenterVertically)
        ) {
            Text(text = attendanceItem.attendanceFractionText, textAlign = TextAlign.Center)
        }
        Column(
            Modifier
                .align(Alignment.CenterVertically)
                .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                Text(text = "${attendanceItem.attendancePercentage}")
                CircularProgressIndicator(
                    (attendanceItem.attendancePercentage / 100.0).toFloat(),
                    modifier = Modifier.size(36.dp)
                )
            }
            if (attendanceItem.warningNumber > 0) {
                Text(text = "Attend: ${attendanceItem.warningNumber}")
            }
        }
    }
}