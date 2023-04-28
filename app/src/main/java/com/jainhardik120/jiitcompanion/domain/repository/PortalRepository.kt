package com.jainhardik120.jiitcompanion.domain.repository

import com.jainhardik120.jiitcompanion.data.local.entity.ResultEntity
import com.jainhardik120.jiitcompanion.data.local.entity.StudentAttendanceEntity
import com.jainhardik120.jiitcompanion.util.Resource
import com.jainhardik120.jiitcompanion.data.local.entity.StudentAttendanceRegistrationEntity
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity
import com.jainhardik120.jiitcompanion.data.repository.model.AttendanceEntry
import com.jainhardik120.jiitcompanion.domain.model.MarksRegistration
import kotlinx.coroutines.flow.Flow


interface PortalRepository {

    fun lastUser(): Resource<Pair<String, String>>

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
        studentid: String, stynumber: Int, token: String
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


    suspend fun getMarksRegistration(
        instituteid: String, studentid: String, token: String
    ): Resource<List<MarksRegistration>>

    suspend fun getMarksPdf(
        studentid: String, instituteid: String, registrationid: String, registrationCode: String, token: String
    ):Resource<String>
}