package com.mob.cameraxxx

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eyalbira.loadingdots.LoadingDots
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mob.cameraxxx.adapters.BitmapRecyclerAdapters
import com.mob.cameraxxx.constant.Constants
import com.mob.cameraxxx.data.BitmapModel
import com.mob.cameraxxx.helpers.BitmapHelper
import com.mob.cameraxxx.helpers.ImageHelper
import com.mob.cameraxxx.service.DataAdapterService
import com.mob.cameraxxx.service.StartDragListener
import kotlinx.android.synthetic.main.activity_game.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

private var REQUEST_CODE_PERMISSIONS: Int = 10
private var REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.INTERNET)

@Suppress("DEPRECATION")
class GameActivity : AppCompatActivity(), StartDragListener {
    //#region variables
    var bitmapList: ArrayList<BitmapModel>? = null
    var orderBitmapList: IntArray? = null
    val row: Int = 3
    val count: Int = 3
    lateinit var imageComponent: ImageView
    lateinit var grd_GameRecylerView: RecyclerView
    lateinit var touchHelper: ItemTouchHelper
    lateinit var _dataAdapterService: DataAdapterService
    lateinit var btn_Help: ImageButton
    lateinit var txtStepCounter: TextView
    lateinit var txt_text_tr: TextView
    lateinit var txt_text_tr_layout: TextView
    lateinit var txt_text_en: TextView
    lateinit var txt_text_en_layout: TextView
    lateinit var textToSpeech: TextToSpeech
    lateinit var btn_textToSpeachTr: ImageButton
    lateinit var btn_textToSpeachEn: ImageButton
    lateinit var btn_speachToTextTr: ImageButton
    lateinit var btn_speachToTextEn: ImageButton
    var stepCount = 0
    lateinit var speechRecognizer: SpeechRecognizer
    lateinit var speechRecognizerIntent: Intent

    //#endregion
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        try {
            txtStepCounter = findViewById(R.id.txt_stepCounter)
            txtStepCounter.text = stepCount.toString()
            txt_text_tr = findViewById(R.id.txt_tr_name)
            txt_text_en = findViewById(R.id.txt_en_name)
            btn_Help = findViewById(R.id.btn_Help)
            btn_textToSpeachTr = findViewById(R.id.img_speachTextTr)
            btn_textToSpeachEn = findViewById(R.id.img_speachTextEn)
            btn_speachToTextTr = findViewById(R.id.img_recognizeTextTr)
            btn_speachToTextEn = findViewById(R.id.img_recognizeTextEn)
            imageComponent = findViewById<ImageView>(R.id.img_Orji)
            grd_GameRecylerView = findViewById<RecyclerView>(R.id.grd_BitmapListView)
            _dataAdapterService = DataAdapterService(this)
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this@GameActivity)
            intializeRecognizeIntent()
            var shakeAnim = AnimationUtils.loadAnimation(this@GameActivity, R.anim.shake_anim)
            btn_Help.animation = shakeAnim

            var width = getDisplayMetrics().widthPixels

            var bmp: Bitmap? = BitmapFactory.decodeResource(resources, R.drawable.naturals)
            var currentSectionId = intent.getLongExtra(Constants.SECTION_ID, 0)

            var section = _dataAdapterService.getSection(currentSectionId.toLong())
            if (section == null) {
                var builder = AlertDialog.Builder(this)
                builder.setMessage("Bölüm bulunamadı")
                builder.setPositiveButton("Tamam", DialogInterface.OnClickListener { dialog, which ->
                    var intent = Intent(this, SectionActivity::class.java)
                    startActivity(intent)
                    finish()
                })
                builder.show()

            } else {
                bmp = ImageHelper.Base64ToBitmap(section!!.image)
            }

            bmp = Bitmap.createScaledBitmap(bmp!!, width, width, true)
            var bitmaps = BitmapHelper.splitBitmap(bmp!!, row, count)
            orderBitmapList = bitmaps.map { bitmapModel -> bitmapModel.id }.toIntArray()

            var e = arrayListOf<BitmapModel>()
            var suffleArr = bitmaps.shuffled()
            e.addAll(suffleArr)
            bitmapList = e

            var adapters = BitmapRecyclerAdapters(this, bitmapList!!, row, count, this)
            imageComponent.setImageBitmap(bmp)

            var gridLayoutManager = GridLayoutManager(this, row, LinearLayoutManager.VERTICAL, false)

            grd_GameRecylerView.layoutManager = gridLayoutManager
            grd_GameRecylerView.adapter = adapters
            touchHelper = ItemTouchHelper(simpleCallback)
            touchHelper.attachToRecyclerView(grd_GameRecylerView)
            textToSpeech = TextToSpeech(this@GameActivity, object : TextToSpeech.OnInitListener {
                override fun onInit(status: Int) {
                    if (status != TextToSpeech.ERROR) {
                        textToSpeech.setLanguage(Locale("tr", "TR"))
                    }
                }
            })

            if (!section!!.textTr.isEmpty()) {
                txt_text_tr.text = section!!.textTr.capitalize()
                btn_textToSpeachTr.setOnClickListener {
                    textToSpeech.setLanguage(Locale("tr", "TR"))
                    textToSpeech.speak(section!!.textTr, TextToSpeech.QUEUE_FLUSH, null)
                }

                btn_speachToTextTr.setOnClickListener { v ->
                    var listenerDialogView = LayoutInflater.from(this@GameActivity).inflate(R.layout.activity_game_microphone_dialog, null)
                    var alertDialogBuilder = AlertDialog.Builder(this@GameActivity).setView(listenerDialogView).setTitle("Dinleniyor...")
                    var listenerDialog = alertDialogBuilder.create()
                    var speachListenerDots = listenerDialog.findViewById<LoadingDots>(R.id.loadingDots)
                    var imgSpeachBtn = listenerDialog.findViewById<ImageButton>(R.id.img_speachBtn)
                    DrawableCompat.setTint(imgSpeachBtn!!.background, ContextCompat.getColor(this@GameActivity, R.color.colorBlue))
                    //imgSpeachBtn!!.backgroundTintList = resources.getColorStateList(R.color.shrine_pink_900)
                    //ViewCompat.setBackgroundTintList(imgSpeachBtn!!, ColorStateList.valueOf(resources.getColor(R.color.colorBlue)))
                    speachListenerDots!!.startAnimation()
                    listenerDialog.show()
                }
            }
            if (!section!!.textEn.isEmpty()) {
                txt_text_en.text = section!!.textEn.capitalize()
                btn_textToSpeachEn.setOnClickListener {
                    textToSpeech.setLanguage(Locale.ENGLISH)
                    textToSpeech.speak(section!!.textEn, TextToSpeech.QUEUE_FLUSH, null)
                }
            }
        } catch (ex: ExceptionInInitializerError) {
            Toast.makeText(this, ex.localizedMessage, Toast.LENGTH_LONG).show()
        }


    }

    override fun onBackPressed() {
        var redirect = Intent(this@GameActivity, SectionActivity::class.java)
        redirect.putExtra(Constants.IS_EDITTABLE_KEY, false)
        startActivity(redirect)
        finish()
    }

    override fun onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop()
        }
        super.onPause()
    }

    fun getDisplayMetrics(): DisplayMetrics {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics
    }

    private fun intializeRecognizeIntent() {
        speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
    }

    var simpleCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END, 0) {
        @RequiresApi(Build.VERSION_CODES.O_MR1)
        override fun onMove(@NonNull recyclerView: RecyclerView, @NonNull viewHolder: RecyclerView.ViewHolder, @NonNull target: RecyclerView.ViewHolder): Boolean {
            val fromPosition = viewHolder.adapterPosition
            val toPosition = target.adapterPosition
            if (checkStepControl(fromPosition, toPosition)) {
                Collections.swap(bitmapList!!, fromPosition, toPosition)
                recyclerView.adapter = BitmapRecyclerAdapters(baseContext, bitmapList!!, row, count, this@GameActivity)
                stepCount++
                txtStepCounter.text = stepCount.toString()
                if (checkFinishControl()) {
                    var dialog = AlertDialog.Builder(this@GameActivity)
                    dialog.setTitle("Puzzle Oyunu")
                    dialog.setMessage("Tebrikler! puzzle'ı tamamladınız")
                    dialog.show()
                    /*var layout = LayoutInflater.from(this@GameActivity).inflate(R.layout.activity_game_alert_dialog, null)
                    var a = BottomSheetDialog(this@GameActivity)
                    a.setContentView(layout)
                    a.dismissWithAnimation = true
                    a.show()*/

                }
            }
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        }
    }

    fun checkFinishControl(): Boolean {
        var maps = bitmapList!!.map { bitmapModel -> bitmapModel.id }.toIntArray()
        return orderBitmapList!! contentEquals maps
    }

    fun checkStepControl(_from: Int, _to: Int): Boolean {
        if (_from + 1 == _to) {
            return true
        } else if (_from - 1 == _to) {
            return true
        } else if (_from + row == _to) {
            return true
        } else if (_from - row == _to) {
            return true
        } else return false
    }

    override fun requestDrag(viewHolder: RecyclerView.ViewHolder?) {
        touchHelper.startDrag(viewHolder!!)
    }
}

