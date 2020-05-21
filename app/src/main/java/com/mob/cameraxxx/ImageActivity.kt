package com.mob.cameraxxx

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mob.cameraxxx.helpers.ImageHelper
import java.io.File


class ImageActivity : AppCompatActivity() {

    private var imgView: ImageView? = null
    private var btn_rotate_image: FloatingActionButton? = null

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        imgView = findViewById(R.id.imageview) as ImageView
        btn_rotate_image = findViewById(R.id.btn_rotate_to_right) as FloatingActionButton

        val intent = getIntent();
        val imagePath = intent.getStringExtra("imgSource") as String
        val imgFile = File(imagePath)
        if (imgFile.exists()) {
            val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)

            var rotatedIMage = ImageHelper.rotateBitmap(bitmap, ExifInterface.ORIENTATION_ROTATE_90)
            imgView!!.setImageBitmap(rotatedIMage)

            btn_rotate_image!!.setOnClickListener { v ->
                var drawable: BitmapDrawable = imgView!!.drawable as BitmapDrawable
                var _btmp = drawable.bitmap
                var rotatedRightIMage = ImageHelper.rotateBitmap(_btmp, ExifInterface.ORIENTATION_ROTATE_90)
                imgView!!.setImageBitmap(rotatedRightIMage)
            }
        } else {
            btn_rotate_image!!.visibility = View.INVISIBLE
        }
        //var decodedString = Base64.decode(base64Image, Base64.DEFAULT)
        //var decodedByte: Bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        //imgView!!.setImageBitmap(decodedByte)
        //imgView!!.setImageBitmap(img)


    }
}
