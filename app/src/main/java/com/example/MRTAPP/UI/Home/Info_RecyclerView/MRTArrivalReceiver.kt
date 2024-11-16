package com.example.MRTAPP

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MRTAPP.Other.GetStationNameLanguage

class MRTArrivalReceiver : BroadcastReceiver() {
    private val CHANNEL_ID = "MRT_CHANNEL_ID"
    private val notificationId = 1
    override fun onReceive(context: Context, intent: Intent) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator())
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)) // 震動0.5秒
        // 啟動到站通知
        showMRTArrivalNotification(context)
    }
    private fun showMRTArrivalNotification(context: Context) {
        val getLanguage= GetStationNameLanguage(context)
        val language=getLanguage.getsaveLanguage2(context)
        var title=""
        var text=""
        when (language) {
            "Zh_tw" -> {
                title = "到站提醒" // 繁體中文
                text = "列車即將抵達!!!"
            }
            "Zh-Hans" -> {
                title = "到站提醒" // 簡體中文
                text = "列车即将到达!!!"
            }
            "En" -> {
                title = "Arrival Reminder" // 英文
                text = "The train is approaching!!!"
            }
            "Ja" -> {
                title = "到着通知" // 日文
                text = "列車がまもなく到着します!!!"
            }
            "Ko" -> {
                title = "도착 알림" // 韓文
                text = "열차가 곧 도착합니다!!!"
            }
            else -> {
                title = "到站提醒" // 默認顯示（可以是繁體中文）
                text = "列車即將抵達!!!"
            }
        }
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.setting_station)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
                return
            notify(notificationId, builder.build())
        }
    }
}
