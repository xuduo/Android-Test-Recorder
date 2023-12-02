package com.xd.mvvm.boilerplate.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.xd.mvvm.boilerplate.dao.RecordingDao

@Database(entities = [Recording::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recording(): RecordingDao
}