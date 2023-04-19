package com.jainhardik120.jiitcompanion.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UserEntity::class], version = 1)
abstract class PortalDatabase : RoomDatabase(){
    abstract val dao : PortalDao
}