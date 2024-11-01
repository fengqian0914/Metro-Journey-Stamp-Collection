package com.example.MRTAPP.UI.Setting.Coupons

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.viewpager.widget.ViewPager
import com.example.MRTAPP.UI.Home.MainActivity
import com.example.MRTAPP.R
import com.google.android.material.tabs.TabLayout

class Coupons_data : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coupons_data)
        val tab_Info=findViewById<TabLayout>(R.id.tab_Info_coupons)
        val viewPager=findViewById<ViewPager>(R.id.viewPager)
        val fragmentAdapter= Coupon_tab_Adapter(supportFragmentManager)
        fragmentAdapter.addFragment(Fragment_coupon_Not_used(),this.getString(R.string.Not_used_information))
        fragmentAdapter.addFragment(Fragment_coupon_Expired(),this.getString(R.string.Expired_information))
//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        viewPager.adapter=fragmentAdapter
        tab_Info.setupWithViewPager(viewPager)

        findViewById<LinearLayout>(R.id.goback).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish() // 結束當前 activity

        }

    }
}