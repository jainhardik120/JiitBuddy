package com.jainhardik120.jiitcompanion.data.local.entity

import androidx.room.Entity

@Entity(tableName = "exam_schedule_table", primaryKeys = ["studentId", "examEvent", "subjectdesc"])
data class ExamScheduleEntity(
    val studentId:String,
    val examEvent:String,
    val datetime:String,
    val datetimeupto:String,
    val subjectdesc:String,
    val roomcode:String,
    val seatno:String,
)
