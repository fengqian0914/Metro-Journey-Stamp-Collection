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

class MRTArrivalReceiver : BroadcastReceiver() {
    private val CHANNEL_ID = "MRT_CHANNEL_ID"
    private val notificationId = 1

    override fun onReceive(context: Context, intent: Intent) {
        val endName = intent.getStringExtra("EndName") ?: "未知目的地"
        val time = intent.getStringExtra("Time") ?: "未知時間"

        // Log 到站信息
        Log.d("MRTArrivalReceiver", "列車到達: $endName, 預計到達時間: $time")


        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)) // 震动500毫秒
        }
        // 發送到站通知
        showMRTArrivalNotification(context, endName, time)
    }

    private fun showMRTArrivalNotification(context: Context, endName: String, time: String) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.logos)
            .setContentTitle("捷運到站提醒")
            .setContentText("列車即將抵達!!!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                return
            }
            notify(notificationId, builder.build())
        }
    }
}
