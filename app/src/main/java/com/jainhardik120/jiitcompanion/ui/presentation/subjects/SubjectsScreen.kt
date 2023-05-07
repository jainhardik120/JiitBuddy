package com.jainhardik120.jiitcompanion.ui.presentation.subjects

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jainhardik120.jiitcompanion.domain.model.SubjectItem
import com.jainhardik120.jiitcompanion.util.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectsScreen(
    viewModel: SubjectsViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(key1 = true) {
        viewModel.initialize()
        viewModel.uiEvent.collect {
            when (it) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(it.message)
                }

                else -> {

                }

            }
        }
    }

    var expanded by remember { mutableStateOf(false) }
    val state = viewModel.state
    if (!state.isOffline) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            modifier = Modifier.fillMaxSize()
        ) {
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
                    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = {
                        expanded = !expanded
                    }) {
                        OutlinedTextField(
                            value = viewModel.state.selectedSemesterCode,
                            onValueChange = {

                            },
                            modifier = Modifier
                                .menuAnchor().fillMaxWidth(),
                            label = { Text(text = "Semester") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            }, readOnly = true
                        )
                        if (viewModel.state.registrations.isNotEmpty()) {
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }) {
                                viewModel.state.registrations.forEach {
                                    DropdownMenuItem(
                                        text = { Text(text = it.registrationcode) },
                                        onClick = {
                                            viewModel.onEvent(
                                                SubjectsScreenEvent.OnSemesterChanged(
                                                    it
                                                )
                                            )
                                            expanded = false
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                    )
                                }
                            }
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Row(Modifier.fillMaxWidth()) {
                Column() {
                    Text(text = item.subjectdesc)
                    Text(text = item.subjectcode)
                }
            }
            Spacer(Modifier.height(4.dp))
            Row(Modifier.fillMaxWidth()) {
                Column() {
                    for (i in item.faculties) {
                        Row(Modifier.fillMaxWidth()) {
                            Text(
                                text = "${
                                    when (i.subjectcomponentcode) {
                                        "L" -> {
                                            "Lecture"
                                        }

                                        "T" -> {
                                            "Tutorial"
                                        }

                                        "P" -> {
                                            "Practical"
                                        }

                                        else -> {
                                            i.subjectcomponentcode
                                        }
                                    }
                                } : ${i.employeename}"
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = item.credits.toString(), style = MaterialTheme.typography.headlineLarge)
        }
    }
}