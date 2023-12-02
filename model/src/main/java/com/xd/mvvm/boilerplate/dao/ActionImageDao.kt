package com.xd.mvvm.boilerplate.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.xd.mvvm.boilerplate.data.ActionImage

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
    fun getActionImageByActionId(actionId: Long): ActionImage?

    // Query to retrieve all ActionImages
    @Query("SELECT * FROM action_images")
    fun getAllActionImages(): List<ActionImage>
}
