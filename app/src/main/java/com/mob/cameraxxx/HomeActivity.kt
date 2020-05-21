package com.mob.cameraxxx

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.mob.cameraxxx.service.DataAdapterService

class HomeActivity : AppCompatActivity() {

    var btnGameStart: Button? = null
    var btnNewLevel: Button? = null
    lateinit var dataAdapterService: DataAdapterService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        btnGameStart = findViewById<Button>(R.id.btn_gameStart)
        btnNewLevel = findViewById<Button>(R.id.btn_newLevel)
        dataAdapterService= DataAdapterService(this)
        dataAdapterService.initData("init.json")



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
