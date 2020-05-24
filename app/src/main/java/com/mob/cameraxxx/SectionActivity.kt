package com.mob.cameraxxx

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.AttributeSet
import android.util.Xml
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.setMargins
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mob.cameraxxx.adapters.ImageViewAdapter
import com.mob.cameraxxx.adapters.SectionAdapters
import com.mob.cameraxxx.data.Image
import com.mob.cameraxxx.data.Section
import com.mob.cameraxxx.service.DataAdapterService
import kotlinx.android.synthetic.main.activity_section.*
import java.io.File

private const val REQUEST_CODE_PERMISSIONS = 10

// This is an array of all the permission specified in the manifest.
private val REQUIRED_PERMISSIONS = emptyArray<String>()

//Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
class SectionActivity() : AppCompatActivity() {
    var imageList = ArrayList<Image>()
    var sections = ArrayList<Section>()
    private lateinit var lst_Imagelist: GridView
    private lateinit var lst_RecyImagelist: RecyclerView

    lateinit var dataAdapterService: DataAdapterService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_section)

        setSupportActionBar(findViewById(R.id.levelActicityToolbar))
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle("Bölümler")

        lst_RecyImagelist = findViewById<RecyclerView>(R.id.grd_RecyclerImageList)
        dataAdapterService = DataAdapterService(this)

        if (allPermissionsGranted()) {
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var handler = Handler()
        return when (item.itemId) {
            R.id.action_showCamera -> {
                handler.postDelayed(object : Runnable {
                    override fun run() {
                        startActivity(Intent(this@SectionActivity, CameraActivity::class.java))
                    }
                }, 0)
                true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    @SuppressLint("ResourceType")
    fun setData() {
        sections = dataAdapterService.getSections()
        var gridLayoutManager = GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false)
      //  gridLayoutManager.generateDefaultLayoutParams().setMargins(100, 0, 10, 0)
        /*  var parser=resources.getXml(R.style.gridLayoutManagerStyle)
          var attr= Xml.asAttributeSet(parser)

          gridLayoutManager.generateLayoutParams(this@SectionActivity,attr)*/
        lst_RecyImagelist.layoutManager = gridLayoutManager

        lst_RecyImagelist.addItemDecoration(ItemDecoration(4,60,true))
        //lst_RecyImagelist.adapter = ImageViewAdapter(this, imageList)
        lst_RecyImagelist.adapter = SectionAdapters(this, sections)
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

    override fun onContextItemSelected(item: MenuItem): Boolean {
        var sectionId = item.itemId

        var builder = AlertDialog.Builder(this@SectionActivity)
        builder.setTitle("Bu bölümü silmek istediğinize emin misiniz?")
        builder.setPositiveButton("Evet") { dialog, which ->
            var result = dataAdapterService.deleteSection(sectionId = sectionId.toLong())
            if (result) {
                sections = dataAdapterService.getSections()
                Toast.makeText(this@SectionActivity, "Silme işlemi başarılı", Toast.LENGTH_LONG).show()
                grd_RecyclerImageList!!.adapter = SectionAdapters(this@SectionActivity, sections)
            } else
                Toast.makeText(this@SectionActivity, "Silme işlemi başarısız", Toast.LENGTH_LONG).show()
        }
        builder.setNegativeButton("Hayır") { dialog, which ->
            closeContextMenu()
        }
        builder.show()
        return super.onContextItemSelected(item)
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