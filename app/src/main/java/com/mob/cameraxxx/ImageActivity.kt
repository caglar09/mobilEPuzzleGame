package com.mob.cameraxxx

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.mob.cameraxxx.constant.Constants
import com.mob.cameraxxx.data.Section
import com.mob.cameraxxx.helpers.ImageHelper
import com.mob.cameraxxx.service.DataAdapterService
import java.io.File
import java.lang.Exception
import java.util.*


class ImageActivity : AppCompatActivity() {

    private var imgView: ImageView? = null
    private lateinit var takedImage: Bitmap
    private var btn_rotate_image: FloatingActionButton? = null
    private lateinit var btn_saveSection: Button
    private lateinit var btn_cancelSection: Button
    private lateinit var txt_tr: TextInputEditText
    private lateinit var txt_tr_layout: TextInputLayout
    private lateinit var txt_en: TextInputEditText
    private lateinit var txt_en_layout: TextInputLayout
    private lateinit var dataAdapterService: DataAdapterService

    private var formValid = false;

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        dataAdapterService = DataAdapterService(this)
        imgView = findViewById(R.id.imageview) as ImageView
        btn_rotate_image = findViewById(R.id.btn_rotate_to_right) as FloatingActionButton
        btn_saveSection = findViewById(R.id.btn_saveSection)
        btn_cancelSection = findViewById(R.id.btn_cancelSection)
        txt_tr = findViewById(R.id.txt_tr)
        txt_en = findViewById(R.id.txt_en)
        txt_tr_layout = findViewById(R.id.txt_tr_layout)
        txt_en_layout = findViewById(R.id.txt_en_layout)
        try {
            val intent = getIntent();
            val imagePath = intent.getStringExtra("imgSource") as String
            val imgFile = File(imagePath)
            if (imgFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                var rotatedIMage = ImageHelper.rotateBitmap(bitmap, ExifInterface.ORIENTATION_ROTATE_90)
                takedImage = rotatedIMage!!.copy(rotatedIMage!!.config, true)
                imgView!!.setImageBitmap(rotatedIMage)

                btn_rotate_image!!.setOnClickListener { v ->
                    var drawable: BitmapDrawable = imgView!!.drawable as BitmapDrawable
                    var _btmp = drawable.bitmap
                    rotatedIMage = ImageHelper.rotateBitmap(_btmp, ExifInterface.ORIENTATION_ROTATE_90)
                    takedImage = rotatedIMage!!.copy(rotatedIMage!!.config, true)
                    imgView!!.setImageBitmap(rotatedIMage)
                }
                btn_saveSection.setOnClickListener { v ->
                    if (validateInputs() && formValid) {

                        var base64Image = ImageHelper.BitMapToBase64(takedImage)
                        var trText = txt_tr.text.toString()
                        var enText = txt_en.text.toString()
                        var sec = Section(Date().time, 0, base64Image, trText, enText)
                        var result = dataAdapterService.saveSection(sec)
                        if (result)
                        {
                            startActivity(Intent(this, SectionActivity::class.java).putExtra(Constants.IS_EDITTABLE_KEY, true))
                            Toast.makeText(this, "Kayıt işlemi başarılı", Toast.LENGTH_LONG).show()
                            finish()
                        }
                        else
                            Toast.makeText(this, "Hata oluştu", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Lütfen bütün alanları doldurun", Toast.LENGTH_LONG).show()
                    }
                }
                txt_tr.addTextChangedListener(textChangeValidation(txt_tr_layout))
                txt_en.addTextChangedListener(textChangeValidation(txt_en_layout))

            } else {
                btn_rotate_image!!.visibility = View.INVISIBLE
                btn_saveSection.isEnabled = false
            }

        } catch (ex: Exception) {
            onBackPressed()
            finish()
        }
        //var decodedString = Base64.decode(base64Image, Base64.DEFAULT)
        //var decodedByte: Bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        //imgView!!.setImageBitmap(decodedByte)
        //imgView!!.setImageBitmap(img)


    }

    fun textChangeValidation(layout: TextInputLayout) = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            try {
                if (s!!.length > 0) {
                    layout.error = ""
                    formValid = true
                } else {
                    layout.error = "Bu alan boş olamaz."
                    formValid = false
                }
            } catch (ex: Exception) {
                Toast.makeText(this@ImageActivity, ex.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    }

    fun validateInputs(): Boolean {
        var _validForm = formValid
        if (txt_tr.text.toString().isEmpty()) {
            txt_tr_layout.error = "Bu alan boş geçilemez"
            _validForm = false
        }
        if (txt_en.text.toString().isEmpty()) {
            _validForm = false
            txt_en_layout.error = "Bu alan boş geçilemez"
        }
        if (!txt_tr.text.toString().isEmpty() && !txt_en.text.toString().isEmpty())
            _validForm = true

        formValid = _validForm
        return formValid
    }
}
