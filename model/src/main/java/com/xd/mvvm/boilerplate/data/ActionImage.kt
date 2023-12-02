package com.xd.mvvm.boilerplate.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import java.io.ByteArrayOutputStream

@Entity(tableName = "action_images",
        foreignKeys = [
            ForeignKey(entity = Action::class,
                       parentColumns = ["id"],
                       childColumns = ["actionId"],
                       onDelete = ForeignKey.CASCADE)
        ])
data class ActionImage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val actionId: Long,

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val screenShot: ByteArray
)

fun convertImageToByteArray(image: Image, statusBarHeight: Int): ByteArray {
    val buffer = image.planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer[bytes]
    val originalBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

    // Check if the crop amount is not greater than the image height
    val cropHeight = if (statusBarHeight < originalBitmap.height) statusBarHeight else 0

    // Create a new bitmap with the top n pixels cropped
    val croppedBitmap = Bitmap.createBitmap(
        originalBitmap,
        0, cropHeight,  // x and y coordinates of the first pixel
        originalBitmap.width, originalBitmap.height - cropHeight // new width and height
    )

    val stream = ByteArrayOutputStream()
    croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
    val byteArray = stream.toByteArray()

    // Recycle the bitmaps to free up memory
    originalBitmap.recycle()
    croppedBitmap.recycle()

    return byteArray
}
