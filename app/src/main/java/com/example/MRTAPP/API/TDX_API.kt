package com.example.MRTAPP.API

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.MRTAPP.BuildConfig
import com.example.MRTAPP.R
import com.example.MRTAPP.UI.Home.MainActivity
import com.example.MRTAPP.UI.Home_fragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException
import java.lang.reflect.Type


data class TDX_AccessTokenResponse(val access_token: String, val expires_in: Long, val token_type: String)

class TDX_API(private val context: Context) {
    val clientId= BuildConfig.CLIENT_ID
    val clientSecret = BuildConfig.CLIENT_SECRET
    private val client = OkHttpClient()
    private val gson = Gson()
    private var accessToken: String? = null

    // 獲取Access Token的方法
    fun getAccessToken(callback: (String?) -> Unit) {
        // 取得 SharedPreferences 實例
        val sharedPreferences = context.getSharedPreferences("AccessToken", Context.MODE_PRIVATE)

        // 編輯 SharedPreferences
        val editor = sharedPreferences.edit()

        val url = "https://tdx.transportdata.tw/auth/realms/TDXConnect/protocol/openid-connect/token"
        val formBody = FormBody.Builder()
            .add("grant_type", "client_credentials")
            .add("client_id", clientId)
            .add("client_secret", clientSecret)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("獲取 Access Token 失敗，原因：${e.message}")
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        println("獲取 Access Token 失敗，原因：HTTP ${response.code}")
                        callback(null)
                    } else {
                        val responseBody = response.body?.string()
                        val tokenResponse = gson.fromJson(responseBody, TDX_AccessTokenResponse::class.java)
                        accessToken = tokenResponse.access_token
                        println("成功獲取 Access Token")
                        println("token:${accessToken}")
                        callback(accessToken)
                    }
                }
            }
        })
    }

    // 使用 Access Token 調用 API 的方法
    fun callApi(destinationStationId: String, originStationId: String, type:String, callback: (MutableList<Double>?) -> Unit) {
        val sharedPreferences = context.getSharedPreferences("tdx", Context.MODE_PRIVATE)
        accessToken = sharedPreferences.getString("AccessToken", null)
        println("callback:${callback}")
        if (accessToken == null) {
            getAccessToken { token ->
                if (token != null) {
                    callApi(destinationStationId, originStationId, type,callback)
                } else {
                    println("呼叫 API 失敗，原因：無法獲取 Access Token")
                    callback(null)
                }
            }
        } else {

            Log.d("title", "AccessToken:${accessToken}")
            when(type) {
                "price" ->
                    ApiCallPrice(destinationStationId, originStationId, accessToken!!, callback)
//                "exit" ->
//                    ApiCallExit(destinationStationId, accessToken!!, callback)
            }
        }
    }
    fun callApiExit(destinationStationId: String, originStationId: String, type:String, callback: (MutableList<Map<String, Any>>?) -> Unit) {
        val sharedPreferences = context.getSharedPreferences("tdx", Context.MODE_PRIVATE)
        accessToken = sharedPreferences.getString("AccessToken", null)
        println("callback:${callback}")
        if (accessToken == null) {
            getAccessToken { token ->
                if (token != null) {
                    callApiExit(destinationStationId, originStationId, type,callback)
                } else {
                    println("呼叫 API 失敗，原因：無法獲取 Access Token")
                    callback(null)
                }
            }
        } else {

            Log.d("title", "AccessToken:${accessToken}")
            when(type) {
//                "price" ->
//                    ApiCallPrice(destinationStationId, originStationId, accessToken!!, callback)
                "exit" ->
                    ApiCallExit(destinationStationId, accessToken!!, callback)
            }
        }
    }
    private fun ApiCallExit(StationID: String, token: String, callback: (MutableList<Map<String, Any>>?) -> Unit) {
        val urltype:String
        if(StationID.substring(0,1)=="Y"){
            urltype="NTMC"
        }else{
            urltype="TRTC"
        }
        val url = "https://tdx.transportdata.tw/api/basic/v2/Rail/Metro/StationExit/${urltype}" +
                "?%24filter=StationID eq '$StationID' "+
                "&%24format=JSON"
        Log.d("title",url)
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Content-Type", "application/json")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("呼叫 API 失敗，原因：${e.message}")
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {

                response.use {
                    val exitList = mutableListOf<Map<String, Any>>()
                    if (!it.isSuccessful) {
                        println("呼叫 API 失敗，原因：HTTP ${response.code}")
                        if (response.code == 429) {
                            println("error2")
                            (context as? Activity)?.runOnUiThread {
                                val dialog_title="錯誤"
                                val dialogs_msg="資料請求速度過快，請稍後再試"
                                showdialog(dialog_title,dialogs_msg)

                                Toast.makeText(context, context.getString(R.string.operation_too_fast), Toast.LENGTH_LONG).show()
                            }
                        }
                        callback(null)
                    } else {
                        Log.d("titles","EXIT"+response.toString())
                        val responseBody = response.body?.string()
                        if (responseBody != null) {
                            try {
                                val jsonArray = JSONArray(responseBody)
                                val pattern = "Exit\\s*[A-Za-z]*\\s*(\\d+)".toRegex()  // 提取 Exit 後的數字，並允許 M1 類型
                                val tempExitList = mutableListOf<Map<String, Any>>()  // 暫存未排序的列表

                                for (i in 0 until jsonArray.length()) {
                                    val firstObject = jsonArray.getJSONObject(i)
                                    val locationDescription = firstObject.getString("LocationDescription")
                                    val StationID = firstObject.getString("StationID")
                                    val StationName = firstObject.getJSONObject("StationName")
                                    val StationName_zh = StationName.getString("Zh_tw")
                                    val StationName_En = StationName.getString("En")
                                    val stair = firstObject.getBoolean("Stair")

                                    val escalator = firstObject.getInt("Escalator")
                                    val elevator = firstObject.getBoolean("Elevator")
                                    val ExitName = firstObject.getJSONObject("ExitName").getString("En")

                                    // 提取 Exit 後的數字，並將其轉換為 Int，用於排序
                                    val matchResult = pattern.find(ExitName)
                                    val exitNumber = matchResult?.groupValues?.get(1)?.toIntOrNull() ?: 1  // 如果無法匹配，設為最大值
//                                    val exitNumber = firstObject.getString("ExitID")
                                    Log.d("ExitId",exitNumber.toString())

                                    val exitMap = mapOf(
                                        "StationID" to StationID,
                                        "StationName_zh" to StationName_zh,
                                        "StationName_En" to StationName_En,
                                        "ExitName" to ExitName,
                                        "ExitNumber" to exitNumber,  // 用於排序的數字
                                        "LocationDescription" to locationDescription,
                                        "Stair" to stair,
                                        "Escalator" to escalator,
                                        "Elevator" to elevator
                                    )

                                    tempExitList.add(exitMap)
                                }
// 根據 ExitNumber 排序
                                val sortedExitList = tempExitList.sortedBy { it["ExitNumber"] as Int }

// 將排序後的資料加入 exitList
                                exitList.addAll(sortedExitList)

// 測試輸出排序結果
                                sortedExitList.forEach { exitMap ->
                                    Log.d("title", "輸出結果 站名：${exitMap}")
                                }
                                Log.d("title", "輸出結果：${sortedExitList}")

                            } catch (e: Exception) {
                                println("Error:${e}")
                            }
                        } else {
                            println("Price:呼叫 API 失敗1")
                        }
                        callback(exitList)
                    }
                }
            }
        })

    }

    private fun showdialog(dialogTitle: String, dialogsMsg: String) {
        val dialog= Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.activity_dialogs)
        dialog.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context, R.color.transparent)))
        val dialog_title=dialog.findViewById<TextView>(R.id.dialog_wait_title)
        val dialog_msg=dialog.findViewById<TextView>(R.id.dialog_wait_msg)
        val dialog_close=dialog.findViewById<ImageView>(R.id.dialog_close)
        dialog_title.text=dialogTitle
        dialog_msg.text=dialogsMsg
        dialog_close.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
        dialog.show()
    }

    private fun ApiCallPrice(destinationStationId: String, originStationId: String,token: String, callback: (MutableList<Double>?) -> Unit) {
        val urltype:String
        if(destinationStationId.substring(0,1)=="Y" || originStationId.substring(0,1)=="Y"){
            urltype="NTMC"
            val url = "https://11f252d4-ebba-44c2-8c42-a897af956c2d.mock.pstmn.io/API_Price_Y"
            var SID=destinationStationId
            var EndID=originStationId
            if(destinationStationId.substring(0,1)!="Y" && originStationId.substring(0,1)=="Y" ){
                val temp=destinationStationId
                SID=originStationId
                EndID=destinationStationId
            }
            val startID = getSIDForStation(context, SID).toString()
            val endID = getSIDForStation(context, EndID).toString()

            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("呼叫 API 失敗，原因：${e.message}")
                    callback(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        val priceList = mutableListOf<Double>()

                        if (!it.isSuccessful) {
                            println("呼叫 API 失敗，原因：HTTP ${response.code}")
                            if (response.code == 429) {
                                println("error2")
                                (context as? Activity)?.runOnUiThread {
                                    Toast.makeText(context, context.getString(R.string.operation_too_fast), Toast.LENGTH_LONG).show()
                                }
                            }
                            callback(null)
                        } else {
                            val responseBody = response.body?.string()
                            if (responseBody != null) {
                                try {
                                    Log.d("logs", "response: $responseBody")
                                    // 解析为 JSONObject
                                    val jsonArray = JSONArray(responseBody)
                                    Log.d("logs", "jsonArray: $jsonArray")
                                    Log.d("logs", "sssssss: $startID $endID")

                                    // 提取价格信息
                                    for (i in 0 until jsonArray.length()) {
                                        val item = jsonArray.getJSONObject(i)
                                        if(item.getString("起站")==startID){
//                                          Log.d("logs", "destinationStationId: $startID")
                                            if(item.getString("訖站")==endID) {
                                                Log.d("logs","起站"+item.getString("起站"))
                                                Log.d("logs","訖站"+item.getString("訖站"))
                                                Log.d("logs","全票"+item.getDouble("全票"))

                                                val price_1 = item.getDouble("全票")
                                                val price_2 =
                                                    item.getDouble("敬老")
                                                val price_3 = item.getDouble("臺北")
                                                priceList.add(price_1)
                                                priceList.add(price_2)
                                                priceList.add(price_3)
                                            }
                                            i+130
                                        }else{
//                                            Log.d("logs","起站"+item.getString("起站"))
//                                            Log.d("logs","訖站"+item.getString("訖站"))


                                        }

                                    }
                                    Log.d("tttt",priceList.toString())

                                } catch (e: Exception) {
                                    println("Error: ${e.message}")
                                }
                            } else {
                                println("Price: 呼叫 API 失敗2")
                            }
                            callback(priceList)
                        }
                    }
                }
            })

        }else{
            urltype="TRTC"

        val url = "https://tdx.transportdata.tw/api/basic/v2/Rail/Metro/ODFare/${urltype}" +
                "?%24filter=DestinationStationID eq '$destinationStationId' and OriginStationID eq '$originStationId'" +
                "&%24format=JSON"

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Content-Type", "application/json")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("呼叫 API 失敗，原因：${e.message}")
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {

                response.use {
                    val priceList = mutableListOf<Double>()

                    if (!it.isSuccessful) {
                        println("呼叫 API 失敗，原因：HTTP ${response.code}")
                        if (response.code == 429) {
                            println("error2")
                            (context as? Activity)?.runOnUiThread {
                                Toast.makeText(context, context.getString(R.string.operation_too_fast), Toast.LENGTH_LONG).show()
                            }
                        }
                        callback(null)
                    } else {
                        val responseBody = response.body?.string()
                        if (responseBody != null) {
                            try {
                                val jsonArray = JSONArray(responseBody)
                                val firstObject = jsonArray.getJSONObject(0)
                                val faresArray = firstObject.getJSONArray("Fares")
                                val fare = faresArray.getJSONObject(0)
                                val price = fare.getDouble("Price")

                                priceList.add(price)
                                priceList.add(price * 0.4)
                                priceList.add(price * 0.6)
                                Log.d("title", priceList.toString())
                            } catch (e: Exception) {
                                println("Error:${e}")
                            }
                        } else {
                            println("Price:呼叫 API 失敗3")
                        }
                        callback(priceList)
                    }
                }
            }
        })
        }
    }
    fun getSIDForStation(context: Context, stationId: String): String? {
        return try {
            // 讀取 JSON 檔案
            val jsonString = context.assets.open("mrt_time_number.json").bufferedReader().use { it.readText() }

            // 定義 JSON 資料的類型
            val type: Type = object : TypeToken<Map<String, Home_fragment.StationInfo>>() {}.type

            // 使用 Gson 解析 JSON 資料
            val stationMap: Map<String, Home_fragment.StationInfo> = Gson().fromJson(jsonString, type)

            // 根據 stationId 查找對應的 SID
            stationMap[stationId]?.SCAME
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
