package com.xd.mvvm.testrecorder.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.xd.mvvm.testrecorder.data.Recording

@Dao
interface RecordingDao {

    // Insert a new recording
    @Insert
    suspend fun insertRecording(recording: Recording): Long

    // Update an existing recording
    @Update
    suspend fun updateRecording(recording: Recording)

    // Delete a recording
    @Delete
    suspend fun deleteRecording(recording: Recording)

    // Query all recordings
    @Query("SELECT * FROM recordings order by id desc")
    fun getAllRecordings(): LiveData<List<Recording>>

    // Query a single recording by ID
    @Query("SELECT * FROM recordings WHERE id = :id")
    suspend fun getRecordingById(id: Long): Recording?

    // Query recordings by name
    @Query("SELECT * FROM recordings WHERE name LIKE :name")
    suspend fun findRecordingsByName(name: String): List<Recording>
}
