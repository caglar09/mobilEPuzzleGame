package com.mob.cameraxxx

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mob.cameraxxx.constant.Constants
import com.mob.cameraxxx.service.DataAdapterService

private var REQUEST_CODE_PERMISSIONS: Int = 11
private var REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET)

class HomeActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private var dbAuthUserRef = FirebaseDatabase.getInstance().getReference("users").child(user!!.uid)
    var btnGameStart: Button? = null
    var btnNewLevel: ImageButton? = null
    var game_icon: AppCompatImageView? = null
    lateinit var dataAdapterService: DataAdapterService
    lateinit var animationMoveToBottom: Animation
    lateinit var animationMoveToTop: Animation
    lateinit var fadeEffectAnim: Animation
    lateinit var rotateAnimation: Animation
    lateinit var animationset: AnimationSet
    lateinit var rotateAndMove: Animation
    lateinit var shakeAnim: Animation
    lateinit var btn_info: ImageButton
    lateinit var logoutBtn: ImageButton
    lateinit var authUsername: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        if (user == null) {
            var signinIntent = Intent(this@HomeActivity, AccountActivity::class.java)
            Toast.makeText(this@HomeActivity, "Lütfen giriş yapınız", Toast.LENGTH_LONG).show()
            startActivity(signinIntent)
            finish()
        }
        btnGameStart = findViewById<Button>(R.id.btn_gameStart)
        btnNewLevel = findViewById<ImageButton>(R.id.btn_newLevel)
        game_icon = findViewById(R.id.game_icon)
        btn_info = findViewById(R.id.btn_info)
        logoutBtn = findViewById(R.id.logoutBtn)
        authUsername = findViewById(R.id.authUsername)
        //Dataadapter Servis caglar
        dataAdapterService = DataAdapterService(this@HomeActivity)
        dataAdapterService.initData("init.json")

        //Dataadapter Servis caglar
        animationMoveToBottom = AnimationUtils.loadAnimation(applicationContext, R.anim.move_top_to_bottom)
        animationMoveToTop = AnimationUtils.loadAnimation(applicationContext, R.anim.move_bottom_to_top)
        fadeEffectAnim = AnimationUtils.loadAnimation(applicationContext, R.anim.hide_to_show)
        rotateAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.rotate_animation)
        rotateAndMove = AnimationUtils.loadAnimation(applicationContext, R.anim.move_and_rotate_anim)
        shakeAnim = AnimationUtils.loadAnimation(applicationContext, R.anim.shake_anim)

        //game_icon!!.animation=animationMoveToBottom
        game_icon!!.animation = rotateAndMove
        btnGameStart!!.animation = animationMoveToTop
        btnNewLevel!!.animation = fadeEffectAnim
        btn_info!!.animation = fadeEffectAnim
        logoutBtn!!.animation = fadeEffectAnim
        authUsername!!.animation = shakeAnim
        dbAuthUserRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(data: DataSnapshot) {
                var _username = data.child("userName")
                authUsername.text = "Hoşgeldiniz " + _username.value
            }
        })

        btnGameStart!!.setOnClickListener { v ->
            var intent = Intent(this@HomeActivity, SectionActivity::class.java)
            intent.putExtra(Constants.IS_EDITTABLE_KEY, false)
            startActivity(intent)
            return@setOnClickListener
        }
        logoutBtn.setOnClickListener {
            mAuth!!.signOut()
            var signInIntent = Intent(this@HomeActivity, AccountActivity::class.java)
            startActivity(signInIntent)
            finish()
        }


         btnNewLevel!!.setOnClickListener { v ->
              var config = dataAdapterService.getAppConfig()
              var alertDialogView = LayoutInflater.from(this).inflate(R.layout.alert_dialog, null)
              var mBuilder = AlertDialog.Builder(this).setView(alertDialogView).setTitle("Pin Girin")
              var alertDialog = mBuilder.show()
              var txtPin = alertDialogView.findViewById<EditText>(R.id.dialog_pin)
              var loginBtn: Button = alertDialogView.findViewById(R.id.dialog_login_btn)
              var cancelBtn: Button = alertDialogView.findViewById(R.id.dialog_cancel_btn)
              loginBtn.setOnClickListener { v ->
                  if (!txtPin.text.toString().isEmpty()) {
                      if (txtPin.text.toString().equals(config.defaultPin)) {
                          var intent = Intent(this@HomeActivity, SectionActivity::class.java)
                          intent.putExtra(Constants.IS_EDITTABLE_KEY, true)
                          startActivity(intent)
                          alertDialog.dismiss()
                      } else {
                          Toast.makeText(this, "Girdiğiniz pin yanlış", Toast.LENGTH_LONG).show()
                      }
                  } else {
                      Toast.makeText(this, "Lütfen pin giriniz.", Toast.LENGTH_LONG).show()
                  }
              }
              cancelBtn.setOnClickListener { v ->
                  alertDialog.dismiss()
              }
              return@setOnClickListener
          }

        btn_info!!.setOnClickListener {
            var infoView = layoutInflater.inflate(R.layout.activity_help_dialog, null)
            var dialog = AlertDialog.Builder(this@HomeActivity).setTitle("Hakkında").setView(infoView)
            dialog.show()
        }
    }



}
