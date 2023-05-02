package com.jainhardik120.jiitcompanion.data.repository.model

data class ResultDetailEntity(
    val cgpapoint: Double,
    val course_credits: Double,
    val creditEarnedInSemeseter: Double,
    val earned_credit: Double,
    val equivalent_grade_point: Double,
    val grade: String,
    val gradePointEarnedInSemeseter: Any,
    val gradepoint: Int,
    val minorsubject: String,
    val passfail: String,
    val sgpapoint: Double,
    val status: String,
    val subjectcode: String,
    val subjectdesc: String
)