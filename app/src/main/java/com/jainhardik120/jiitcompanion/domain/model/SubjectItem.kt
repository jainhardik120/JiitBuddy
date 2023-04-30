package com.jainhardik120.jiitcompanion.domain.model

data class SubjectItem(
    val audtsubject: String,
    val credits: Double,
    val minorsubject: String,
    val stytype: String,
    val remarks: String,
    val subjectcode: String,
    val subjectdesc: String,
    val subjectid: String,
    val faculties: List<Faculty>
)
