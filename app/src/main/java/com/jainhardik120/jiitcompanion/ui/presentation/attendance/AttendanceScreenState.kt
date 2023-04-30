package com.jainhardik120.jiitcompanion.ui.presentation.attendance

import com.jainhardik120.jiitcompanion.data.local.entity.StudentAttendanceRegistrationEntity
import com.jainhardik120.jiitcompanion.data.repository.model.AttendanceEntry
import com.jainhardik120.jiitcompanion.domain.model.AttendanceItem
import java.time.LocalDate

data class AttendanceScreenState(
    val registrations:List<StudentAttendanceRegistrationEntity> = emptyList(),
    val selectedSemesterCode: String = "",
    val selectedSemesterId: String = "",
    val attendanceData:List<AttendanceItem> = emptyList(),
    val attendanceWarning :Int = 80,
    val isDetailDataReady: Boolean = false,
    val isBottomSheetExpanded: Boolean = false,
    val attendanceEntries: List<AttendanceEntry> = emptyList(),
    val map: Map<LocalDate, Pair<Int, Int>> = emptyMap(),
    val stringMap: Map<LocalDate, List<AttendanceEntry>> = emptyMap(),
    val lastAttendanceDate:LocalDate?=null,
    val selectedDate:LocalDate?=null
)