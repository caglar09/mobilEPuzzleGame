package com.mob.cameraxxx

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Parcel
import android.os.Parcelable
import android.view.*
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mob.cameraxxx.data.Image
import kotlinx.android.synthetic.main.activity_single_image.view.*
import kotlinx.android.synthetic.main.activity_single_image.view.img
import kotlinx.android.synthetic.main.bitmap_partial.view.*
import java.io.File

private const val REQUEST_CODE_PERMISSIONS = 10

// This is an array of all the permission specified in the manifest.
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

class LevelActivity : AppCompatActivity() {
    var imageList = ArrayList<Image>()
    private lateinit var lst_Imagelist: GridView
    private lateinit var lst_RecyImagelist: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_level)
        setSupportActionBar(findViewById(R.id.levelActicityToolbar))
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        lst_RecyImagelist = findViewById<RecyclerView>(R.id.grd_RecyclerImageList)

        //lst_Imagelist = findViewById(R.id.grd_ListView)
        if (allPermissionsGranted()) {
            var handler = Handler()
            imageList = getFiles()
            var gridLayoutManager = GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false)

            lst_RecyImagelist.layoutManager = gridLayoutManager
            lst_RecyImagelist.adapter = ImageViewRecyleViewAdapter(this, imageList)

        } else {
            ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
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
                        startActivity(Intent(this@LevelActivity, MainActivity::class.java))
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

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {

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
}


class ImageViewRecyleViewAdapter : RecyclerView.Adapter<ImageViewRecyleViewAdapter.ViewHolder> {
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
            img.setOnClickListener { v ->
                Toast.makeText(_context, v.getTag(R.string.image_Tag).toString(), Toast.LENGTH_LONG).show()
            }
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