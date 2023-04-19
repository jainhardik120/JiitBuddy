package com.jainhardik120.jiitcompanion.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jainhardik120.jiitcompanion.data.local.entity.StudentAttendanceEntity
import com.jainhardik120.jiitcompanion.data.local.entity.StudentAttendanceRegistrationEntity
import com.jainhardik120.jiitcompanion.data.local.entity.UserEntity

@Database(entities = [UserEntity::class, StudentAttendanceEntity::class, StudentAttendanceRegistrationEntity::class], version = 1)
abstract class PortalDatabase : RoomDatabase(){
    abstract val dao : PortalDao
}