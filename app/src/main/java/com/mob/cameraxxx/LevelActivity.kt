package com.mob.cameraxxx

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_single_image.view.*
import java.io.File
private const val REQUEST_CODE_PERMISSIONS = 10
// This is an array of all the permission specified in the manifest.
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)

class LevelActivity : AppCompatActivity() {
    var imageList=ArrayList<Image>()
  private lateinit var lst_Imagelist:GridView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_level)
        lst_Imagelist=findViewById(R.id.grd_ListView)
        if (allPermissionsGranted()){
          imageList= getFiles()
            lst_Imagelist.adapter=ImageViewAdapter(this,imageList)
        }else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }

    fun getFiles(): ArrayList<Image> {
        var localimageList=ArrayList<Image>()
        var currentImage:Image
        var downloadsFolder: File =externalMediaDirs.first()
        var listFiles=downloadsFolder.listFiles()
        for (file in listFiles)
        {
            currentImage=Image(file.absolutePath,file.length())
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

    /**
     * Check if all permission specified in the manifest have been granted
     */
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }
}

class ImageViewAdapter:BaseAdapter{
    var _images=ArrayList<Image>()
    var _context:Context?=null
    constructor(context:Context,listOfImage:ArrayList<Image>):super(){
        _images=listOfImage
        _context=context
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
       var image=_images[position]
        var inflator=_context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var listItemView= inflator.inflate(R.layout.activity_single_image,null)

        val imgFile = File(image.path)
        if (imgFile.exists()){
            val bitmap= BitmapFactory.decodeFile(imgFile.path)
            listItemView.img!!.setImageBitmap(bitmap)
        }
        return  listItemView
    }

    override fun getItem(position: Int): Any {
        return  _images[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
       return _images.size
    }

}