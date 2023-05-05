package com.jainhardik120.jiitcompanion.domain.model

data class AttendanceItem(
    val subjectId: String,
    val subjectDesc: String,
    val attendancePercentage: Int,
    val totalClass:Int,
    val totalPres:Int,
    val attendanceDetailText: String,
    val componentIdText : ArrayList<String>
)