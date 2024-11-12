package com.example.MRTAPP.UI.Setting.Station

import android.app.Dialog
import android.app.PendingIntent
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import com.example.MRTAPP.Other.GetStationNameLanguage
import com.example.MRTAPP.Other.Widget.ItemAppWidget
import com.example.MRTAPP.Other.Widget.MyPinnedWidgetReceiver
import com.example.MRTAPP.Other.dialogs
import com.example.MRTAPP.R
import com.example.MRTAPP.UI.Home.MainActivity
import org.json.JSONObject

class Setting_station : AppCompatActivity() {
    var routeIndex = 0
    var routeText = ""
    var widgetStationName: String? = null
    var stationIndex = 0
    var videoIndex=0
    private lateinit var appWidgetHost: AppWidgetHost

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_station)
        supportActionBar?.hide()

        val routesSpinner = findViewById<Spinner>(R.id.spinner_routes)
        val stationsSpinner = findViewById<Spinner>(R.id.spinner_stations)
        val routes = listOf("BR ${getString(R.string.route_BR)}", "G ${getString(R.string.route_G)}", "R ${getString(R.string.route_R)}",
            "BL ${getString(R.string.route_BL)}", "O ${getString(R.string.route_O)}", "Y ${getString(R.string.route_Y)}")
        // 創建 ArrayAdapter
        val routesAdapter = ArrayAdapter(this, R.layout.spinner_item, routes)
        routesAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        routesSpinner.adapter = routesAdapter

        stationItem(routeIndex, stationsSpinner)
        //路線清單
        routesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                stationItem(position, stationsSpinner)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {//沒點選
            }
        }
        //站點清單
        stationsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                widgetStationName = parent.getItemAtPosition(position).toString()
                val confirmText = findViewById<TextView>(R.id.confirm_text)
                confirmText.text = widgetStationName
                stationIndex = position
            }
            override fun onNothingSelected(parent: AdapterView<*>) {//沒點選
            }
        }

        val goback = findViewById<LinearLayout>(R.id.goback)
        goback.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish() // 結束當前 activity
        }

        val settingStationSaveBtn = findViewById<Button>(R.id.setting_station_save_btn)
        settingStationSaveBtn.setOnClickListener {

            val prefs = this.getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE)
            val isWidgetAdded = prefs.getBoolean("isWidgetAdded", false)
            val stationName = widgetStationName.toString()
            val strArray = stationName.split("\n")
            val sharedPreferences = this.getSharedPreferences("widgetStationName", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            val getLanguage=GetStationNameLanguage(this)
            val language=getLanguage.getsaveLanguage2(this)
            editor.putString("widgetStationName_Id", strArray[0])
            editor.putString("widgetStationName", strArray[1])
            if(language=="Zh_tw"){
                editor.putString("widgetStationName_en", getLanguage.getStationName2(this,strArray[2],"Zh_tw","En"))
            }else{
                editor.putString("widgetStationName_en", strArray[2])
            }

            editor.apply()

            if (isWidgetAdded) {
                Toast.makeText(this, this.getString(R.string.modified), Toast.LENGTH_SHORT).show()
                val appWidgetManager = getSystemService(AppWidgetManager::class.java)
                val myProvider = ComponentName(this, ItemAppWidget::class.java)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(myProvider)
                val itemAppWidget = ItemAppWidget()
                for (appWidgetId in appWidgetIds) {
                    itemAppWidget.onUpdate(this, appWidgetManager, appWidgetIds)
                }
            } else {
                Toast.makeText(this,this.getString(R.string.creating_widget), Toast.LENGTH_SHORT).show()
                pinAppWidget()
            }
        }
        appWidgetHost = AppWidgetHost(this, 1) // 初始化 AppWidgetHost
        val setting_station_widgetInfo_btn = findViewById<Button>(R.id.setting_station_widgetInfo_btn)
        setting_station_widgetInfo_btn.setOnClickListener {
            showdialog()
        }
    }
    private fun showdialog() {
        val dialog= Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.activity_dialogs)
        dialog.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.transparent)))
        val dialog_title=dialog.findViewById<TextView>(R.id.dialog_wait_title)
        val dialog_msg=dialog.findViewById<TextView>(R.id.dialog_wait_msg)
        val dialog_close=dialog.findViewById<ImageView>(R.id.dialog_close)
        val dialogs_videoview=dialog.findViewById<VideoView>(R.id.videoView)
        val dialog_bg=dialog.findViewById<LinearLayout>(R.id.dialog_bg)
        val dialog_video_layout=dialog.findViewById<LinearLayout>(R.id.video_btn_layout)
        video(dialogs_videoview,videoIndex)
        val Previousbtn=dialog.findViewById<Button>(R.id.dialog_Previous_btn)
        val Nextbtn=dialog.findViewById<Button>(R.id.dialog_Next_btn)
        dialog_bg.setBackgroundResource(R.color.startgradient)
        dialog_title.text=getString(R.string.app_name)
        dialog_msg.visibility=View.GONE
        dialogs_videoview.visibility=View.VISIBLE
        dialog_video_layout.visibility=View.VISIBLE
        Previousbtn.setOnClickListener {
            when(videoIndex){
                0-> {
                    videoIndex=2
                    video(dialogs_videoview,videoIndex)
                }
                1->{
                    videoIndex=0
                    video(dialogs_videoview,videoIndex)
                }
                2->{
                    videoIndex=1
                    video(dialogs_videoview,videoIndex)
                }
            }
        }
        Nextbtn.setOnClickListener {
            when(videoIndex){
                0-> {
                    videoIndex=1
                    video(dialogs_videoview,videoIndex)
                }
                1->{
                    videoIndex=2
                    video(dialogs_videoview,videoIndex)
                }
                2->{
                    videoIndex=0
                    video(dialogs_videoview,videoIndex)
                }
            }
        }



        dialog_close.setOnClickListener {
            dialog.dismiss()

        }
        dialog.show()
    }

    private fun pinAppWidget() {
        val appWidgetManager = getSystemService(AppWidgetManager::class.java)
        val myProvider = ComponentName(this, ItemAppWidget::class.java)
        // 檢查裝置是否支持固定小工具功能
        if (appWidgetManager.isRequestPinAppWidgetSupported) {
            // 檢查 Android 版本是否 >= O (API 26)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val pinnedWidgetCallbackIntent = Intent(this, MyPinnedWidgetReceiver::class.java)
                val pinnedWidgetCallbackPendingIntent: PendingIntent = PendingIntent.getBroadcast(this, 0,
                    pinnedWidgetCallbackIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                val success = appWidgetManager.requestPinAppWidget(myProvider, null,
                    pinnedWidgetCallbackPendingIntent)
                if (success) {
                    Toast.makeText(this, this.getString(R.string.confirm_widget_request), Toast.LENGTH_SHORT).show()
                    // 記錄小工具的狀態
                    val prefs = this.getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE)
                    val editor = prefs.edit()
                    editor.putBoolean("isWidgetAdded", true)
                    editor.apply()
                } else {
                    Toast.makeText(this, this.getString(R.string.request_failed), Toast.LENGTH_SHORT).show()
                }
            } else {
            }
        } else {
            Toast.makeText(this, this.getString(R.string.widget_not_supported), Toast.LENGTH_SHORT).show()
        }

    }

    fun stationItem(routeIndex: Int, stationsSpinner: Spinner) {
        routeText = when (routeIndex) {
            0 -> "BR" 1 -> "G" 2 -> "R"
            3 -> "BL" 4 -> "O" 5 -> "Y" else -> ""
        }
        val jsonString = this.assets?.open("mrt_language.json")?.bufferedReader().use { it?.readText() }
        val jsons = JSONObject(JSONObject(jsonString)[routeText].toString())
        val jsonkeys = jsons.keys()
        val stationNames = mutableListOf<String>()
        val InputLanguage = GetStationNameLanguage(this).getsaveLanguage(this)
        while (jsonkeys.hasNext()) {
            val key = jsonkeys.next()
            val stationObject = jsons.getJSONObject(key)
            val stationName = stationObject.getString("Zh_tw")
            var stationName_lang=""
            if(InputLanguage=="Zh_tw"){
                stationName_lang = stationObject.getString("En")
            }else
                stationName_lang = stationObject.getString(InputLanguage)
            stationNames.add("$key\n$stationName\n$stationName_lang")
        }
        val stationAdapter = ArrayAdapter(this, R.layout.spinner_item, stationNames)
        stationAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        stationsSpinner.adapter = stationAdapter
    }
    fun video(dialogs_videoview:VideoView,VideoIndex:Int){
        var videoUri:Uri?=null
        when(VideoIndex){
            0-> videoUri=Uri.parse("android.resource://" + packageName + "/" + R.raw.widget_1)
            1-> videoUri=Uri.parse("android.resource://" + packageName + "/" + R.raw.widget_2)
            2-> videoUri=Uri.parse("android.resource://" + packageName + "/" + R.raw.widget_3)
        }
        dialogs_videoview.setVideoURI(videoUri)
        val mediaController = MediaController(this)// 設置控制器
        mediaController.setAnchorView(dialogs_videoview)
        dialogs_videoview.setMediaController(mediaController)
        dialogs_videoview.setOnCompletionListener {
            dialogs_videoview.start() // 設置播放完成後重播
        }
        dialogs_videoview.start() // 開始播放
    }

}
