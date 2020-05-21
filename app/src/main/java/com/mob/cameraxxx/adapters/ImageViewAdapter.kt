package com.mob.cameraxxx.adapters

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.mob.cameraxxx.ImageActivity
import com.mob.cameraxxx.adapters.ImageViewAdapter
import com.mob.cameraxxx.R
import com.mob.cameraxxx.data.Image
import java.io.File

class ImageViewAdapter : RecyclerView.Adapter<ImageViewAdapter.ViewHolder> {
    var _imageList = arrayListOf<Image>()
    var _context: Context? = null

    constructor(context: Context, imagelist: ArrayList<Image>) {
        _imageList = imagelist
        _context = context
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img = itemView.findViewById<ImageView>(R.id.img)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        var view = LayoutInflater.from(parent?.context).inflate(R.layout.activity_single_image, parent, false)
        view!!.setOnClickListener { v ->
            var img = v.findViewById<ImageView>(R.id.img)
           /* img.setOnClickListener { t ->
                var filePath = t.getTag(R.string.image_Tag).toString()
                val intent = Intent(_context!!, ImageActivity::class.java)
                intent.putExtra("imgSource", filePath)
                _context!!.startActivity(intent)
            }*/

            img.setOnTouchListener(View.OnTouchListener { t, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        var filePath = t.getTag(R.string.image_Tag).toString()
                        val intent = Intent(_context!!, ImageActivity::class.java)
                        intent.putExtra("imgSource", filePath)
                        _context!!.startActivity(intent)
                        true
                    }
                    else ->
                        false
                }
            })
        }
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return _imageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imgFile = File(_imageList[position].path)
        if (imgFile.exists()) {
            val bitmap = BitmapFactory.decodeFile(imgFile.path)
            holder?.img.setImageBitmap(bitmap)
            holder?.img.setTag(R.string.image_Tag, imgFile.absolutePath)
        }
    }


}