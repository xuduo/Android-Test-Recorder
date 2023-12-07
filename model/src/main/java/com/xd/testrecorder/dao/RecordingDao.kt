package com.xd.testrecorder.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.xd.testrecorder.data.Recording
import com.xd.testrecorder.data.RecordingWithActionCount

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

    @Query("SELECT recordings.*, (SELECT COUNT(*) FROM actions WHERE recordingId = recordings.id) AS actionCount FROM recordings order by id desc")
    fun getRecordingsWithActionCount(): LiveData<List<RecordingWithActionCount>>

    // Query a single recording by ID
    @Query("SELECT * FROM recordings WHERE id = :id")
    suspend fun getRecordingById(id: Long): Recording?

    // Query recordings by name
    @Query("SELECT * FROM recordings WHERE name LIKE :name")
    suspend fun findRecordingsByName(name: String): List<Recording>
}
