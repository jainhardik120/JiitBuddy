package com.jainhardik120.jiitcompanion.ui.presentation.grades


import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.jainhardik120.jiitcompanion.data.local.entity.ResultEntity
import com.jainhardik120.jiitcompanion.domain.model.MarksRegistration
import com.jainhardik120.jiitcompanion.util.UiEvent
import java.io.File


@Composable
fun GradesScreen(
    viewModel: GradesViewModel = hiltViewModel()
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {

        })
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect {
            when (it) {
                is UiEvent.OpenPdf -> {
                    try {
                        val pdfOpenIntent = Intent(Intent.ACTION_VIEW)
                        pdfOpenIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        pdfOpenIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        pdfOpenIntent.setDataAndType(
                            FileProvider.getUriForFile(
                                context,
                                context.applicationContext.packageName + ".provider",
                                File(it.path)
                            ), "application/pdf"
                        )
                        launcher.launch(pdfOpenIntent)
                    } catch (e: Exception) {
                        Log.d("myApp", "onCreateView: $e")
                    }
                }

                else -> {}
            }
        }
    }
    val state = viewModel.state
    Scaffold(floatingActionButton = {
        ExtendedFloatingActionButton(
            onClick = { viewModel.onEvent(GradesScreenEvent.ButtonViewMarksClicked) },
            shape = MaterialTheme.shapes.large,
            expanded = true,
            icon = {
                Icon(
                    Icons.Outlined.FileDownload,
                    contentDescription = "Files Download Icon"
                )
            },
            text = {
                Text(text = "View Marks")
            }
        )
    }
    ) {

        Column(
            Modifier
                .padding(it)
                .fillMaxSize()) {

            Spacer(Modifier.height(16.dp))
            if(state.results.isNotEmpty()){
                GradesChart(modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp), resultEntities = state.results)
                Spacer(Modifier.height(16.dp))
            }
            LazyColumn() {
                items(state.results.size) { index ->
                    ResultItem(resultEntity = state.results[index])
                    if (index != state.results.size - 1) {
                        Divider()
                    }
                }

            }
        }

    }
    if (state.isMarksDialogOpened && state.isMarksRegistrationsLoaded) {
        SubjectMarksDialog(registrations = state.marksRegistrations, onClick = {
            viewModel.onEvent(GradesScreenEvent.MarksClicked(it))
        }, onDismiss = {
            viewModel.onEvent(GradesScreenEvent.MarksDialogDismissed)
        })
    }
}


@Composable
fun SubjectMarksDialog(
    registrations: List<MarksRegistration>,
    onClick: (registration: MarksRegistration) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        title = {
            Text(
                text = "Subject Marks",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth(), content = {
                itemsIndexed(registrations) { _, registration ->
                    DropdownMenuItem(text = {
                        Text(
                            text = registration.registrationcode,
                            modifier = Modifier
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }, onClick = { onClick(registration) })
                }
            })
        },
        confirmButton = {

        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(text = "Cancel")
            }
        })
}


@Composable
fun ResultItem(resultEntity: ResultEntity) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column() {
            Text(
                text = resultEntity.stynumber.toString(),
                style = MaterialTheme.typography.headlineLarge
            )
        }
        Spacer(Modifier.width(4.dp))
        Column(verticalArrangement = Arrangement.SpaceAround, modifier = Modifier.weight(1f)) {
            Text(text = "SGPA : ${resultEntity.sgpa}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "CGPA : ${resultEntity.cgpa}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResultItemPreview() {
    ResultItem(
        resultEntity = ResultEntity(
            9.3, 181.0, 65, 181.0, 19.5,
            19.5, 8.8, 1, 19.5, 19.5, 19.5, 65,
            181.0, 181.0, 19.5
        )
    )
}