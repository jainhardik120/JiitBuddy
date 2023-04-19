package com.jainhardik120.jiitcompanion.domain.repository

import com.jainhardik120.jiitcompanion.core.util.Resource
import com.jainhardik120.jiitcompanion.data.local.UserEntity
import kotlinx.coroutines.flow.Flow


interface PortalRepository {

    fun loginUser(enrollmentno: String, password: String): Flow<Resource<UserEntity>>

}