package com.example.MRTAPP.Other

import android.content.Context
import android.util.Log
import org.json.JSONObject
import java.io.InputStream

class GetStationNameLanguage(context: Context) {
    fun loadStationJson(context: Context): JSONObject {
        val inputStream: InputStream = context.assets.open("mrt_language.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        return JSONObject(jsonString)
    }
    fun getsaveCountry(context: Context):String{
        val sharedPreferences = context.getSharedPreferences("Settings",
            Context.MODE_PRIVATE
        )
        val savedLanguage = sharedPreferences.getString("My_Lang", "default_language")
        val savedCountry = sharedPreferences.getString("My_Country", "default_country")
        var language="zh_tw"
        when(savedCountry){
            "TW" -> language="Zh_tw"// zh-TW
            "CN" -> language="Zh-Hans" // zh-CN
            "US"-> language="En"
            "JP"->language="Ja"
            "KR"->language="Ko"
            else ->language="Zh_tw"
        }
        Log.d("savedCountry","savedCountry${savedCountry}language${language}")
        return savedCountry.toString()

    }
    fun getStationName(context: Context, chineseName: String,language: String? =null): String {
        val stationJson = loadStationJson(context)
        var languages:String
        if(language==null){
            languages=getsaveCountry(context).toString()
        }else{
            languages=language
        }
        Log.d("getStationName","chineseName${chineseName} language${languages}")

        stationJson.keys().forEach { lineKey ->
            val lineObject = stationJson.getJSONObject(lineKey)

            lineObject.keys().forEach { stationKey ->
                val stationObject = lineObject.getJSONObject(stationKey)
                val zhTwName = stationObject.getString("Zh_tw")

                if (zhTwName == chineseName) {
                    return when (languages) {
                        "TW" -> stationObject.getString("Zh_tw")
                        "US" -> stationObject.getString("En")
                        "JP" -> stationObject.getString("Ja")
                        "CN" -> stationObject.getString("Zh_Hans")
                        "KR" -> stationObject.getString("Ko")
                        else -> zhTwName  // 默认返回中文
                    }
                }
            }
        }
        return "Station not found"
    }
}