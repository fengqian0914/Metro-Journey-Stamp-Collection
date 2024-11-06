package com.example.MRTAPP.Data

import android.content.Context
import com.example.MRTAPP.UI.Home_fragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class getSIDForStation {
    fun getSID(context: Context, stationId: String): String? {
        return try {
            // 讀取 JSON 檔案
            val jsonString = context.assets.open("mrt_time_number.json").bufferedReader().use { it.readText() }

            // 定義 JSON 資料的類型
            val type: Type = object : TypeToken<Map<String, Home_fragment.StationInfo>>() {}.type

            // 使用 Gson 解析 JSON 資料
            val stationMap: Map<String, Home_fragment.StationInfo> = Gson().fromJson(jsonString, type)

            // 根據 stationId 查找對應的 SID
            stationMap[stationId]?.SID
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}