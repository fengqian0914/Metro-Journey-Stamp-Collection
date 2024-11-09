package com.example.MRTAPP.UI.Home

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.example.MRTAPP.R
import com.google.android.material.tabs.TabLayout

class station_data : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_station_data)

        val station_name = intent.getStringExtra("name")
        val tab_Info = findViewById<TabLayout>(R.id.tab_Info)
        val viewPager = findViewById<ViewPager>(R.id.viewPager)

        val fragmentAdapter = Station_tab_Adapter(supportFragmentManager, station_name.toString())
        fragmentAdapter.addFragment(Fragment_Station_info(), getString(R.string.Station_information))
        fragmentAdapter.addFragment(Fragment_Train_info(), getString(R.string.Train_information))
        viewPager.adapter = fragmentAdapter
        tab_Info.setupWithViewPager(viewPager)
        // 設置分頁切換監聽器
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                // 當切換到 Train Information 分頁時開始倒數計時
                if (position == 1) { // 這裡的 1 是 Fragment_Train_info 的索引
                    val trainInfoFragment = fragmentAdapter.getItem(position) as Fragment_Train_info
                    if(station_name!="板新"&&station_name!="中原"&&station_name!="橋和"&&station_name!="板橋(環狀)"){
                        trainInfoFragment.startTimer() // 調用 startTimer 方法
                    }
                } else {
                    // 當切換到其他分頁時停止計時
                    val previousFragment = fragmentAdapter.getItem(1) as Fragment_Train_info
                    previousFragment.stopTimer() // 停止計時
                }
            }
            override fun onPageScrollStateChanged(state: Int) {}
        })
        findViewById<LinearLayout>(R.id.goback).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
