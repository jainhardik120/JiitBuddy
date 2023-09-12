package com.jainhardik120.jiitcompanion.ui.presentation.exams

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jainhardik120.jiitcompanion.domain.ExamScheduleModel
import com.jainhardik120.jiitcompanion.util.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
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
    var expanded2 by remember { mutableStateOf(false) }
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
                ExposedDropdownMenuBox(expanded = expanded1, onExpandedChange = {
                    expanded1 = !expanded1
                }, modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = viewModel.state.selectedSemesterCode,
                        onValueChange = {

                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        label = { Text(text = "Semester") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded1)
                        }, readOnly = true
                    )
                    if (viewModel.state.registrations.isNotEmpty()) {
                        ExposedDropdownMenu(
                            expanded = expanded1,
                            modifier = Modifier.fillMaxWidth(),
                            onDismissRequest = { expanded1 = false }) {
                            viewModel.state.registrations.forEach {
                                DropdownMenuItem(
                                    text = { Text(text = it.registrationcode) },
                                    onClick = {
                                        viewModel.onEvent(
                                            ExamScreenEvent.OnSemesterChanged(
                                                it
                                            )
                                        )
                                        expanded1 = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                ExposedDropdownMenuBox(modifier = Modifier.fillMaxWidth(),expanded = expanded2, onExpandedChange = {
                    expanded2 = !expanded2
                }) {
                    OutlinedTextField(
                        value = viewModel.state.selectedEventDesc,
                        onValueChange = {

                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        label = { Text(text = "Exam Event") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded2)
                        }, readOnly = true
                    )
                    if (viewModel.state.events.isNotEmpty()) {
                        ExposedDropdownMenu(
                            expanded = expanded2,
                            modifier = Modifier.fillMaxWidth(),
                            onDismissRequest = { expanded2 = false }) {
                            viewModel.state.events.forEach {
                                DropdownMenuItem(
                                    text = { Text(text = it.exameventdesc) },
                                    onClick = {
                                        viewModel.onEvent(
                                            ExamScreenEvent.OnExamEventChanged(
                                                it
                                            )
                                        )
                                        expanded2 = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }
                }
            }
            LazyColumn(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(), content = {
                itemsIndexed(viewModel.state.schedule) { index, item ->
                    ExamScheduleItem(item)
                    if (index != viewModel.state.schedule.size - 1) {
                        Divider()
                    }
                }
            })
        }
    }
}

@Preview(showBackground = true, device = "spec:width=720px,height=1560px,dpi=240")
@Composable
fun ExamScheduleItemPreview() {
    ExamScheduleItem(
        item = ExamScheduleModel(
            "20/02/2023",
            "01:00 pm to 02:00 pm",
            "PRINCIPLES OF MANAGEMENT (15B1NHS434)",
            "G5",
            "A11",
            true,
            "Mon"
        )
    )
}

@Composable
fun ExamScheduleItem(item: ExamScheduleModel) {
    Surface/*(color = if(item.isCurrentPresent){MaterialTheme.colorScheme.surface}else{MaterialTheme.colorScheme.errorContainer})*/ {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp)
        ) {
            Row(Modifier.fillMaxWidth()) {
                Text(text = item.subjectdesc)
            }
            Spacer(Modifier.height(4.dp))
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f).fillMaxWidth()){
                    Text(
                        text = "${item.day} ${item.datetime}",
                        textAlign = TextAlign.Center
                    )
                    Text(text = item.datetimeupto)
                }
                Text(text = "${item.roomcode} ${item.seatno}")
            }
        }
    }
}