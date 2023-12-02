package com.xd.mvvm.boilerplate.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.xd.mvvm.boilerplate.dao.ActionDao
import com.xd.mvvm.boilerplate.dao.ActionImageDao
import com.xd.mvvm.boilerplate.dao.RecordingDao
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Database(entities = [Recording::class, Action::class, ActionImage::class], version = 1)
@TypeConverters(CordsConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recording(): RecordingDao
    abstract fun action(): ActionDao
    abstract fun actionImage(): ActionImageDao
}

class CordsConverter {

    private val moshi = Moshi.Builder().build()
    private val pairType = Types.newParameterizedType(Pair::class.java, Int::class.javaObjectType, Int::class.javaObjectType)
    private val listType = Types.newParameterizedType(List::class.java, pairType)
    private val jsonAdapter = moshi.adapter<List<Pair<Int, Int>>>(listType)

    @TypeConverter
    fun fromCordsJson(json: String?): List<Pair<Int, Int>>? {
        return if (json == null) null else jsonAdapter.fromJson(json)
    }

    @TypeConverter
    fun cordsToJson(cords: List<Pair<Int, Int>>?): String? {
        return if (cords == null) null else jsonAdapter.toJson(cords)
    }
}
