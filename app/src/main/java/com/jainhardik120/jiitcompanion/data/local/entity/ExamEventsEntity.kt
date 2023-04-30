package com.jainhardik120.jiitcompanion.data.local.entity

import androidx.room.Entity

@Entity(tableName = "exam_events_table", primaryKeys = ["studentId", "registrationid", "exameventid"])
data class ExamEventsEntity(
    val studentId:String,
    val eventfrom: Long,
    val exameventcode: String,
    val exameventdesc: String,
    val exameventid: String,
    val registrationid: String
)