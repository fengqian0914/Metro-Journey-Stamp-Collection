package com.example.MRTAPP.Other.Widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.MRTAPP.R

class MyPinnedWidgetReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // 用戶已確認添加小工具，顯示通知
        Toast.makeText(context, context.getString(R.string.widget_added_to_home), Toast.LENGTH_SHORT).show()

        // 保存狀態到 SharedPreferences，表示用戶已添加過小工具
        val prefs = context.getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean("isWidgetAdded", true)
        editor.apply()
    }
}