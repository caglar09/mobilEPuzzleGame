package com.mob.cameraxxx

import android.Manifest
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mob.cameraxxx.adapters.BitmapRecyclerAdapters
import com.mob.cameraxxx.constant.Constants
import com.mob.cameraxxx.constant.RECOGNIZE_LANGUAGES
import com.mob.cameraxxx.data.BitmapModel
import com.mob.cameraxxx.data.Section
import com.mob.cameraxxx.helpers.BitmapHelper
import com.mob.cameraxxx.helpers.ImageHelper
import com.mob.cameraxxx.service.DataAdapterService
import com.mob.cameraxxx.service.StartDragListener
import com.tomergoldst.tooltips.ToolTipsManager
import java.util.*
import kotlin.collections.HashMap

private var REQUEST_CODE_PERMISSIONS: Int = 11
private var REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.RECORD_AUDIO)

@Suppress("DEPRECATION")
class GameActivity : AppCompatActivity(), StartDragListener, ToolTipsManager.TipListener {
    var mAuthUser = FirebaseAuth.getInstance()
    var dbRef = FirebaseDatabase.getInstance().getReference("users").child(mAuthUser!!.uid!!).child("sections")

    //#region variables
    var bitmapList: ArrayList<BitmapModel>? = null
    var orderBitmapList: IntArray? = null
    val row: Int = 3
    val count: Int = 3
    private var isFirstLoad: Boolean = true

    // lateinit var imageComponent: ImageView
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
    lateinit var btn_checkSpeechTr: ImageButton
    lateinit var btn_checkSpeechEn: ImageButton
    lateinit var relativeLayout_Step2: RelativeLayout
    lateinit var tooltipManager: ToolTipsManager
    lateinit var speechRecognizer: SpeechRecognizer
    lateinit var myCountDownTimer: MyCountDownTimer
    var section: Section? = null
    var stepCount = 0


    //#endregion
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
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
            btn_checkSpeechTr = findViewById(R.id.img_checkTextSpeechTr)
            btn_checkSpeechEn = findViewById(R.id.img_checkTextSpeechEn)
            //imageComponent = findViewById<ImageView>(R.id.img_Orji)
            relativeLayout_Step2 = findViewById<RelativeLayout>(R.id.relative_layout_step2)
            grd_GameRecylerView = findViewById<RecyclerView>(R.id.grd_BitmapListView)
            _dataAdapterService = DataAdapterService(this)
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this@GameActivity)
            var shakeAnim = AnimationUtils.loadAnimation(this@GameActivity, R.anim.shake_anim)
            btn_Help.animation = shakeAnim
            tooltipManager = ToolTipsManager(this@GameActivity)
            btn_Help.setOnClickListener {
                var helpDialogView = LayoutInflater.from(this@GameActivity).inflate(R.layout.activity_game_alert_dialog, null)
                var helpDialog = Dialog(this@GameActivity)
                helpDialog.setContentView(helpDialogView)
                helpDialog.show()
                val layoutParams = WindowManager.LayoutParams()
                layoutParams.copyFrom(helpDialog.window!!.attributes)
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
                layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
                helpDialog.window!!.attributes = layoutParams
                // tooltipManager.show(tooltipBuilder.build())
            }

            relativeLayout_Step2.visibility = View.INVISIBLE

            var width = getDisplayMetrics().widthPixels

            var bmp: Bitmap? = BitmapFactory.decodeResource(resources, R.drawable.naturals)
            var currentSectionId = intent.getStringExtra(Constants.SECTION_ID)

            dbRef.child(currentSectionId).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(s: DataSnapshot) {
                    var _section = s.getValue(Section::class.java)
                    section = _section

                    if (isFirstLoad) {
                        isFirstLoad = false
                        if (section == null) {
                            var builder = AlertDialog.Builder(this@GameActivity)
                            builder.setMessage("Bölüm bulunamadı")
                            builder.setPositiveButton("Tamam", DialogInterface.OnClickListener { dialog, which ->
                                var intent = Intent(this@GameActivity, SectionActivity::class.java)
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

                        var adapters = BitmapRecyclerAdapters(this@GameActivity, bitmapList!!, row, count, this@GameActivity)
                        var gridLayoutManager = GridLayoutManager(this@GameActivity, row, LinearLayoutManager.VERTICAL, false)

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

                        //#region ImagePreviwer
                        var previewView = layoutInflater.inflate(R.layout.activity_game_preview_image, null)
                        var previewImage = previewView.findViewById<ImageView>(R.id.img_review)
                        var nextbutton = previewView.findViewById<Button>(R.id.btn_previewNext)
                        var backButton = previewView.findViewById<Button>(R.id.btn_previewBack)
                        var txt_previewTr = previewView.findViewById<TextView>(R.id.txt_previewTextTr)
                        var txt_previewEn = previewView.findViewById<TextView>(R.id.txt_previewTextEn)
                        previewImage.setImageBitmap(bmp)
                        txt_previewTr.setText(section!!.textTr.capitalize())
                        txt_previewEn.setText(section!!.textEn.capitalize())
                        var preview = AlertDialog.Builder(this@GameActivity).setView(previewView).setTitle("Ön izleme").create()
                        myCountDownTimer = MyCountDownTimer(10000, 1000, preview)
                        preview.setCancelable(false)
                        preview.window!!.attributes.windowAnimations = R.style.DialogSlide
                        backButton.setOnClickListener {
                            myCountDownTimer.onFinish()
                            onBackPressed()
                            finish()
                        }
                        nextbutton.setOnClickListener {
                            myCountDownTimer.onFinish()
                            preview.dismiss()
                        }
                        preview.setOnShowListener { dialog: DialogInterface? ->
                            myCountDownTimer.start()
                        }

                        if (section!!.knowedTr) {
                            btn_checkSpeechTr!!.backgroundTintList = resources.getColorStateList(R.color.colorGreen)
                        }
                        if (section!!.knowedEn) {
                            btn_checkSpeechEn!!.backgroundTintList = resources.getColorStateList(R.color.colorGreen)
                        }
                        if (section!!.completed) {
                            var confirmDialog = AlertDialog.Builder(this@GameActivity).setTitle("Dikkat!").setMessage("Bölümü tekrarlamak istiyormusunuz?")
                            confirmDialog.setPositiveButton("Tekrar Oyna") { dialog, which ->
                                if (resetSection()) {
                                    btn_checkSpeechTr!!.backgroundTintList = resources.getColorStateList(R.color.colorDanger)
                                    btn_checkSpeechEn!!.backgroundTintList = resources.getColorStateList(R.color.colorDanger)
                                    preview.show()
                                }
                            }
                            confirmDialog.setNegativeButton("Geri") { dialog, which ->
                                onBackPressed()
                                finish()
                            }
                            confirmDialog.setCancelable(false)
                            confirmDialog.show()
                        } else {
                            preview.show()
                        }
                        //#endregion
                    }

                    var listenerDialogView = LayoutInflater.from(this@GameActivity).inflate(R.layout.activity_game_microphone_dialog, null)
                    var btn_speechOk = listenerDialogView.findViewById<Button>(R.id.btn_okSpeech)
                    var btn_speechCancel = listenerDialogView.findViewById<Button>(R.id.btn_cancelSpeech)
                    var txt_readedText = listenerDialogView.findViewById<TextView>(R.id.txt_readedText)

                    var alertDialogBuilder = AlertDialog.Builder(this@GameActivity).setView(listenerDialogView).setTitle("Lütfen mikrofon tuşuna basarak konuşun").setIcon(R.drawable.ic_mic_black_24dp)
                    alertDialogBuilder.setCancelable(false)

                    var listenerDialog = alertDialogBuilder.create()

                    if (!section!!.textTr.isEmpty()) {
                        txt_text_tr.text = section!!.textTr.capitalize()
                        btn_textToSpeachTr.setOnClickListener {
                            textToSpeech.setLanguage(Locale("tr", "TR"))
                            textToSpeech.speak(section!!.textTr, TextToSpeech.QUEUE_FLUSH, null)
                        }

                        btn_speachToTextTr.setOnClickListener { v ->
                            listenerDialog.show()
                            startRecognize(listenerDialogView, RECOGNIZE_LANGUAGES.TR)
                            btn_speechOk.setOnClickListener {
                                var readedText = txt_readedText.text

                                if (!readedText.toString().toLowerCase().equals(section!!.textTr.toLowerCase())) {
                                    Toast.makeText(this@GameActivity, "Söylediğiniz kelimeler eşleşmiyor. Lütfen tekrar deneyiniz.", Toast.LENGTH_LONG).show()
                                    btn_checkSpeechTr!!.backgroundTintList = resources.getColorStateList(R.color.colorDanger)
                                } else {
                                    listenerDialog.dismiss()
                                    section!!.knowedTr = true
                                    if (checkFinishedControl()) {
                                        showComplatedDialog()
                                    }
                                    btn_checkSpeechTr!!.backgroundTintList = resources.getColorStateList(R.color.colorGreen)
                                    txt_readedText.text = ""
                                }
                            }
                            btn_speechCancel.setOnClickListener {
                                listenerDialog.dismiss()
                            }

                        }
                    }
                    if (!section!!.textEn.isEmpty()) {
                        txt_text_en.text = section!!.textEn.capitalize()
                        btn_textToSpeachEn.setOnClickListener {
                            textToSpeech.setLanguage(Locale.ENGLISH)
                            textToSpeech.speak(section!!.textEn, TextToSpeech.QUEUE_FLUSH, null)
                        }

                        btn_speachToTextEn.setOnClickListener { v ->
                            if (allPermissionsGranted()) {
                                listenerDialog.show()
                                startRecognize(listenerDialogView, RECOGNIZE_LANGUAGES.EN)
                                btn_speechOk.setOnClickListener {
                                    var readedText = txt_readedText.text
                                    if (!readedText.toString().toLowerCase().equals(section!!.textEn.toLowerCase())) {
                                        Toast.makeText(this@GameActivity, "Söylediğiniz kelimeler eşleşmiyor. Lütfen tekrar deneyiniz.", Toast.LENGTH_LONG).show()
                                        btn_checkSpeechEn!!.backgroundTintList = resources.getColorStateList(R.color.colorDanger)
                                    } else {
                                        listenerDialog.dismiss()
                                        btn_checkSpeechEn!!.backgroundTintList = resources.getColorStateList(R.color.colorGreen)
                                        section!!.knowedEn = true
                                        if (checkFinishedControl()) {
                                            showComplatedDialog()
                                        }
                                        txt_readedText.text = ""
                                    }
                                }
                                btn_speechCancel.setOnClickListener {
                                    listenerDialog.dismiss()
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
            //section = _dataAdapterService.getSection(currentSectionId.toString())

        } catch (ex: ExceptionInInitializerError) {
            Toast.makeText(this, ex.localizedMessage, Toast.LENGTH_LONG).show()
        }


    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()

    }

    override fun onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop()
        }
        super.onPause()
    }

    override fun requestDrag(viewHolder: RecyclerView.ViewHolder?) {
        touchHelper.startDrag(viewHolder!!)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this,
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT).show()
                finish()

            }
        }
    }

    fun getDisplayMetrics(): DisplayMetrics {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics
    }

    fun checkFinishedControl(): Boolean {
        if (section!!.knowedTr && section!!.knowedEn) {
            section!!.completed = true
            var childUpdates = HashMap<String, Boolean>()
            childUpdates["completed"] = true
            childUpdates["knowedTr"] = true
            childUpdates["knowedEn"] = true
            dbRef.child(section!!.id).updateChildren(childUpdates as Map<String, Any>)
            return true
        } else {
            return false
        }
    }

    fun resetSection(): Boolean {
        section!!.completed = false
        section!!.puzzleCompleted = false
        section!!.knowedEn = false
        section!!.knowedTr = false

        var childUpdates = HashMap<String, Boolean>()
        childUpdates["completed"] = false
        childUpdates["knowedTr"] = false
        childUpdates["knowedEn"] = false
        childUpdates["puzzleCompleted"] = false

        dbRef.child(section!!.id).updateChildren(childUpdates as Map<String, Any>)
        isFirstLoad = true
        return true
    }

    fun showComplatedDialog() {
        var complatedDialogView = layoutInflater.inflate(R.layout.activity_finish_dialog, null)
        var complatedDialogBuilder = AlertDialog.Builder(this@GameActivity).setView(complatedDialogView).create()
        var nextLevelBtn = complatedDialogView.findViewById<Button>(R.id.btn_nextLevel)
        nextLevelBtn.setOnClickListener {
            var nextlevel = _dataAdapterService.nextLevel(section!!.id)//
            if (nextlevel != null) {
                var intent = Intent(this@GameActivity, GameActivity::class.java)
                intent.putExtra(Constants.SECTION_ID, nextlevel!!.id)
                startActivity(intent)
                finish()
            } else {
                var intent = Intent(this@GameActivity, Section::class.java)
                intent.putExtra(Constants.IS_EDITTABLE_KEY, false)
                startActivity(intent)
                finish()
            }
            return@setOnClickListener
        }
        complatedDialogBuilder.show()

    }

    fun startRecognize(dialogView: View, reconizeLang: RECOGNIZE_LANGUAGES) {
        var speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        when (reconizeLang) {
            RECOGNIZE_LANGUAGES.EN ->
                speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH)
            RECOGNIZE_LANGUAGES.TR ->
                speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale("tr", "tr-TR"))
        }

        var imgSpeachBtn = dialogView.findViewById<ImageButton>(R.id.img_speachBtn)
        var txt_readedText = dialogView.findViewById<TextView>(R.id.txt_readedText)
        var btn_speechOk = dialogView.findViewById<Button>(R.id.btn_okSpeech)
        var btn_speechCancel = dialogView.findViewById<Button>(R.id.btn_cancelSpeech)

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onError(error: Int) {
                Toast.makeText(dialogView.context, "Söylediğiniz anlaşılmadı. Lütfen tekrar söyleyin", Toast.LENGTH_LONG).show()
                btn_speechOk!!.isEnabled = false
            }

            override fun onBeginningOfSpeech() {
                imgSpeachBtn!!.backgroundTintList = resources.getColorStateList(R.color.colorBlue)
            }

            override fun onBufferReceived(buffer: ByteArray?) {
            }

            override fun onEndOfSpeech() {
                imgSpeachBtn!!.backgroundTintList = resources.getColorStateList(R.color.colorPrimaryDark)
            }

            override fun onRmsChanged(rmsdB: Float) {
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
            }

            override fun onPartialResults(partialResults: Bundle?) {
            }

            override fun onReadyForSpeech(params: Bundle?) {
                imgSpeachBtn!!.backgroundTintList = resources.getColorStateList(R.color.colorBlue)
            }

            override fun onResults(results: Bundle?) {
                var _results = results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (_results != null) {
                    btn_speechOk!!.isEnabled = true
                    txt_readedText.text = _results!![0]
                } else {
                    Toast.makeText(dialogView.context, "Söylediğiniz anlaşılmadı. Lütfen tekrar söyleyin", Toast.LENGTH_LONG).show()
                    btn_speechOk!!.isEnabled = false
                }
            }
        })
        imgSpeachBtn.setOnClickListener {
            speechRecognizer.startListening(speechRecognizerIntent)
        }

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
                    var childUpdates = HashMap<String, Boolean>()
                    childUpdates["puzzleCompleted"] = true
                    var newRef = dbRef.child(section!!.id)
                    newRef.updateChildren(childUpdates as Map<String, Any>)
                    section!!.puzzleCompleted = true
                    //_dataAdapterService.updateSection(section!!.id, section!!)//
                    var dialog = AlertDialog.Builder(this@GameActivity)
                    dialog.setTitle("Puzzle Oyunu")
                    dialog.setMessage("Tebrikler! puzzle'ı tamamladınız. Şimdi sıra 2. adımda lütfen aşağıdaki kelimeleri türkçe ve inglizce olarak seslendirin.")
                    dialog.show()
                    relativeLayout_Step2.animation = AnimationUtils.loadAnimation(this@GameActivity, R.anim.move_bottom_to_top)
                    relativeLayout_Step2.visibility = View.VISIBLE
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

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onTipDismissed(view: View?, anchorViewId: Int, byUser: Boolean) {
        /* if (anchorViewId==R.id.btn_Help){
             Toast.makeText(this@GameActivity,"Dismiss",Toast.LENGTH_SHORT).show()
         }*/
    }

    class MyCountDownTimer : CountDownTimer {
        private var millisInfeature: Long
        private var countDownInterval: Long
        private var alertDialog: AlertDialog

        constructor(_millisUntilFinished: Long, _countDownInterval: Long, _dialog: AlertDialog) : super(_millisUntilFinished, _countDownInterval) {
            millisInfeature = _millisUntilFinished
            countDownInterval = _countDownInterval
            alertDialog = _dialog
        }

        override fun onFinish() {
            if (alertDialog.isShowing)
                alertDialog.dismiss()
            super.cancel()
        }

        override fun onTick(millisUntilFinished: Long) {
            var progressBar = alertDialog.findViewById<ProgressBar>(R.id.progressbar)
            var progressText = alertDialog.findViewById<TextView>(R.id.txt_progress)
            var progress: Int = (millisUntilFinished / 1000).toInt()
            progressBar!!.progress = progressBar.max - progress
            progressText!!.text = progress.toString()
        }

    }
}


