package com.mob.cameraxxx.adapters

import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mob.cameraxxx.GameActivity
import com.mob.cameraxxx.R
import com.mob.cameraxxx.constant.Constants
import com.mob.cameraxxx.data.Section
import com.mob.cameraxxx.helpers.ImageHelper
import com.mob.cameraxxx.service.DataAdapterService

class SectionAdapters : RecyclerView.Adapter<SectionAdapters.ViewHolder>, View.OnClickListener {
    var mAuth=FirebaseAuth.getInstance()
    var dbref=FirebaseDatabase.getInstance().getReference("users").child(mAuth!!.uid!!.toString()).child("sections")
    var _sections = arrayListOf<Section>()
    var _context: Context? = null
    var _isEditable: Boolean = false
    private var isSelectedButton: ViewHolder? = null
    private lateinit var _dataAdapterService: DataAdapterService

    constructor(context: Context, sectionList: ArrayList<Section>, dataAdapterService: DataAdapterService, isEditable: Boolean) {
        _sections = sectionList
        _context = context
        _isEditable = isEditable
        _dataAdapterService = dataAdapterService
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img = itemView.findViewById<Button>(R.id.btn_Section_click)
        val btn_trash_section = itemView.findViewById<ImageButton>(R.id.btn_trash_single_section)
        var btn_complated_section = itemView.findViewById<ImageButton>(R.id.btn_complated_single_section2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(_context).inflate(R.layout.activity_single_section, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return _sections.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var section = _sections[position]
        val bitmap = ImageHelper.Base64ToBitmap(section.image)
       if (holder?.btn_trash_section.isVisible)
           holder?.btn_trash_section.visibility = View.GONE
        if (bitmap != null) {
            //holder?.img.setImageBitmap(bitmap)
            holder?.img.text = (position + 1).toString()
            var size = 22
            holder?.img.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size.toFloat())
            holder?.img.setTag(R.string.image_Tag, section.id)

            if (_isEditable) {
                holder?.img.setOnLongClickListener {
                    if (isSelectedButton !== null && isSelectedButton!!.position !== holder.position) {
                        isSelectedButton!!.btn_trash_section.visibility = View.GONE
                    }
                    holder?.btn_trash_section.visibility = View.VISIBLE
                    holder?.btn_trash_section.animation = AnimationUtils.loadAnimation(_context, R.anim.shake_anim)
                    isSelectedButton = holder

                    true
                }
                holder?.btn_complated_section.visibility = View.GONE
                holder?.img.setOnFocusChangeListener { v: View?, hasFocus: Boolean ->
                    if (!hasFocus) {
                        v!!.findViewById<ImageButton>(R.id.btn_trash_single_section).visibility = View.GONE
                    }

                }
            } else {
                holder?.img.setOnClickListener(this)
                holder?.btn_trash_section.visibility = View.GONE
                if (section.completed)
                    holder?.btn_complated_section.visibility = View.VISIBLE
                else
                    holder?.btn_complated_section.visibility = View.GONE
            }



            holder?.btn_trash_section.setOnClickListener {
                dbref.child(section.id).removeValue()
               /* var result = _dataAdapterService.deleteSection(section.id).
                if (result) {
                    _sections.remove(section)
                    this.notifyItemRemoved(position)}

                else
                    Toast.makeText(_context, "Silme işlemi başarısız", Toast.LENGTH_LONG).show()*/
                //result
            }
        }


    }

    override fun onClick(v: View?) {
        var _img = v!!.findViewById<Button>(R.id.btn_Section_click)
        var tag = _img.getTag(R.string.image_Tag) as String
        var intent = Intent(_context, GameActivity::class.java)
        intent.putExtra(Constants.SECTION_ID, tag!!.toString())
        _context!!.startActivity(intent)
    }


}