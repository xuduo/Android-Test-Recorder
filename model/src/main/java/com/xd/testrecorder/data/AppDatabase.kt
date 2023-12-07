package com.xd.testrecorder.data

import android.graphics.Rect
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.Types
import com.xd.testrecorder.dao.ActionDao
import com.xd.testrecorder.dao.ActionImageDao
import com.xd.testrecorder.dao.RecordingDao

@Database(entities = [Recording::class, Action::class, ActionImage::class], version = 1)
@TypeConverters(CordsConverter::class, RectConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recording(): RecordingDao
    abstract fun action(): ActionDao
    abstract fun actionImage(): ActionImageDao
}

class PairAdapter {
    @ToJson
    fun toJson(pair: Pair<Int, Int>): String {
        return "${pair.first},${pair.second}"
    }

    @FromJson
    fun fromJson(json: String): Pair<Int, Int> {
        val parts = json.split(",")
        if (parts.size != 2) throw JsonDataException("Invalid json for Pair")
        return Pair(parts[0].toInt(), parts[1].toInt())
    }
}

class RectConverter {

    @TypeConverter
    fun fromRect(rect: Rect?): String {
        return rect?.let { "${it.left},${it.top},${it.right},${it.bottom}" } ?: ""
    }

    @TypeConverter
    fun toRect(data: String): Rect? {
        if (data.isEmpty()) return null

        return data.split(",").let {
            if (it.size == 4) {
                Rect(it[0].toInt(), it[1].toInt(), it[2].toInt(), it[3].toInt())
            } else {
                null
            }
        }
    }
}

class CordsConverter {

    private val moshi = Moshi.Builder()
        .add(PairAdapter())
        .build()
    private val pairType = Types.newParameterizedType(
        Pair::class.java,
        Int::class.javaObjectType,
        Int::class.javaObjectType
    )
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
