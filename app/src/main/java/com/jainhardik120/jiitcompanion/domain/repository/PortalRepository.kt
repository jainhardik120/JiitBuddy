package com.jainhardik120.jiitcompanion.domain.repository

import com.jainhardik120.jiitcompanion.data.local.entity.ExamEventsEntity
import com.jainhardik120.jiitcompanion.data.local.entity.ExamRegistrationsEntity
import com.jainhardik120.jiitcompanion.data.local.entity.StudentAttendanceEntity
import com.jainhardik120.jiitcompanion.data.local.entity.StudentAttendanceRegistrationEntity
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity
import com.jainhardik120.jiitcompanion.data.remote.model.AttendanceEntry
import com.jainhardik120.jiitcompanion.data.remote.model.MarksRegistration
import com.jainhardik120.jiitcompanion.data.remote.model.RegisteredSubject
import com.jainhardik120.jiitcompanion.data.remote.model.ResultDetailEntity
import com.jainhardik120.jiitcompanion.data.remote.model.ResultEntity
import com.jainhardik120.jiitcompanion.data.remote.model.SubjectSemesterRegistrations
import com.jainhardik120.jiitcompanion.domain.ExamScheduleModel
import com.jainhardik120.jiitcompanion.util.Resource


interface PortalRepository {

    fun lastUser(): Resource<Pair<String, String>>

    fun incrementSheetOpening()

    fun getOpenings():Int

    fun getIsOpened():Boolean

    fun updateOpened()

    fun getAttendanceWarning():Int

    fun updateAttendanceWarning(warning:Int)

    suspend fun getLastAttendanceRegistration(enrollmentno: String):Resource<String>

    suspend fun loginUser(
        enrollmentno: String,
        password: String
    ): Resource<Pair<UserEntity, String>>

    suspend fun updateUserLastAttendanceRegistrationId(enrollmentno: String, registrationid: String)

    suspend fun getAttendanceRegistrationDetails(
        clientid: String,
        instituteid: String,
        studentid: String,
        membertype: String, token: String
    ): Resource<List<StudentAttendanceRegistrationEntity>>

    suspend fun getStudentResultData(
        instituteid: String,
        studentid: String, token: String
    ): Resource<List<ResultEntity>>

    suspend fun getAttendanceDetails(
        clientid: String,
        instituteid: String,
        studentid: String,
        stynumber: Int, registrationid: String, token: String
    ): Resource<List<StudentAttendanceEntity>>

    suspend fun getSubjectAttendanceDetails(
        clientid: String,
        instituteid: String,
        studentid: String,
        subjectId: String, registrationid: String, cmpidkey: String, token: String
    ): Resource<List<AttendanceEntry>>

    suspend fun getExamRegistrations(
        clientid: String,
        instituteid: String,
        studentid: String, token: String
    ): Resource<List<ExamRegistrationsEntity>>

    suspend fun getExamEvents(
        studentid: String,
        instituteid: String,
        registrationid: String, token: String
    ): Resource<List<ExamEventsEntity>>

    suspend fun getExamSchedules(
        exameventid: String,
        registrationid: String,
        instituteid: String,
        studentid: String, token: String
    ): Resource<List<ExamScheduleModel>>

    suspend fun getMarksRegistration(
        instituteid: String, studentid: String, token: String
    ): Resource<List<MarksRegistration>>

    suspend fun getSubjectsRegistrations(
        instituteid: String, studentid: String, token: String
    ): Resource<List<SubjectSemesterRegistrations>>

    suspend fun getSubjects(
        instituteid: String, registrationid: String, studentid: String, token: String
    ): Resource<List<RegisteredSubject>>

    suspend fun getMarksPdf(
        studentid: String, instituteid: String, registrationid: String, registrationCode: String, token: String
    ):Resource<String>

    suspend fun logOut(studentid: String)

    suspend fun getResultDetail(
        studentid: String, instituteid: String, stynumber: Int, token: String
    ):Resource<List<ResultDetailEntity>>
}