package com.xd.mvvm.boilerplate.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "recordings")
data class Recording(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    @ColumnInfo(name = "name")
    var name: String? = "Unnamed Recording",

    @ColumnInfo(name = "package_name")
    var packageName: String? = "Unnamed PackageName",

    @ColumnInfo(name = "create_time")
    var createTime: Long = System.currentTimeMillis(), // Set createTime when the object is created

    @ColumnInfo(name = "update_time")
    var updateTime: Long = createTime
)
