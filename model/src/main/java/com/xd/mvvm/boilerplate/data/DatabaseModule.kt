package com.xd.mvvm.boilerplate.data;

import android.content.Context
import androidx.room.Room
import com.xd.mvvm.boilerplate.dao.ActionDao
import com.xd.mvvm.boilerplate.dao.ActionImageDao
import com.xd.mvvm.boilerplate.dao.RecordingDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "app_db"
        ).build()
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
