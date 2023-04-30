package com.jainhardik120.jiitcompanion.ui.presentation.attendance

import com.jainhardik120.jiitcompanion.data.local.entity.StudentAttendanceRegistrationEntity
import com.jainhardik120.jiitcompanion.domain.model.AttendanceItem
import java.time.LocalDate


sealed class AttendanceScreenEvent{
    object DismissBottomSheet:AttendanceScreenEvent()
    object OnKeyboardDone:AttendanceScreenEvent()
    data class OnSemesterChanged(val semester : StudentAttendanceRegistrationEntity): AttendanceScreenEvent()
    data class OnAttendanceItemClicked(val attendanceItem : AttendanceItem): AttendanceScreenEvent()
    data class OnDayClicked(val day : LocalDate): AttendanceScreenEvent()
    data class OnAttendanceWarningTextChanged(val warning:String):AttendanceScreenEvent()
}