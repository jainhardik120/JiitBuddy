package com.jainhardik120.jiitcompanion.data.local.entity

import androidx.room.Entity

@Entity(tableName = "attendance_detail_table", primaryKeys = arrayOf("studentid", "registrationid", "subjectid"))
data class StudentAttendanceEntity(
    val studentid : String,
    val registrationid: String,
    val LTpercantage: Double? = 0.0,
    val Lpercentage: Double? = 0.0,
    val Lsubjectcomponentcode: String? = null,
    val Lsubjectcomponentid: String? = null,
    val Ltotalclass: Double? = 0.0,
    val Ltotalpres: Double? = 0.0,
    val Ppercentage: Double? = 0.0,
    val Psubjectcomponentcode: String? = null,
    val Psubjectcomponentid: String? = null,
    val Tpercentage: Double? = 0.0,
    val Tsubjectcomponentcode: String? = null,
    val Tsubjectcomponentid: String? = null,
    val Ttotalclass: Double? = 0.0,
    val Ttotalpres: Double? = 0.0,
    val abseent: Double? = 0.0,
    val slno: Int? = 0,
    val subjectcode: String,
    val subjectid: String
)