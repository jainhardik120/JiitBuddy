package com.jainhardik120.jiitcompanion.data.local.entity

import androidx.room.Entity

@Entity(tableName = "exam_schedule_table", primaryKeys = ["studentId", "examEvent", "subjectdesc"])
data class ExamScheduleEntity(
    val studentId: String,
    val examEvent: String,
    val datetime: String,
    val datetimeupto: String,
    val subjectdesc: String,
    val roomcode: String,
    val seatno: String,
)

//data class Subjectinfo(
//    val branchcode: String,
//    val datetime: String,
//    val datetimefrom: String,
//    val datetimeupto: String,
//    val dsid: String,
//    val examcode: String,
//    val groupwiseds: String,
//    val groupwiseexclude: String,
//    val programcode: String,
//    val roomcode: String,
//    val seatno: String,
//    val status: Any,
//    val subjectcode: String,
//    val subjectdesc: String,
//    val subjectid: String
//)
//
//    {
//        "datetimeupto": "10:00 am to 12:00 pm",
//        "branchcode": "COMPUTER SCIENCE & ENGINEERING",
//        "dsid": "JIDSI2304A0000013",
//        "groupwiseds": "Y",
//        "subjectcode": "18B11EC213",
//        "datetimefrom": "10:00 am",
//        "subjectid": "190115",
//        "subjectdesc": "DIGITAL SYSTEMS (18B11EC213) ",
//        "datetime": "19/05/2023",
//        "programcode": "BACHELOR OF TECHNOLOGY",
//        "groupwiseexclude": "I",
//        "roomcode": "G9",
//        "examcode": "JIIT",
//        "seatno": "H9",
//        "status": null
//    },
//    {
//        "datetimeupto": "02:30 pm to 04:30 pm",
//        "branchcode": "COMPUTER SCIENCE & ENGINEERING",
//        "dsid": "JIDSI2304A0000013",
//        "groupwiseds": "Y",
//        "subjectcode": "15B1NHS434",
//        "datetimefrom": "02:30 pm",
//        "subjectid": "150133",
//        "subjectdesc": "PRINCIPLES OF MANAGEMENT (15B1NHS434) ",
//        "datetime": "20/05/2023",
//        "programcode": "BACHELOR OF TECHNOLOGY",
//        "groupwiseexclude": "I",
//        "roomcode": "G8",
//        "examcode": "JIIT",
//        "seatno": "H3",
//        "status": null
//    },
//    {
//        "datetimeupto": "02:30 pm to 04:30 pm",
//        "branchcode": "COMPUTER SCIENCE & ENGINEERING",
//        "dsid": "JIDSI2304A0000013",
//        "groupwiseds": "Y",
//        "subjectcode": "15B11CI411",
//        "datetimefrom": "02:30 pm",
//        "subjectid": "150110",
//        "subjectdesc": "ALGORITHMS AND PROBLEM SOLVING (15B11CI411) ",
//        "datetime": "23/05/2023",
//        "programcode": "BACHELOR OF TECHNOLOGY",
//        "groupwiseexclude": "I",
//        "roomcode": null,
//        "examcode": null,
//        "seatno": null,
//        "status": null
//    },
//    {
//        "datetimeupto": "10:00 am to 12:00 pm",
//        "branchcode": "COMPUTER SCIENCE & ENGINEERING",
//        "dsid": "JIDSI2304A0000013",
//        "groupwiseds": "Y",
//        "subjectcode": "19B13BT211",
//        "datetimefrom": "10:00 am",
//        "subjectid": "190067",
//        "subjectdesc": "ENVIRONMENTAL STUDIES (19B13BT211) ",
//        "datetime": "25/05/2023",
//        "programcode": "BACHELOR OF TECHNOLOGY",
//        "groupwiseexclude": "I",
//        "roomcode": null,
//        "examcode": null,
//        "seatno": null,
//        "status": null
//    },
//    {
//        "datetimeupto": "02:30 pm to 04:30 pm",
//        "branchcode": "COMPUTER SCIENCE & ENGINEERING",
//        "dsid": "JIDSI2304A0000013",
//        "groupwiseds": "Y",
//        "subjectcode": "15B11MA301",
//        "datetimefrom": "02:30 pm",
//        "subjectid": "150066",
//        "subjectdesc": "PROBABILITY AND RANDOM PROCESSES (15B11MA301) ",
//        "datetime": "26/05/2023",
//        "programcode": "BACHELOR OF TECHNOLOGY",
//        "groupwiseexclude": "I",
//        "roomcode": null,
//        "examcode": null,
//        "seatno": null,
//        "status": null
//    }