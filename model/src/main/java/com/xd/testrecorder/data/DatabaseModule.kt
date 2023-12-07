package com.xd.testrecorder.data

import android.content.Context
import androidx.room.Room
import com.xd.common.logger.Logger
import com.xd.testrecorder.dao.ActionDao
import com.xd.testrecorder.dao.ActionImageDao
import com.xd.testrecorder.dao.RecordingDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    val logger = Logger("DatabaseModule")

    init {
        logger.i("DatabaseModule init")
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
        val timestamp = System.currentTimeMillis()
        val module = Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "app_db"
        ).build()
        logger.i("provideDatabase cost ${System.currentTimeMillis() - timestamp}")
        return module
    }

    @Provides
    fun provideRecording(database: AppDatabase): RecordingDao {
        return database.recording()
    }

    @Provides
    fun provideAction(database: AppDatabase): ActionDao {
        return database.action()
    }

    @Provides
    fun provideActionImage(database: AppDatabase): ActionImageDao {
        return database.actionImage()
    }
}
