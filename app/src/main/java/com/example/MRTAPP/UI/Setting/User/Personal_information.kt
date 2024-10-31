package com.example.MRTAPP.UI.Setting.User

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.MRTAPP.UI.Home.MainActivity
import com.example.MRTAPP.R
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class Personal_information : AppCompatActivity() {

    private lateinit var storage: FirebaseStorage
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var imageView: ImageView
    private var imageUri: Uri? = null // 初始化為 null，表示尚未選擇圖片


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_information)
        supportActionBar?.hide()

        val tab_Info=findViewById<TabLayout>(R.id.tab_setting_personal)
        val viewPager=findViewById<ViewPager>(R.id.personalViewpager)
        val fragmentAdapter= Setting_Personal_Tab_Adapter(supportFragmentManager)
        fragmentAdapter.addFragment(Setting_Fragment_Tab_Name(), getString(R.string.Setting_tab_name))
        fragmentAdapter.addFragment(Setting_Fragment_Tab_Password(), getString(R.string.Setting_tab_password))
        fragmentAdapter.addFragment(Setting_Fragment_Tab_Image(), getString(R.string.Setting_tab_Image))

//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        viewPager.adapter=fragmentAdapter
        tab_Info.setupWithViewPager(viewPager)


       // 初始化 Firebase
        storage = FirebaseStorage.getInstance()
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // 設定預設圖片的 URI


        val goback=findViewById<LinearLayout>(R.id.goback)
        goback.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish() // 結束當前 activity
        }



    }
}



