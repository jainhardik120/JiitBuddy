package com.jainhardik120.jiitcompanion.data.local

import androidx.room.*
import com.jainhardik120.jiitcompanion.data.local.entity.ExamEventsEntity
import com.jainhardik120.jiitcompanion.data.local.entity.ExamRegistrationsEntity
import com.jainhardik120.jiitcompanion.data.local.entity.ExamScheduleEntity
import com.jainhardik120.jiitcompanion.data.local.entity.StudentAttendanceEntity
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

    @Query("SELECT * FROM user_table WHERE username = :username AND password = :password")
    suspend fun getUserByEnrollPass(username: String, password: String): List<UserEntity>

    @Query("SELECT * FROM attendance_registration_table WHERE studentid = :studentid")
    suspend fun getStudentAttendanceRegistrationDetails(studentid: String): List<StudentAttendanceRegistrationEntity>

    @Query("UPDATE user_table SET lastAttendanceRegistrationId = :registrationId WHERE enrollmentno = :enrollmentno")
    suspend fun updateUserLastAttendanceRegistration(enrollmentno: String, registrationId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceRegistrations(registrations: List<StudentAttendanceRegistrationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceEntities(attendance: List<StudentAttendanceEntity>)

    @Query("SELECT * FROM attendance_detail_table WHERE studentid = :studentid AND registrationid = :registrationid")
    suspend fun getStudentAttendanceOfRegistrationId(
        studentid: String,
        registrationid: String
    ): List<StudentAttendanceEntity>

    @Query("DELETE FROM user_table WHERE memberid = :studentid")
    suspend fun deleteUserEntity(studentid: String)

    @Query("DELETE FROM attendance_registration_table WHERE studentid = :studentid")
    suspend fun deleteAttendanceRegistrations(studentid: String)

    @Query("DELETE FROM attendance_detail_table WHERE studentid = :studentid")
    suspend fun deleteAttendanceEntity(studentid: String)

    @Query("DELETE FROM exam_events_table WHERE studentId = :studentid")
    suspend fun deleteExamEvents(studentid: String)

    @Query("DELETE FROM exam_registrations_table WHERE studentId = :studentid")
    suspend fun deleteExamRegistrations(studentid: String)

    @Query("DELETE FROM exam_schedule_table WHERE studentId = :studentid")
    suspend fun deleteExamSchedules(studentid: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExamEvents(events: List<ExamEventsEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExamRegistrations(registrations: List<ExamRegistrationsEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExamSchedules(schedules: List<ExamScheduleEntity>)

    @Query("SELECT * FROM exam_registrations_table WHERE studentId = :studentid")
    suspend fun getExamRegistrations(studentid: String): List<ExamRegistrationsEntity>

    @Query("SELECT * FROM exam_events_table WHERE studentId = :studentid AND registrationid = :registrationid")
    suspend fun getExamEvents(studentid: String, registrationid:String): List<ExamEventsEntity>

    @Query("SELECT * FROM exam_schedule_table WHERE studentId = :studentid AND examEvent = :examEvent")
    suspend fun getExamSchedules(studentid: String, examEvent:String): List<ExamScheduleEntity>
}