package com.xd.mvvm.testrecorder.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    var updateTime: Long = createTime,

    @ColumnInfo(name = "icon", typeAffinity = ColumnInfo.BLOB)
    var icon: ByteArray? = null

) {
    fun getIconBitmap(): ImageBitmap? {
        return icon?.let { BitmapFactory.decodeByteArray(it, 0, it.size).asImageBitmap() }
    }

    fun getFormattedCreateTime(): String {
        val formatter = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
        return formatter.format(Date(createTime))
    }

    fun setIcon(
        bitmap: Bitmap,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
        quality: Int = 100
    ) {
        val stream = ByteArrayOutputStream()
        bitmap.compress(format, quality, stream)
        this.icon = stream.toByteArray()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Recording

        if (id != other.id) return false
        if (name != other.name) return false
        if (packageName != other.packageName) return false
        if (createTime != other.createTime) return false
        if (updateTime != other.updateTime) return false
        if (icon != null) {
            if (other.icon == null) return false
            if (!icon.contentEquals(other.icon)) return false
        } else if (other.icon != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (packageName?.hashCode() ?: 0)
        result = 31 * result + createTime.hashCode()
        result = 31 * result + updateTime.hashCode()
        result = 31 * result + (icon?.contentHashCode() ?: 0)
        return result
    }
}
