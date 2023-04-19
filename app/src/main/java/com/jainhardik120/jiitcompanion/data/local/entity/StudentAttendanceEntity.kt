package com.jainhardik120.jiitcompanion.data.local.entity

import androidx.room.Entity

@Entity(tableName = "attendance_detail_table", primaryKeys = arrayOf("studentid", "registrationid", "subjectid"))
data class StudentAttendanceEntity(
    val studentid : String,
    val registrationid: String,
    val LTpercantage: Double? = null,
    val Lpercentage: Double? = null,
    val Lsubjectcomponentcode: String? = null,
    val Lsubjectcomponentid: String? = null,
    val Ltotalclass: Double? = null,
    val Ltotalpres: Double? = null,
    val Ppercentage: Double? = null,
    val Psubjectcomponentcode: String? = null,
    val Psubjectcomponentid: String? = null,
    val Tpercentage: Double? = null,
    val Tsubjectcomponentcode: String? = null,
    val Tsubjectcomponentid: String? = null,
    val Ttotalclass: Double? = null,
    val Ttotalpres: Double? = null,
    val abseent: Double? = null,
    val slno: Int? = null,
    val subjectcode: String,
    val subjectid: String
)