package com.mob.cameraxxx.helpers

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Base64
import androidx.camera.core.ImageProxy
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayOutputStream

class ImageHelper {
    companion object {
        fun decodeBitmap(image: ImageProxy): Bitmap? {
            val buffer = image.planes[0].buffer
            val bytes = ByteArray(buffer.capacity()).also { buffer.get(it) }
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }

        fun BitMapToBase64(bitmap: Bitmap): String {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val b = baos.toByteArray()
            return Base64.encodeToString(b, Base64.DEFAULT)
        }
        fun Base64ToBitmap(base64String:String):Bitmap?{
            try {
                var decodedString = Base64.decode(base64String, Base64.DEFAULT)
                return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            }catch (ex:Exception){
                return null
            }

        }

        fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap? {
            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_NORMAL -> return bitmap
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1f, 1f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                    matrix.setRotate(180f)
                    matrix.postScale(-1f, 1f)
                }
                ExifInterface.ORIENTATION_TRANSPOSE -> {
                    matrix.setRotate(90f)
                    matrix.postScale(-1f, 1f)
                }
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
                ExifInterface.ORIENTATION_TRANSVERSE -> {
                    matrix.setRotate(-90f)
                    matrix.postScale(-1f, 1f)
                }
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(-90f)
                else -> return bitmap
            }
            return try {
                val bmRotated: Bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true)
                bitmap.recycle()
                bmRotated
            } catch (e: OutOfMemoryError) {
                e.printStackTrace()
                null
            }
        }
    }
}