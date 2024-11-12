package com.example.MRTAPP.Other.Widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import com.example.MRTAPP.API.MRT_API
import com.example.MRTAPP.Other.GetStationNameLanguage
import com.example.MRTAPP.R
import com.example.MRTAPP.UI.Setting.Station.Setting_station
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray

class ItemAppWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
            apiupdate(context, appWidgetId, appWidgetManager)
            // 設定 AlarmManager 每分鐘更新一次 widget
            val intent = Intent(context, ItemAppWidget::class.java).apply {
                action = "ACTION_AUTO_UPDATE_WIDGET"
            }
            val pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 60 * 1000, // 1分鐘首次更新
                60 * 1000, pendingIntent // 每分鐘更新
            )
        }
    }
    override fun onDisabled(context: Context) {
        // 取消定時任務
        val intent = Intent(context, ItemAppWidget::class.java).apply {
            action = "ACTION_AUTO_UPDATE_WIDGET" // 自定義的自動更新小工具動作
        }
        // 創建一個與設定定時器時相同的 PendingIntent
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        // 從系統服務中獲取 AlarmManager 來管理定時任務
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // 取消先前設置的定時任務，停止小工具的自動更新
        alarmManager.cancel(pendingIntent)
        // 確保調用父類的 onDisabled 方法來處理其他邏輯
        super.onDisabled(context)
    }


    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == "ACTION_UPDATE_WIDGET"|| intent.action == "ACTION_AUTO_UPDATE_WIDGET") {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(Intent(context, ItemAppWidget::class.java).component)
            for (appWidgetId in appWidgetIds) {
                apiupdate(context, appWidgetId, appWidgetManager)
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }
    }
    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        // 用戶移除了小工具，重置 SharedPreferences 中的狀態
        val prefs = context.getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean("isWidgetAdded", false)
        editor.apply()
    }
}

internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
    val views = RemoteViews(context.packageName, R.layout.item_app_widget)

    // 設定按鈕點擊事件來更新 Widget
    val updateintent = Intent(context, ItemAppWidget::class.java).apply {
        action = "ACTION_UPDATE_WIDGET"
    }
    val update_pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, updateintent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    val prefs = context.getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE)
    val isWidgetAdded = prefs.getBoolean("isWidgetAdded",false)
    views.setOnClickPendingIntent(R.id.updata_widget_btn, update_pendingIntent)



    val setting_intent = Intent(context, Setting_station::class.java) // 跳回設定小工具
    val setting_pendingIntent = PendingIntent.getActivity(
        context,
        appWidgetId,
        setting_intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    views.setOnClickPendingIntent(R.id.widget_setting_btn, setting_pendingIntent)



    // 更新 Widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

fun apiupdate(context: Context, appWidgetId: Int, appWidgetManager: AppWidgetManager) {
    val MRTApi = MRT_API(context)
    val dynamicUrl = "https://api.metro.taipei/metroapi/TrackInfo.asmx"
    val sharedPreferences = context.getSharedPreferences("widgetStationName", Context.MODE_PRIVATE)
    val stationName = sharedPreferences.getString("widgetStationName", null).toString()

    MRTApi.update_Arrival_time(dynamicUrl, stationName) { response ->
        val jsons = convertJsonArrayToList(response)
        saveDataToSharedPreferences(context, jsons) // 儲存數據到 SharedPreferences
        val listIntent = Intent(context, ListWidgetService::class.java).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            putStringArrayListExtra("train_data", ArrayList(jsons.map { it.toJsonString() }))
        }
        val views = RemoteViews(context.packageName, R.layout.item_app_widget)
        views.setRemoteAdapter(R.id.widget_listview, listIntent)

        val NowDateTime=jsons[0].NowDateTime
        Start_Staion_Title(views,context)
        views.setTextViewText(R.id.Widget_updatetime,NowDateTime)
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_listview)
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}

fun Start_Staion_Title(views: RemoteViews,context: Context) {
    val sharedPreferences = context.getSharedPreferences("widgetStationName", Context.MODE_PRIVATE)
    val stationName = sharedPreferences.getString("widgetStationName", null).toString()
    val stationNameId = sharedPreferences.getString("widgetStationName_Id", null).toString()
    val stationName_en = sharedPreferences.getString("widgetStationName_en", null).toString()
    val GetLanguage = GetStationNameLanguage(context)
    views.setTextViewText(R.id.start_station_title_zh,stationName)
    if(GetLanguage.getsaveLanguage2(context)=="Zh_tw"){
        views.setTextViewText(R.id.start_station_title_en,
            GetLanguage.getStationName2(context,stationName,"Zh_tw","En"))

    }else{
        views.setTextViewText(R.id.start_station_title_en,GetLanguage.getStationName(context,stationName))
    }
    views.setTextViewText(R.id.train_Info_Station_Id_widget_title,stationNameId)
    val backgroundResource = when {
        stationNameId.contains("R22A") -> R.drawable.circle_layout_bg_r22a
        stationNameId.contains("G03A") -> R.drawable.circle_layout_bg_g03a
        stationNameId.contains("BL") -> R.drawable.circle_layout_bg_bl
        stationNameId.contains("BR") -> R.drawable.circle_layout_bg_br
        stationNameId.contains("R" )-> R.drawable.circle_layout_bg_r
        stationNameId.contains( "G" )-> R.drawable.circle_layout_bg_g
        stationNameId.contains("O" )-> R.drawable.circle_layout_bg_o
        stationNameId.contains("Y") -> R.drawable.circle_layout_bg_y
        else -> R.drawable.circle_layout_bg_bl
    }
    views.setInt(R.id.train_Info_Station_Id_widget_title, "setBackgroundResource", backgroundResource)


}

fun convertJsonArrayToList(jsonArray: JSONArray?): List<Widget_Trainlist> {
    val list = mutableListOf<Widget_Trainlist>()
    jsonArray?.let {
        for (i in 0 until it.length()) {
            val jsonObject = it.getJSONObject(i)
            val trainInfo = Widget_Trainlist(
                TrainNumber = jsonObject.getString("TrainNumber"),
                StationName = jsonObject.getString("StationName"),
                DestinationName = jsonObject.getString("DestinationName"),
                countDown = jsonObject.getString("countDown"),
                NowDateTime = jsonObject.getString("NowDateTime")
            )
            list.add(trainInfo)
        }
    }
    return list
}

// 儲存數據到 SharedPreferences
private fun saveDataToSharedPreferences(context: Context, data: List<Widget_Trainlist>) {
    val sharedPreferences = context.getSharedPreferences("MRTWidgetPrefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    val json = Gson().toJson(data)
    editor.putString("trainData", json)
    editor.apply()
}


