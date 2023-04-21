package com.jainhardik120.jiitcompanion.domain.repository

import com.jainhardik120.jiitcompanion.util.Resource
import com.jainhardik120.jiitcompanion.data.local.entity.StudentAttendanceRegistrationEntity
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow


interface PortalRepository {

    fun loginUser(enrollmentno: String, password: String): Flow<Resource<Pair<UserEntity, String>>>
    fun lastUser(): Resource<Pair<String, String>>

    fun getAttendanceRegistrationDetails(
        clientid: String,
        instituteid: String,
        studentid: String,
        membertype: String, token: String
    ): Flow<Resource<List<StudentAttendanceRegistrationEntity>>>
}