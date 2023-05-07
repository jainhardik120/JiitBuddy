package com.jainhardik120.jiitcompanion.ui.presentation.grades


import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import com.jainhardik120.jiitcompanion.ui.components.icons.FileDownload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.jainhardik120.jiitcompanion.data.remote.model.ResultEntity
import com.jainhardik120.jiitcompanion.data.remote.model.ResultDetailEntity
import com.jainhardik120.jiitcompanion.data.remote.model.MarksRegistration
import com.jainhardik120.jiitcompanion.util.UiEvent
import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradesScreen(
    viewModel: GradesViewModel = hiltViewModel()
) {

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(key1 = true) {
        viewModel.initialize()
        viewModel.uiEvent.collect {
            when (it) {

                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(it.message)}
                else -> {}
            }
        }
    }
    val state = viewModel.state
    if (!state.isOffline) {
        Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState)}
        ) {
            Column(
                Modifier
                    .padding(it)
                    .fillMaxSize()
            ) {

                Spacer(Modifier.height(16.dp))
                if (state.results.isNotEmpty()) {
                    GradesChart(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp), resultEntities = state.results
                    )
                    Spacer(Modifier.height(16.dp))
                }
                LazyColumn() {
                    items(state.results.size) { index ->
                        ResultItem(resultEntity = state.results[index], onClick = {
                            viewModel.onEvent(GradesScreenEvent.ResultItemClicked(state.results[index].stynumber))
                        })
                        if (index != state.results.size - 1) {
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


    if (state.isBottomSheetExpanded && state.isDetailDataReady) {
        ModalBottomSheet(onDismissRequest = {
            viewModel.onEvent(GradesScreenEvent.BottomSheetDismissed)
        }, sheetState = bottomSheetState) {
            LazyColumn(content = {
                itemsIndexed(state.detailData) { index, item ->
                    ResultDetailItem(item = item)
                    if(index!=state.detailData.size-1){
                        Divider()
                    }
                }
            })
        }
    }
}

@Preview
@Composable
fun ResultDetailItemPreview() {
    val item = ResultDetailEntity(
        cgpapoint = 36.0,
        course_credits = 4.0,
        creditEarnedInSemeseter = 4.0,
        earned_credit = 4.0,
        equivalent_grade_point = 9.0,
        grade = "A",
        gradePointEarnedInSemeseter = "null",
        gradepoint = 9,
        minorsubject = "N",
        passfail = "N",
        sgpapoint = 36.0,
        status = "P",
        subjectcode = "15B11CI111",
        subjectdesc = "SOFTWARE DEVELOPMENT FUNDAMENTALS-1"
    )
    ResultDetailItem(item = item)
}

@Composable
fun ResultDetailItem(item: ResultDetailEntity) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(12.dp)) {
        Row(Modifier.fillMaxWidth()) {
            Text(text = "${item.subjectdesc} ${item.subjectcode}", textAlign = TextAlign.Center)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Credit : ${item.earned_credit}/${item.course_credits}")
            Text(text = "Grade : ${item.gradepoint} ${item.grade}")
            Text(text = "SGPA Points : ${item.sgpapoint}")
        }
    }
}

@Composable
fun ResultItem(resultEntity: ResultEntity, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(
                enabled = true,
                onClick = { onClick() }
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = resultEntity.stynumber.toString(),
                style = MaterialTheme.typography.headlineLarge
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(
            verticalArrangement = Arrangement.SpaceAround, modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Row(Modifier.fillMaxWidth()) {
                Text(
                    text = "SGPA : ${resultEntity.sgpa}",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "CGPA : ${resultEntity.cgpa}",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            Row(Modifier.fillMaxWidth()) {
                Text(text = "Grade Points : ${resultEntity.earnedgradepoints}/${resultEntity.registeredcredit * 10}")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResultItemPreview() {
    ResultItem(
        resultEntity = ResultEntity(
            9.3,
            181.0,
            65,
            181.0,
            19.5,
            19.5,
            8.8,
            1,
            19.5,
            19.5,
            19.5,
            65,
            181.0,
            181.0,
            19.5
        ),
        onClick = {

        }
    )
}