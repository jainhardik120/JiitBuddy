package com.jainhardik120.jiitcompanion.data.local.entity

import androidx.room.Entity

@Entity(tableName = "result_item_table")
data class ResultEntity(
    val cgpa: Double,
    val earnedgradepoints: Double,
    val prograde: Int,
    val prograsivegradepoints: Double,
    val prograsivetotalearnedcredit: Double,
    val registeredcredit: Double,
    val sgpa: Double,
    val stynumber: Int,
    val totalcoursecredit: Double,
    val totalearnedcredit: Double,
    val totalearnedcredits: Double,
    val totalgradepoints: Int,
    val totalpointsecuredcgpa: Double,
    val totalpointsecuredsgpa: Double,
    val totalregisteredcredit: Double
)