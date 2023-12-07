package com.xd.testrecorder.data

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class RecordingWithActionCount(
    @Embedded val recording: Recording,
    @ColumnInfo(name = "actionCount") val actionCount: Int
)
