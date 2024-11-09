package com.example.MRTAPP.UI.Home

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.example.MRTAPP.R
import com.example.MRTAPP.UI.Camera
import com.example.MRTAPP.UI.Home_fragment
import com.example.MRTAPP.UI.Settings_Fragment
import com.example.MRTAPP.UI.Star_Fragment
import com.example.MRTAPP.UI.Store_Fragment
import com.example.MRTAPP.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import org.osmdroid.config.Configuration

class MainActivity : AppCompatActivity(), MapView.StationTextListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navview: BottomNavigationView
    private lateinit var navbar_camera: FloatingActionButton
    private lateinit var startStation: TextView
    private lateinit var endStation: TextView
    private lateinit var mapView: MapView


    private val CHANNEL_ID = "MRT_CHANNEL_ID"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selectedColor = ContextCompat.getColor(this, R.color.nav_item_icon_tint)
        val unselectedColor = ContextCompat.getColor(this, R.color.nav_item_icon_tint_dark)
        // 使用 ColorStateList 設定顏色
        val colorStateList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_selected),
                intArrayOf(-android.R.attr.state_selected)
            ),
            intArrayOf(selectedColor, unselectedColor)
        )

        mapView = findViewById(R.id.mapView)
        navview = findViewById(R.id.bottomNavigationView)
        navview.itemIconTintList = colorStateList
        navview.itemTextColor = colorStateList
        navview.background = null;
        navview.menu.getItem(2).isEnabled = false
        replece(Home_fragment())
        navview.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navbar_home -> replece(Home_fragment())
                R.id.navbar_setting -> replece(Settings_Fragment())
                R.id.navbar_star -> replece(Star_Fragment())
                R.id.navbar_store -> replece(Store_Fragment())
            }
            true
        }
        navbar_camera = findViewById(R.id.fab)
        navbar_camera.setOnClickListener {
            replece(Camera())
        }


        createNotificationChannel()




        // 回調
        mapView.setStationTextListener(this)
    }

    private fun replece(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmenttransaction = fragmentManager.beginTransaction()
        fragmenttransaction.replace(R.id.navhost, fragment)
        fragmenttransaction.commit()
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "MRT Arrival Notification"
            val descriptionText = "Notification channel for MRT arrival times"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    override fun onStationTextChanged(Station: String, location: String,code:String) {


        if(location=="start"){
            this.startStation.text = Station
        }else{
            this.endStation.text = Station

        }

    }





}