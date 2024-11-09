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
    fun getsaveLanguage(context: Context):String{
        val sharedPreferences = context.getSharedPreferences("Settings",
            Context.MODE_PRIVATE
        )
        val savedLanguage = sharedPreferences.getString("My_Lang", "default_language")
        val savedCountry = sharedPreferences.getString("My_Country", "default_country")
        var language="zh_tw"
        when(savedCountry){
            "TW" -> language="Zh_tw"// zh-TW
            "CN" -> language="Zh_Hans" // zh-CN
            "US"-> language="En"
            "JP"->language="Ja"
            "KR"->language="Ko"
            else ->language="Zh_tw"
        }
        Log.d("savedCountry","savedCountry${savedCountry}language${language}")
        return language.toString()

    }
    fun getsaveLanguage2(context: Context):String{
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
        return language.toString()

    }
    fun getStationName(context: Context, InputName: String,Outputlanguage: String? =null,InputLanguage:String?=null): String {
        val stationJson = loadStationJson(context)
        var InputLanguage_temp="Zh_tw"
        var Outputlanguage_temp:String

        if(Outputlanguage==null){
            Outputlanguage_temp=getsaveCountry(context).toString()
        }else{
            Outputlanguage_temp=Outputlanguage
        }

        if(InputLanguage==null){
            InputLanguage_temp="Zh_tw"
        }else{
            InputLanguage_temp=InputLanguage
        }

        Log.d("getStationName","chineseName${InputName} language${Outputlanguage_temp}")

        stationJson.keys().forEach { lineKey ->
            val lineObject = stationJson.getJSONObject(lineKey)

            lineObject.keys().forEach { stationKey ->
                val stationObject = lineObject.getJSONObject(stationKey)

                val OutputName = stationObject.getString(InputLanguage_temp.toString())
//                Log.d("clickInfo","InputName${InputName} \n" +
//                        "OutputName${OutputName} \n" +
//                        "InputLanguage_temp${InputLanguage_temp} \n" +
//                        "Outputlanguage_temp${Outputlanguage_temp} \n")
                if (OutputName == InputName) {
                    return when (Outputlanguage_temp) {
                        "TW" -> stationObject.getString("Zh_tw")
                        "US" -> stationObject.getString("En")
                        "JP" -> stationObject.getString("Ja")
                        "CN" -> stationObject.getString("Zh_Hans")
                        "KR" -> stationObject.getString("Ko")
                        "Zh_tw" -> stationObject.getString("Zh_tw")
                        "En" -> stationObject.getString("En")
                        "Ja" -> stationObject.getString("Ja")
                        "Zh-Hans" -> stationObject.getString("Zh_Hans")
                        "Ko" -> stationObject.getString("Ko")
                        "CN" -> stationObject.getString("Zh_Hans")
                        "Zh_Hans" -> stationObject.getString("Zh_Hans")
                        else -> OutputName  // 默认返回中文
                    }
                }
            }
        }
        return "Station not found"
    }
    fun getStationName2(context: Context, InputName: String,InputLanguage:String?=null,Outputlanguage: String? =null): String {
        val stationJson = loadStationJson(context)
        var InputLanguage_temp:String
        var Outputlanguage_temp:String




        stationJson.keys().forEach { lineKey ->
            val lineObject = stationJson.getJSONObject(lineKey)

            lineObject.keys().forEach { stationKey ->
                val stationObject = lineObject.getJSONObject(stationKey)



                if (stationObject.getString(InputLanguage) == InputName) {
                    return when (Outputlanguage) {
                        "Zh_tw" -> stationObject.getString("Zh_tw")
                        "En" -> stationObject.getString("En")
                        "Ja" -> stationObject.getString("Ja")
                        "Zh-Hans" -> stationObject.getString("Zh_Hans")
                        "Ko" -> stationObject.getString("Ko")
                        "CN" -> stationObject.getString("Zh_Hans")
                        "Zh_Hans" -> stationObject.getString("Zh_Hans")

                        else -> "not found"  // 默认返回中文
                    }
                }
            }
        }
        return "Station not found"
    }

}