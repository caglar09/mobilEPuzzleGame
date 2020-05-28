package com.mob.cameraxxx

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mob.cameraxxx.adapters.SectionAdapters
import com.mob.cameraxxx.constant.Constants
import com.mob.cameraxxx.data.Image
import com.mob.cameraxxx.data.Section
import com.mob.cameraxxx.service.DataAdapterService
import java.io.File


private const val REQUEST_CODE_PERMISSIONS = 10

// This is an array of all the permission specified in the manifest.
private val REQUIRED_PERMISSIONS = emptyArray<String>()

//Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
class SectionActivity() : AppCompatActivity() {
    var imageList = ArrayList<Image>()
    var sections = ArrayList<Section>()
    private var btn_CameraRedirect: ImageButton? = null
    private var btn_backButton: ImageButton? = null
    private lateinit var btn_camera_redirect_layout: RelativeLayout
    private lateinit var lst_RecyImagelist: RecyclerView
    private var _isEditable: Boolean = false
    lateinit var dataAdapterService: DataAdapterService
    private lateinit var _sectionAdapter: SectionAdapters


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_section)
        /*setSupportActionBar(findViewById(R.id.levelActicityToolbar))
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle("Bölümler")*/
        var isEditable: Boolean = intent.getBooleanExtra(Constants.IS_EDITTABLE_KEY, false)
        _isEditable = isEditable

        btn_camera_redirect_layout = findViewById<RelativeLayout>(R.id.btn_camera_redirect_layout)
        btn_CameraRedirect = findViewById<ImageButton>(R.id.btn_camera_redirect)
        btn_backButton = findViewById<ImageButton>(R.id.btn_back)
        lst_RecyImagelist = findViewById<RecyclerView>(R.id.grd_RecyclerImageList)
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
        if (allPermissionsGranted()) {
            sections = dataAdapterService.getSections()
            setData()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var inflator = menuInflater
        inflator.inflate(R.menu.level_actionbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    @SuppressLint("ResourceType")
    fun setData() {

        var gridLayoutManager = GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false)
        lst_RecyImagelist.layoutManager = gridLayoutManager

        lst_RecyImagelist.addItemDecoration(ItemDecoration(4, 30, true))
        //lst_RecyImagelist.adapter = ImageViewAdapter(this, imageList)
        lst_RecyImagelist.adapter = SectionAdapters(this, sections, dataAdapterService, _isEditable)
        registerForContextMenu(lst_RecyImagelist)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                setData()
            } else {
                Toast.makeText(this,
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
                baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    fun getFiles(): ArrayList<Image> {
        var localimageList = ArrayList<Image>()
        var currentImage: Image
        var downloadsFolder: File = externalMediaDirs.first()
        var listFiles = downloadsFolder.listFiles()
        for (file in listFiles) {
            currentImage = Image(file.absolutePath, file.length())
            localimageList.add(currentImage)
        }
        return localimageList
    }


}

class ItemDecoration : RecyclerView.ItemDecoration {
    var spanCount: Int
    var spacing: Int
    var includeEdge: Boolean

    constructor(_spanCount: Int, _spacing: Int, _includeEdge: Boolean) {
        spanCount = _spanCount
        spacing = _spacing
        includeEdge = _includeEdge

    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        var position = parent.getChildAdapterPosition(view); // item position
        var column = position % spanCount; // item column

        if (includeEdge) {
            outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

            if (position < spanCount) { // top edge
                outRect.top = spacing;
            }
            outRect.bottom = spacing; // item bottom
        } else {
            outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
            outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            if (position >= spanCount) {
                outRect.top = spacing; // item top
            }
        }
    }
}

/*

class ImageViewAdapter : BaseAdapter {
    var _images = ArrayList<Image>()
    var _context: Context? = null

    constructor(context: Context, listOfImage: ArrayList<Image>) : super() {
        _images = listOfImage
        _context = context
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var image = _images[position]
        var inflator = _context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var listItemView = inflator.inflate(R.layout.activity_single_image, null)

        val imgFile = File(image.path)
        if (imgFile.exists()) {
            val bitmap = BitmapFactory.decodeFile(imgFile.path)
            listItemView.img!!.setImageBitmap(bitmap)
        }
        return listItemView
    }

    override fun getItem(position: Int): Any {
        return _images[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return _images.size
    }

}
*/