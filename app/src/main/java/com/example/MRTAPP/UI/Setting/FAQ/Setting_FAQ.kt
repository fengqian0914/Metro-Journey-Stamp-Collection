package com.example.MRTAPP.UI.Setting.FAQ

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.MRTAPP.R
import com.example.MRTAPP.UI.Cameras.recyclerViewAdapter
import com.example.MRTAPP.UI.Home.Info_RecyclerView.StationInfo_RecylerViewAdapter
import com.example.MRTAPP.UI.Home.Info_RecyclerView.TrainInfoList
import com.example.MRTAPP.UI.Home.MainActivity

class Setting_FAQ : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var settingFAQRecylerViewAdapter: Setting_FAQ_RecylerViewAdapter
    private val FAQList = mutableListOf<Setting_FAQ_List>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_faq)
        supportActionBar?.hide()
        FAQList.addAll(
            listOf(
                Setting_FAQ_List(
                    getString(R.string.faq_title_1),
                    getString(R.string.faq_ans_1)
                ),
                Setting_FAQ_List(
                    getString(R.string.faq_title_2),
                    getString(R.string.faq_ans_2)
                ),
                Setting_FAQ_List(
                    getString(R.string.faq_title_3),
                    getString(R.string.faq_ans_3)
                ),
                Setting_FAQ_List(
                    getString(R.string.faq_title_4),
                    getString(R.string.faq_ans_4)
                ),
                Setting_FAQ_List(
                    getString(R.string.faq_title_5),
                    getString(R.string.faq_ans_5)
                ),
                Setting_FAQ_List(
                    getString(R.string.faq_title_6),
                    getString(R.string.faq_ans_6)
                ),
                Setting_FAQ_List(
                    getString(R.string.faq_title_7),
                    getString(R.string.faq_ans_7)
                ),
                Setting_FAQ_List(
                    getString(R.string.faq_title_8),
                    getString(R.string.faq_ans_8)
                ),
                Setting_FAQ_List(
                    getString(R.string.faq_title_9),
                    getString(R.string.faq_ans_9)
                ),
                Setting_FAQ_List(
                    getString(R.string.faq_title_10),
                    getString(R.string.faq_ans_10)
                ),
                Setting_FAQ_List(
                    getString(R.string.faq_title_11),
                    getString(R.string.faq_ans_11)
                )
            )
        )
        recyclerView = findViewById(R.id.FAQ_RecylerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        settingFAQRecylerViewAdapter = Setting_FAQ_RecylerViewAdapter(this, FAQList)
        recyclerView.adapter = settingFAQRecylerViewAdapter
        val goback=findViewById<LinearLayout>(R.id.goback)
        goback.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish() // 結束當前 activity
        }
    }
}