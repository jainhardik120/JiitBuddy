package com.jainhardik120.jiitcompanion.domain

data class ExamScheduleModel(
    val datetime: String,
    val datetimeupto: String,
    val subjectdesc: String,
    val roomcode: String,
    val seatno: String,
    val isCurrentPresent: Boolean,
    val day: String
)
