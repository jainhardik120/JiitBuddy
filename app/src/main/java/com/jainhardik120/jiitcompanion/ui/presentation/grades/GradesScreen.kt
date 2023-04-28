package com.jainhardik120.jiitcompanion.ui.presentation.grades


import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.jainhardik120.jiitcompanion.data.local.entity.ResultEntity
import com.jainhardik120.jiitcompanion.domain.model.MarksRegistration
import com.jainhardik120.jiitcompanion.util.UiEvent
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradesScreen(
    viewModel: GradesViewModel = hiltViewModel()
) {
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult(), onResult = {

    })
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect {
            when (it) {
                is UiEvent.OpenPdf -> {
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
                    try {
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
    Button(onClick = {
        viewModel.onEvent(GradesScreenEvent.ButtonViewMarksClicked)
    }) {
        Text(text = "View Marks")
    }
    LazyColumn {
        items(state.results.size) { it ->
            ResultItem(resultEntity = state.results[it])
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
fun SubjectMarksDialog(registrations: List<MarksRegistration>, onClick: (registration : MarksRegistration)-> Unit, onDismiss: ()->Unit) {
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
                itemsIndexed(registrations) { index, registration ->
                    Column(Modifier.clickable(
                        enabled = true,
                        onClick = {
                            onClick(registration)
                        }
                    )) {
                        Text(
                            text = registration.registrationdesc,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            textAlign = TextAlign.Center
                        )
                    }
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
    OutlinedCard(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = resultEntity.stynumber.toString(), fontSize = 24.sp)
            Column() {
                Text(text = "SGPA : ${resultEntity.sgpa}")
                Text(text = "CGPA : ${resultEntity.cgpa}")
            }
        }
    }
}

@Preview
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