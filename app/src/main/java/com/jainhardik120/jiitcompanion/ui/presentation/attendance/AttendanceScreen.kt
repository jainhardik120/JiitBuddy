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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import com.jainhardik120.jiitcompanion.data.local.entity.StudentAttendanceEntity
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
                AttendanceItem(studentAttendanceEntity = state.attendanceData[it])
                Divider()
            }
        }
    }
}

    /*
     StudentAttendanceEntity(studentid=JIAC2202375, registrationid=JIRUM22110000001, LTpercantage=79.5, Lpercentage=79.4, Lsubjectcomponentcode=L, Lsubjectcomponentid=JISCP19050000001, Ltotalclass=34.0, Ltotalpres=27.0, Ppercentage=0.0, Psubjectcomponentcode=, Psubjectcomponentid=, Tpercentage=80.0, Tsubjectcomponentcode=T, Tsubjectcomponentid=JISCP19050000002, Ttotalclass=10.0, Ttotalpres=8.0, abseent=2.0, slno=1, subjectcode=PROBABILITY AND RANDOM PROCESSES(15B11MA301), subjectid=150066),
     StudentAttendanceEntity(studentid=JIAC2202375, registrationid=JIRUM22110000001, LTpercantage=0.0, Lpercentage=88.0, Lsubjectcomponentcode=L, Lsubjectcomponentid=JISCP19050000001, Ltotalclass=25.0, Ltotalpres=22.0, Ppercentage=0.0, Psubjectcomponentcode=, Psubjectcomponentid=, Tpercentage=0.0, Tsubjectcomponentcode=, Tsubjectcomponentid=, Ttotalclass=0.0, Ttotalpres=0.0, abseent=3.0, slno=2, subjectcode=ALGORITHMS AND PROBLEM SOLVING(15B11CI411), subjectid=150110),
     StudentAttendanceEntity(studentid=JIAC2202375, registrationid=JIRUM22110000001, LTpercantage=0.0, Lpercentage=0.0, Lsubjectcomponentcode=, Lsubjectcomponentid=, Ltotalclass=0.0, Ltotalpres=0.0, Ppercentage=84.6, Psubjectcomponentcode=P, Psubjectcomponentid=JISCP19050000003, Tpercentage=0.0, Tsubjectcomponentcode=, Tsubjectcomponentid=, Ttotalclass=0.0, Ttotalpres=0.0, abseent=2.0, slno=3, subjectcode=ALGORITHMS AND PROBLEM SOLVING LAB(15B17CI471), subjectid=150112),
     StudentAttendanceEntity(studentid=JIAC2202375, registrationid=JIRUM22110000001, LTpercantage=82.8, Lpercentage=77.8, Lsubjectcomponentcode=L, Lsubjectcomponentid=JISCP19050000001, Ltotalclass=18.0, Ltotalpres=14.0, Ppercentage=0.0, Psubjectcomponentcode=, Psubjectcomponentid=, Tpercentage=90.9, Tsubjectcomponentcode=T, Tsubjectcomponentid=JISCP19050000002, Ttotalclass=11.0, Ttotalpres=10.0, abseent=4.0, slno=4, subjectcode=PRINCIPLES OF MANAGEMENT(15B1NHS434), subjectid=150133),
     StudentAttendanceEntity(studentid=JIAC2202375, registrationid=JIRUM22110000001, LTpercantage=0.0, Lpercentage=77.8, Lsubjectcomponentcode=L, Lsubjectcomponentid=JISCP19050000001, Ltotalclass=27.0, Ltotalpres=21.0, Ppercentage=0.0, Psubjectcomponentcode=, Psubjectcomponentid=, Tpercentage=0.0, Tsubjectcomponentcode=, Tsubjectcomponentid=, Ttotalclass=0.0, Ttotalpres=0.0, abseent=6.0, slno=5, subjectcode=ENVIRONMENTAL STUDIES(19B13BT211), subjectid=190067),
     StudentAttendanceEntity(studentid=JIAC2202375, registrationid=JIRUM22110000001, LTpercantage=73.7, Lpercentage=72.4, Lsubjectcomponentcode=L, Lsubjectcomponentid=JISCP19050000001, Ltotalclass=29.0, Ltotalpres=21.0, Ppercentage=0.0, Psubjectcomponentcode=, Psubjectcomponentid=, Tpercentage=77.8, Tsubjectcomponentcode=T, Tsubjectcomponentid=JISCP19050000002, Ttotalclass=9.0, Ttotalpres=7.0, abseent=8.0, slno=6, subjectcode=DIGITAL SYSTEMS(18B11EC213), subjectid=190115),
     StudentAttendanceEntity(studentid=JIAC2202375, registrationid=JIRUM22110000001, LTpercantage=0.0, Lpercentage=0.0, Lsubjectcomponentcode=, Lsubjectcomponentid=, Ltotalclass=0.0, Ltotalpres=0.0, Ppercentage=77.8, Psubjectcomponentcode=P, Psubjectcomponentid=JISCP19050000003, Tpercentage=0.0, Tsubjectcomponentcode=, Tsubjectcomponentid=, Ttotalclass=0.0, Ttotalpres=0.0, abseent=2.0, slno=7, subjectcode=DIGITAL SYSTEMS LAB(18B15EC213), subjectid=190116)]
    */

@Preview
@Composable
fun AttendanceItemPreview() {
    val attendanceItem = StudentAttendanceEntity(
        studentid="JIAC2202375",
        registrationid="JIRUM22110000001",
        LTpercantage=79.5,
        Lpercentage=79.4, Lsubjectcomponentcode="L", Lsubjectcomponentid="JISCP19050000001", Ltotalclass=34.0, Ltotalpres=27.0,
        Ppercentage=0.0, Psubjectcomponentcode="", Psubjectcomponentid="",
        Tpercentage=80.0, Tsubjectcomponentcode="T", Tsubjectcomponentid="JISCP19050000002", Ttotalclass=10.0, Ttotalpres=8.0,
        abseent=2.0, slno=1, subjectcode="PROBABILITY AND RANDOM PROCESSES(15B11MA301)", subjectid="150066")
    AttendanceItem(studentAttendanceEntity = attendanceItem)

}

@Composable
fun AttendanceItem(studentAttendanceEntity: StudentAttendanceEntity) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)) {
        Row(Modifier.fillMaxWidth()){
            Column() {
                Text(text = studentAttendanceEntity.subjectcode)
            }
            Column() {
                Box(Modifier.size(28.dp)) {
                    studentAttendanceEntity.LTpercantage?.div(100)
                        ?.let { CircularProgressIndicator(it.toFloat()) }
                }
            }
        }

    }
}