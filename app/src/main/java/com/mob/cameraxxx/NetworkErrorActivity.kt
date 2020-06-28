package com.mob.cameraxxx

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.mob.cameraxxx.helpers.NetworkHelper

class NetworkErrorActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network_error)

        var btn_networkCheck: Button = findViewById(R.id.btn_networkCheck)
        btn_networkCheck.setOnClickListener {
            if (NetworkHelper.isOnline(this@NetworkErrorActivity) === true) {
                startActivity(Intent(this@NetworkErrorActivity, AccountActivity::class.java))
                finish()
            } else {
                Toast.makeText(this@NetworkErrorActivity, "İnternete bağlı değilsiniz", Toast.LENGTH_LONG).show()
            }
        }
    }
}
