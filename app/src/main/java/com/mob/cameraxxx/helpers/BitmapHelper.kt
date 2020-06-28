package com.mob.cameraxxx.helpers

import android.graphics.Bitmap
import com.mob.cameraxxx.data.BitmapModel

class BitmapHelper {
    companion object {
        fun splitBitmap(
            bitmap: Bitmap,
            xCount: Int,
            yCount: Int
        ): ArrayList<BitmapModel> {
            val bitmaps = ArrayList<BitmapModel>()
            val width: Int
            val height: Int
            width = bitmap.width / xCount
            height = bitmap.height / yCount

            var index:Int=0
            for (y in 0 until yCount) {
                for (x in 0 until xCount) {
                    var bmp = Bitmap.createBitmap(bitmap, x * width, y * height, width, height)
                    var model=BitmapModel(index,bmp)
                    bitmaps.add(model)
                    index++
                }
            }
            // Return the array
            return bitmaps
        }
    }
}