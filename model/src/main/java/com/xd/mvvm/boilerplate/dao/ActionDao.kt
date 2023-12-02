package com.xd.mvvm.boilerplate.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.xd.mvvm.boilerplate.data.Action
import com.xd.mvvm.boilerplate.data.Recording

@Dao
interface ActionDao {

    // Insert a new recording
    @Insert
    suspend fun insertAction(action: Action): Long

    // Update an existing recording
    @Update
    suspend fun updateAction(action: Action)

    // Delete a recording
    @Delete
    suspend fun deleteAction(recording: Action)

    // Query all recordings
    @Query("SELECT * FROM actions order by id desc")
    fun getAllActions(): LiveData<List<Action>>

    @Query("SELECT * FROM actions WHERE recordingId = :recordingId order by id asc")
    fun getActionsByRecordingId(recordingId: Long): LiveData<List<Action>>

    // Query a single recording by ID
    @Query("SELECT * FROM actions WHERE id = :id")
    suspend fun getActionById(id: Long): Action?

}
