package com.example.MRTAPP.UI.Setting.About_Us

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import com.example.MRTAPP.R
import com.example.MRTAPP.UI.Home.MainActivity

class Setting_About_Us : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_about_us)
        findViewById<Button>(R.id.FormBtn).setOnClickListener {
            val formUrl = "https://docs.google.com/forms/d/e/1FAIpQLSdFGEyJZhjmDWIYHoy8tMTRqzUM_wkN1EuZwnxEG74pDRQtEg/viewform?usp=sf_link"
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(formUrl)
            }
            startActivity(intent)

        }
        val goback = findViewById<LinearLayout>(R.id.goback)
        goback.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish() // 結束當前 activity
        }
    }
}