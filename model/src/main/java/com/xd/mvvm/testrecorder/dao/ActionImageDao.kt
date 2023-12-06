package com.xd.mvvm.testrecorder.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.xd.mvvm.testrecorder.data.ActionImage

@Dao
interface ActionImageDao {

    // Insert a new ActionImage
    @Insert
    fun insertActionImage(actionImage: ActionImage): Long

    // Update an existing ActionImage
    @Update
    fun updateActionImage(actionImage: ActionImage)

    // Delete an ActionImage
    @Delete
    fun deleteActionImage(actionImage: ActionImage)

    // Query to retrieve an ActionImage by its actionId
    @Query("SELECT * FROM action_images WHERE actionId = :actionId")
    fun getActionImageByActionId(actionId: Long): LiveData<ActionImage>

    // Query to retrieve all ActionImages
    @Query("SELECT * FROM action_images")
    fun getAllActionImages(): List<ActionImage>
}
