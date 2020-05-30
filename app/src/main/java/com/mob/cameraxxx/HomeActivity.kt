package com.mob.cameraxxx

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.view.animation.GridLayoutAnimationController
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.drawToBitmap
import com.mob.cameraxxx.constant.Constants
import com.mob.cameraxxx.service.DataAdapterService
import kotlinx.android.synthetic.main.alert_dialog.view.*

class HomeActivity : AppCompatActivity() {

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
    lateinit var btn_info: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        btnGameStart = findViewById<Button>(R.id.btn_gameStart)
        btnNewLevel = findViewById<ImageButton>(R.id.btn_newLevel)
        game_icon = findViewById(R.id.game_icon)
        dataAdapterService = DataAdapterService(this)
        dataAdapterService.initData("init.json")
        animationset = AnimationSet(true)
        btn_info = findViewById(R.id.btn_info)

        animationMoveToBottom = AnimationUtils.loadAnimation(applicationContext, R.anim.move_top_to_bottom)
        animationMoveToTop = AnimationUtils.loadAnimation(applicationContext, R.anim.move_bottom_to_top)
        fadeEffectAnim = AnimationUtils.loadAnimation(applicationContext, R.anim.hide_to_show)
        rotateAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.rotate_animation)
        rotateAndMove = AnimationUtils.loadAnimation(applicationContext, R.anim.move_and_rotate_anim)
        animationset.addAnimation(animationMoveToBottom)
        animationset.addAnimation(rotateAnimation)

        //game_icon!!.animation=animationMoveToBottom
        game_icon!!.animation = rotateAndMove
        btnGameStart!!.animation = animationMoveToTop
        btnNewLevel!!.animation = fadeEffectAnim
        btn_info!!.animation = fadeEffectAnim

        btnGameStart!!.setOnClickListener { v ->
            var intent = Intent(this@HomeActivity, SectionActivity::class.java)
            intent.putExtra(Constants.IS_EDITTABLE_KEY, false)
            startActivity(intent)
            return@setOnClickListener
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
