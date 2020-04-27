package com.mob.cameraxxx

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.io.File


class ImageActivity : AppCompatActivity() {

   private  var imgView: ImageView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        imgView=findViewById(R.id.imageview) as ImageView

        val intent = getIntent();
        val url=intent.getStringExtra("img") as String

        val imgFile = File(url)
        if (imgFile.exists()){
            val bitmap=BitmapFactory.decodeFile(imgFile.absolutePath)
            imgView!!.setImageBitmap(bitmap)
        }
    }
}
