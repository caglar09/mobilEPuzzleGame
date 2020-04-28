package com.mob.cameraxxx

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_level.*
import kotlinx.android.synthetic.main.activity_single_image.view.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class Bitmap_Generator_Activity : AppCompatActivity() {
    val filepath: String = "storage/emulated/0/Android/media/com.mob.cameraxxx/1587985731842s.jpg"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bitmap__generator_)
        var grd_BitmapListView = findViewById(R.id.grd_BitmapListView) as GridView
        var orjImg = findViewById(R.id.img_Orji) as ImageView
        var img = File(filepath)
        if (img.exists()) {
            var bmp = BitmapFactory.decodeFile(img.absolutePath)
            //var bmp = BitmapFactory.decodeResource(resources,R.drawable.space2)
            var bitmaps = splitBitmap(bmp, 4, 4)
            var adapters = BitmapAdapters(this, bitmaps)
            grd_BitmapListView.adapter = adapters
            orjImg.setImageBitmap(bmp)
        }
    }


    fun splitBitmap(
        bitmap: Bitmap,
        xCount: Int,
        yCount: Int
    ): ArrayList<Bitmap> {
        // Allocate a two dimensional array to hold the individual images.
        val bitmaps = ArrayList<Bitmap>()
        val width: Int
        val height: Int
        // Divide the original bitmap width by the desired vertical column count
        width = bitmap.width / xCount
        // Divide the original bitmap height by the desired horizontal row count
        height = bitmap.height / yCount
        // Loop the array and create bitmaps for each coordinate
        for (x in 0 until xCount) {
            for (y in 0 until yCount) {
                var bmp = Bitmap.createBitmap(bitmap, x * width, y * height, width, height)
                bitmaps.add(bmp)
            }
        }
        // Return the array
        return bitmaps
    }

    class BitmapAdapters : BaseAdapter {
        var _bMpas: List<Bitmap>
        var _context: Context

        constructor(ctx: Context, bMaps: ArrayList<Bitmap>) {
            _bMpas = bMaps.shuffled()
            _context = ctx
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var item = _bMpas!![position]
            var inflator =
                _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var view = inflator.inflate(R.layout.bitmap_partial, null)
            view.img.setImageBitmap(item)

            return view
        }

        override fun getItem(position: Int): Any {
            return _bMpas!![position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return _bMpas!!.size
        }

    }
}
