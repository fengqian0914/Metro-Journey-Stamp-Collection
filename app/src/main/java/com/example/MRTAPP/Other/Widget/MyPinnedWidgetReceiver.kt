package com.example.MRTAPP.Other.Widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class MyPinnedWidgetReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // 用戶已確認添加小部件，顯示通知
        Toast.makeText(context, "小部件已添加到桌面", Toast.LENGTH_SHORT).show()

        // 保存狀態到 SharedPreferences，表示用戶已添加過小部件
        val prefs = context.getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean("isWidgetAdded", true)
        editor.apply()
    }
}