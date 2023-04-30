package com.jainhardik120.jiitcompanion.data.repository.model

import kotlinx.serialization.Serializable

@Serializable
data class AttendanceEntry(
    val attendanceby: String,
    val classtype: String,
    val datetime: String,
    val present: String
)