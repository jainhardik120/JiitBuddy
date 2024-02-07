package com.jainhardik120.jiitcompanion.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jainhardik120.jiitcompanion.data.local.entity.ExamEventsEntity
import com.jainhardik120.jiitcompanion.data.local.entity.ExamRegistrationsEntity
import com.jainhardik120.jiitcompanion.data.local.entity.ExamScheduleEntity
import com.jainhardik120.jiitcompanion.data.local.entity.StudentAttendanceEntity
import com.jainhardik120.jiitcompanion.data.local.entity.StudentAttendanceRegistrationEntity
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity

@Database(entities = [
    UserEntity::class,
    StudentAttendanceEntity::class,
    StudentAttendanceRegistrationEntity::class,
    ExamEventsEntity::class,
    ExamRegistrationsEntity::class,
    ExamScheduleEntity::class], version = 5, exportSchema = false)
abstract class PortalDatabase : RoomDatabase(){
    abstract val dao : PortalDao
}