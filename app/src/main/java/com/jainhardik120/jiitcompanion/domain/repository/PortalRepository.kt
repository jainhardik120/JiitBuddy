package com.jainhardik120.jiitcompanion.domain.repository

import com.jainhardik120.jiitcompanion.data.local.entity.ResultEntity
import com.jainhardik120.jiitcompanion.data.local.entity.StudentAttendanceEntity
import com.jainhardik120.jiitcompanion.util.Resource
import com.jainhardik120.jiitcompanion.data.local.entity.StudentAttendanceRegistrationEntity
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow


interface PortalRepository {

    fun lastUser(): Resource<Pair<String, String>>

    fun loginUser(enrollmentno: String, password: String): Flow<Resource<Pair<UserEntity, String>>>

    suspend fun updateUserLastAttendanceRegistrationId(enrollmentno: String, registrationid: String)

    fun getAttendanceRegistrationDetails(
        clientid: String,
        instituteid: String,
        studentid: String,
        membertype: String, token: String
    ): Flow<Resource<List<StudentAttendanceRegistrationEntity>>>

    fun getStudentResultData(
        instituteid: String,
        studentid: String, stynumber: Int, token: String
    ): Flow<Resource<List<ResultEntity>>>

    fun getAttendanceDetails(
        clientid: String,
        instituteid: String,
        studentid: String,
        stynumber: Int, registrationid: String, token: String
    ): Flow<Resource<List<StudentAttendanceEntity>>>
}