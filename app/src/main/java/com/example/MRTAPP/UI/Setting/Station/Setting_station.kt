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
        val BRText=getString(R.string.route_BR)
        val GText=getString(R.string.route_G)
        val RText=getString(R.string.route_R)
        val BLText=getString(R.string.route_BL)
        val OText=getString(R.string.route_O)
        val YText=getString(R.string.route_Y)
        val routes = listOf(
            "BR ${BRText}", "G ${GText}",
            "R ${RText}", "BL ${BLText}", "O ${OText}",
            "Y ${YText}"
        )

        // 使用自定义的布局来创建 ArrayAdapter
        val routesAdapter = ArrayAdapter(this, R.layout.spinner_item, routes)
        routesAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        routesSpinner.adapter = routesAdapter

        stationItem(routeIndex, stationsSpinner)

        routesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                stationItem(position, stationsSpinner)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // 当没有选中任何选项时
            }
        }

        stationsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                widgetStationName = parent.getItemAtPosition(position).toString()
                val confirmText = findViewById<TextView>(R.id.confirm_text)
                confirmText.text = widgetStationName
                stationIndex = position
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // 当没有选中任何选项时
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
            editor.putString("widgetStationName_Id", strArray[0])
            editor.putString("widgetStationName", strArray[1])
            editor.putString("widgetStationName_en", strArray[2])
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
        val setting_station_widgetInfo_btn = findViewById<Button>(R.id.setting_station_widgetInfo_btn)
        setting_station_widgetInfo_btn.setOnClickListener {
            showdialog()


        }

        appWidgetHost = AppWidgetHost(this, 1) // 初始化 AppWidgetHost
    }

    private fun pinAppWidget() {
        val appWidgetManager = getSystemService(AppWidgetManager::class.java)
        val myProvider = ComponentName(this, ItemAppWidget::class.java)

        Log.d("pinAppWidget", "1 - Start pinAppWidget")

        // 檢查裝置是否支持固定小部件功能
        if (appWidgetManager.isRequestPinAppWidgetSupported) {
            Log.d("pinAppWidget", "2 - Device supports pinning widgets")

            // 檢查 Android 版本是否 >= O (API 26)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d("pinAppWidget", "3 - Android version: ${Build.VERSION.SDK_INT}")

                val pinnedWidgetCallbackIntent = Intent(this, MyPinnedWidgetReceiver::class.java)
                val pinnedWidgetCallbackPendingIntent: PendingIntent = PendingIntent.getBroadcast(
                    this,
                    0,
                    pinnedWidgetCallbackIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                val intentSender = pinnedWidgetCallbackPendingIntent.intentSender

                // 請求固定小部件，傳遞 IntentSender 作為回調
                val success = appWidgetManager.requestPinAppWidget(
                    myProvider,
                    null,  // 可選：傳遞 Bundle 用於小部件配置
                    pinnedWidgetCallbackPendingIntent
                )

                if (success) {
                    Log.d("pinAppWidget", "4 - Pin widget request sent successfully")
                    Toast.makeText(this, this.getString(R.string.confirm_widget_request), Toast.LENGTH_SHORT).show()

                    // 記錄已添加小部件的狀態
                    val prefs = this.getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE)
                    val editor = prefs.edit()
                    editor.putBoolean("isWidgetAdded", true)
                    editor.apply()
                } else {
                    Log.d("pinAppWidget", "5 - Pin widget request failed")
                    Toast.makeText(this, this.getString(R.string.request_failed), Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.d("pinAppWidget", "6 - Android version too low: ${Build.VERSION.SDK_INT}")
            }
        } else {
            Log.d("pinAppWidget", "7 - Device does not support pinning widgets")
            Toast.makeText(this, this.getString(R.string.widget_not_supported), Toast.LENGTH_SHORT).show()
        }

        Log.d("pinAppWidget", "8 - End pinAppWidget")
    }

    fun stationItem(routeIndex: Int, stationsSpinner: Spinner) {
        routeText = when (routeIndex) {
            0 -> "BR"
            1 -> "G"
            2 -> "R"
            3 -> "BL"
            4 -> "O"
            5 -> "Y"
            else -> ""
        }

        val jsonString = this.assets?.open("mrt_language.json")?.bufferedReader().use { it?.readText() }
        val json = JSONObject(jsonString)[routeText].toString()
        val jsons = JSONObject(json)
        val jsonkeys = jsons.keys()
        val stationNames = mutableListOf<String>()
        val GetLanguage = GetStationNameLanguage(this)
        val InputLanguage=GetLanguage.getsaveLanguage(this)
        Log.d("InputLanguage","${InputLanguage}")
        while (jsonkeys.hasNext()) {
            val key = jsonkeys.next()
            val stationObject = jsons.getJSONObject(key)
            val stationName = stationObject.getString("Zh_tw")
            val stationName_en = stationObject.getString("En")
            val stationName_lang = stationObject.getString(InputLanguage)

            stationNames.add("$key\n$stationName\n$stationName_lang")
        }

        val stationAdapter = ArrayAdapter(this, R.layout.spinner_item, stationNames)
        stationAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        stationsSpinner.adapter = stationAdapter

        // 設置下拉選單的高度，只顯示10個選項，超過則滾動
        stationsSpinner.viewTreeObserver.addOnGlobalLayoutListener {
            try {
                val popup = Spinner::class.java.getDeclaredField("mPopup")
                popup.isAccessible = true
                val popupWindow = popup.get(stationsSpinner) as ListPopupWindow
                popupWindow.height = (60 * 3).toInt()  // 每行 60dp，顯示10個選項
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        Log.d("station_datas", "json: $json")
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
    fun video(dialogs_videoview:VideoView,VideoIndex:Int){
        var videoUri:Uri?=null
        when(VideoIndex){
            0-> videoUri=Uri.parse("android.resource://" + packageName + "/" + R.raw.widget_1)
            1-> videoUri=Uri.parse("android.resource://" + packageName + "/" + R.raw.widget_2)
            2-> videoUri=Uri.parse("android.resource://" + packageName + "/" + R.raw.widget_3)

        }
        dialogs_videoview.setVideoURI(videoUri)
        // 設置控制器
        val mediaController = MediaController(this)
        mediaController.setAnchorView(dialogs_videoview)
        dialogs_videoview.setMediaController(mediaController)

        // 設置播放完成後重播
        dialogs_videoview.setOnCompletionListener {
            dialogs_videoview.start() // 重播
        }
        // 開始播放
        dialogs_videoview.start()

    }

}
