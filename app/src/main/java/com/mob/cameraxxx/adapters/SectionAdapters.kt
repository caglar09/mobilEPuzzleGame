package com.mob.cameraxxx.adapters

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Size
import android.util.TypedValue
import android.view.*
import android.webkit.WebSettings
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.mob.cameraxxx.ImageActivity
import com.mob.cameraxxx.adapters.SectionAdapters
import com.mob.cameraxxx.R
import com.mob.cameraxxx.data.Image
import com.mob.cameraxxx.data.Section
import com.mob.cameraxxx.helpers.ImageHelper
import java.io.File
import java.util.function.IntToDoubleFunction

class SectionAdapters : RecyclerView.Adapter<SectionAdapters.ViewHolder>, View.OnCreateContextMenuListener {
    var _sections = arrayListOf<Section>()
    var _context: Context? = null

    constructor(context: Context, sectionList: ArrayList<Section>) {
        _sections = sectionList
        _context = context
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img = itemView.findViewById<Button>(R.id.btn_Section_click)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        var view = LayoutInflater.from(_context).inflate(R.layout.activity_single_section, parent, false)
        view!!.setOnCreateContextMenuListener(this@SectionAdapters)
        view!!.setOnClickListener { v ->
            //var img = v.findViewById<ImageView>(R.id.img)
            var img = v.findViewById<Button>(R.id.btn_Section_click)
            img.setOnTouchListener(View.OnTouchListener { t, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                       Toast.makeText(_context,"Test",Toast.LENGTH_SHORT).show()
                        true
                    }
                    else ->
                        false
                }
            })
            img.setOnClickListener { v ->
                Toast.makeText(_context,"Test",Toast.LENGTH_SHORT).show()
            }
        }
        /*view!!.setOnClickListener { v ->
            //var img = v.findViewById<ImageView>(R.id.img)
            var img = v.findViewById<Button>(R.id.btn_Section)
             img.setOnTouchListener(w.OnTouchListener { t, event ->
                 when (event.action) {
                     MotionEvent.ACTION_DOWN -> {
                         var imageId = t.getTag(R.string.image_Tag).toString()
                         val intent = Intent(_context!!, ImageActivity::class.java)
                         intent.putExtra("imgSource", imageId)
                         _context!!.startActivity(intent)
                         true
                     }
                     else ->
                         false
                 }
             })
              img.setOnClickListener { t ->
                var filePath = t.getTag(R.string.image_Tag).toString()
                val intent = Intent(_context!!, ImageActivity::ass.java)
                intent.putExtra("imgSource", filePath)
                _context!!.startActivity(intent)
            }
         }*/
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return _sections.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var section = _sections[position]
        val bitmap = ImageHelper.Base64ToBitmap(section.image)
        if (bitmap != null) {
            //holder?.img.setImageBitmap(bitmap)
            holder?.img.text = (position + 1).toString()
            var size = 22
            holder?.img.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size.toFloat())
            holder?.img.setTag(R.string.image_Tag, section.id)
        }

    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
//var img=v!!.findViewById<ImageView>(R.id.img)
        var img = v!!.findViewById<Button>(R.id.btn_Section_click)
        var id = img.getTag(R.string.image_Tag).toString().toInt()
        menu!!.setHeaderTitle("İşlemler")
        menu!!.setHeaderIcon(R.drawable.ic_android)
        menu!!.add(0, id, 0, "Sil")
    }

}