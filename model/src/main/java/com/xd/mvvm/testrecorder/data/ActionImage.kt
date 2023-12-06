package com.xd.mvvm.testrecorder.data

import android.graphics.Bitmap
import android.media.Image
import android.util.Log
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

@Entity(
    tableName = "action_images",
    foreignKeys = [
        ForeignKey(
            entity = Action::class,
            parentColumns = ["id"],
            childColumns = ["actionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ActionImage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val actionId: Long,

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val screenShot: ByteArray
)

fun convertImageToByteArray(image: Image, cropTop: Int = 0, cropHeight: Int = 0): ByteArray {
    Log.d("convertImageToByteArray", "convertImageToByteArray ${image.width} ${image.height}")
    val originalBitmap = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888)
    originalBitmap.copyPixelsFromBuffer(image.planes[0].buffer)

    // Create a new bitmap with the top n pixels cropped
    val croppedBitmap = Bitmap.createBitmap(
        originalBitmap,
        0, cropTop,  // x and y coordinates of the first pixel
        originalBitmap.width,
        if (cropHeight == 0) {
            image.height - cropTop// new width and height
        } else {
            cropHeight
        }
    )

    val stream = ByteArrayOutputStream()
    croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    val byteArray = stream.toByteArray()

    // Recycle the bitmaps to free up memory
    originalBitmap.recycle()
    croppedBitmap.recycle()
    image.close()
    stream.close()

    return byteArray
}

private fun getBitmapFromRGBA(bytes: ByteArray, width: Int, height: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val buffer = ByteBuffer.wrap(bytes)
    bitmap.copyPixelsFromBuffer(buffer)
    return bitmap
}
