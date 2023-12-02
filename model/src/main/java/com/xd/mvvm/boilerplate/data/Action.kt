package com.xd.mvvm.boilerplate.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(tableName = "actions",
    foreignKeys = [
        ForeignKey(entity = Recording::class,
            parentColumns = ["id"],
            childColumns = ["recordingId"],
            onDelete = ForeignKey.CASCADE)
    ])

data class Action(
    @PrimaryKey(autoGenerate = true)
    val actionId: Long = 0,

    val recordingId: Long,

    val type: String,

    val cords: List<Int>, // This needs to be converted to a storable format

    val duration: Int,

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val binaryData: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Action

        if (actionId != other.actionId) return false
        if (recordingId != other.recordingId) return false
        if (type != other.type) return false
        if (cords != other.cords) return false
        if (duration != other.duration) return false
        return binaryData.contentEquals(other.binaryData)
    }

    override fun hashCode(): Int {
        var result = actionId.hashCode()
        result = 31 * result + recordingId.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + cords.hashCode()
        result = 31 * result + duration
        result = 31 * result + binaryData.contentHashCode()
        return result
    }
}

//class Converters {
//    @TypeConverter
//    fun fromIntList(value: List<Int>): String = Gson().toJson(value)
//
//    @TypeConverter
//    fun toIntList(value: String): List<Int> = Gson().fromJson(value, object : TypeToken<List<Int>>() {}.type)
//}