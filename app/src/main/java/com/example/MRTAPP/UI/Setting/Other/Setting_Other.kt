package com.example.MRTAPP.UI.Setting.Other

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import com.example.MRTAPP.R
import com.example.MRTAPP.UI.Home.MainActivity

class Setting_Other : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_other)
        supportActionBar?.hide()

        val goback = findViewById<LinearLayout>(R.id.goback)
        goback.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish() // 結束當前 activity
        }
    }
}