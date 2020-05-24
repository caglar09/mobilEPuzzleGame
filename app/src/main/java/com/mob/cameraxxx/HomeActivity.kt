package com.mob.cameraxxx

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.view.animation.GridLayoutAnimationController
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.drawToBitmap
import com.mob.cameraxxx.service.DataAdapterService

class HomeActivity : AppCompatActivity() {

    var btnGameStart: Button? = null
    var btnNewLevel: ImageButton? = null
    var game_icon: AppCompatImageView?=null
    lateinit var dataAdapterService: DataAdapterService
    lateinit var animationMoveToBottom:Animation
    lateinit var animationMoveToTop:Animation
    lateinit var fadeEffectAnim:Animation
    lateinit var rotateAnimation:Animation
    lateinit var  animationset:AnimationSet
    lateinit var rotateAndMove:Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        btnGameStart = findViewById<Button>(R.id.btn_gameStart)
        btnNewLevel = findViewById<ImageButton>(R.id.btn_newLevel)
        game_icon=findViewById(R.id.game_icon)
        dataAdapterService= DataAdapterService(this)
        dataAdapterService.initData("init.json")
        animationset=AnimationSet(true)

        animationMoveToBottom=AnimationUtils.loadAnimation(applicationContext,R.anim.move_top_to_bottom)
        animationMoveToTop=AnimationUtils.loadAnimation(applicationContext,R.anim.move_bottom_to_top)
        fadeEffectAnim=AnimationUtils.loadAnimation(applicationContext,R.anim.hide_to_show)
        rotateAnimation=AnimationUtils.loadAnimation(applicationContext,R.anim.rotate_animation)
        rotateAndMove=AnimationUtils.loadAnimation(applicationContext,R.anim.move_and_rotate_anim)
        animationset.addAnimation(animationMoveToBottom)
        animationset.addAnimation(rotateAnimation)

        //game_icon!!.animation=animationMoveToBottom
        game_icon!!.animation=rotateAndMove

        btnGameStart!!.animation=animationMoveToTop
        btnNewLevel!!.animation=fadeEffectAnim


        btnGameStart!!.setOnClickListener { v ->
            var intent = Intent(this@HomeActivity, GameActivity::class.java)
            startActivity(intent)
            return@setOnClickListener
        }

        btnNewLevel!!.setOnClickListener { v ->
            var intent = Intent(this@HomeActivity, SectionActivity::class.java)
            startActivity(intent)
            return@setOnClickListener
        }
    }

}
