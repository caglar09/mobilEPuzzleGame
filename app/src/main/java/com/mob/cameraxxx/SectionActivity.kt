package com.mob.cameraxxx

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mob.cameraxxx.adapters.SectionAdapters
import com.mob.cameraxxx.constant.Constants
import com.mob.cameraxxx.data.Section
import com.mob.cameraxxx.service.DataAdapterService


private const val REQUEST_CODE_PERMISSIONS = 10
private val REQUIRED_PERMISSIONS = emptyArray<String>()

class SectionActivity() : AppCompatActivity() {
    var sections = ArrayList<Section>()
    var mAuthUser = FirebaseAuth.getInstance()
    var databaseRef = FirebaseDatabase.getInstance().getReference("users").child(mAuthUser.uid!!)

    private var btn_CameraRedirect: ImageButton? = null
    private var btn_backButton: ImageButton? = null
    private lateinit var btn_camera_redirect_layout: RelativeLayout
    private lateinit var lst_RecyImagelist: RecyclerView
    private var _isEditable: Boolean = false
    private lateinit var  loading:ProgressBar
    lateinit var dataAdapterService: DataAdapterService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_section)

        var isEditable: Boolean = intent.getBooleanExtra(Constants.IS_EDITTABLE_KEY, false)
        _isEditable = isEditable

        btn_camera_redirect_layout = findViewById<RelativeLayout>(R.id.btn_camera_redirect_layout)
        btn_CameraRedirect = findViewById<ImageButton>(R.id.btn_camera_redirect)
        btn_backButton = findViewById<ImageButton>(R.id.btn_back)
        lst_RecyImagelist = findViewById<RecyclerView>(R.id.grd_RecyclerImageList)
        loading=findViewById(R.id.loading)
        dataAdapterService = DataAdapterService(this)

        btn_CameraRedirect!!.setOnClickListener { v ->
            startActivity(Intent(this@SectionActivity, CameraActivity::class.java))
        }
        btn_backButton!!.setOnClickListener { v ->
            onBackPressed()
        }
        if (!_isEditable) {
            btn_CameraRedirect!!.visibility = View.GONE
            btn_camera_redirect_layout.visibility = View.GONE
        }
        loading.visibility=View.VISIBLE
        databaseRef.child("sections").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(_sectionRefs: DataSnapshot) {
                var _dbSections= ArrayList<Section>()
                for (sction:DataSnapshot in _sectionRefs.children){
                    var s=sction.getValue(Section::class.java)
                    _dbSections.add(s!!)
                }
                sections=_dbSections
                loading.visibility=View.GONE
                setData()
            }

            override fun onCancelled(p0: DatabaseError) {
                loading.visibility=View.GONE
                var dialog=AlertDialog.Builder(this@SectionActivity).setTitle("Error").setMessage(p0.message).setPositiveButton("Ok", DialogInterface.OnClickListener(){
                    dialog, which ->
                    dialog.dismiss()
                })
                dialog.show()
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    @SuppressLint("ResourceType")
    fun setData() {

        var gridLayoutManager = GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false)
        lst_RecyImagelist.layoutManager = gridLayoutManager

        lst_RecyImagelist.adapter = SectionAdapters(this, sections, dataAdapterService, _isEditable)
    }

}
