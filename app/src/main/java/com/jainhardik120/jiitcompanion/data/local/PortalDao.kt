package com.jainhardik120.jiitcompanion.data.local

import androidx.room.*
import com.jainhardik120.jiitcompanion.data.local.entity.StudentAttendanceRegistrationEntity
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity

@Dao
interface PortalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(userEntity: UserEntity)

    @Delete
    suspend fun deleteUser(userEntity: UserEntity)

    @Query("DELETE FROM user_table WHERE enrollmentno = :enrollmentno")
    suspend fun deleteUserByEnrollment(enrollmentno: String)

    @Query("SELECT * FROM user_table WHERE enrollmentno = :enrollmentno AND password = :password")
    suspend fun getUserByEnrollPass(enrollmentno: String, password: String): List<UserEntity>

    @Query("SELECT * FROM attendance_registration_table WHERE studentid = :studentid")
    suspend fun getStudentAttendanceRegistrationDetails(studentid: String):List<StudentAttendanceRegistrationEntity>
}