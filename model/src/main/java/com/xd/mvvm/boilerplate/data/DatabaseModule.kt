package com.xd.mvvm.boilerplate.data;

import android.content.Context
import androidx.room.Room
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
            "my_database_name"
        ).build()
    }

    @Provides
    fun provideYourDao(database: AppDatabase): RecordingDao {
        return database.recording()
    }
}
