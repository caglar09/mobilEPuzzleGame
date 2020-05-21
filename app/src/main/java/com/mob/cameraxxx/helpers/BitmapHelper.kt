package com.mob.cameraxxx.helpers

import android.graphics.Bitmap
import android.util.Log
import com.mob.cameraxxx.data.BitmapModel

class BitmapHelper {
    companion object {
        fun splitBitmap(
            bitmap: Bitmap,
            xCount: Int,
            yCount: Int
        ): ArrayList<BitmapModel> {
            // Allocate a two dimensional array to hold the individual images.
            val bitmaps = ArrayList<BitmapModel>()
            val width: Int
            val height: Int
            // Divide the original bitmap width by the desired vertical column count
            width = bitmap.width / xCount
            // Divide the original bitmap height by the desired horizontal row count
            height = bitmap.height / yCount

            // Loop the array and create bitmaps for each coordinate
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