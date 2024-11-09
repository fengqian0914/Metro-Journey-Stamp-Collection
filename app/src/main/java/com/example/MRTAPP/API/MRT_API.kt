package com.example.MRTAPP.API

import android.content.Context
import android.util.Log
import com.example.MRTAPP.BuildConfig
import com.example.MRTAPP.R
import com.example.MRTAPP.UI.Home.Info_RecyclerView.TrainInfoList
import com.example.MRTAPP.UI.Home.Info_RecyclerView.TrainInfo_RecyclerViewAdapter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

// MRT_API 類，封裝了獲取 Access Token 和調用 API 的邏輯
class MRT_API(private val context: Context) {
    private val MRT_USERNAME = BuildConfig.MRT_USERNAME
    private val MRT_PASSWORD = BuildConfig.MRT_PASSWORD
    private val BASE_URL = "https://ws.metro.taipei/trtcBeaconBE/"
    private var TrainInfoList = mutableListOf<TrainInfoList>()
    private var recyclerViewAdapter: TrainInfo_RecyclerViewAdapter?=null

    fun ApiCall(type:String,baseUrl: String, startID: String, endID: String, callback: (String?) -> Unit) {
        var soapRequest=""
        when(type){
            "total_time"-> soapRequest="""
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                           xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                           xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                <soap:Body>
                    <GetRecommandRoute xmlns="http://tempuri.org/">
                        <entrySid>${startID}</entrySid>
                        <exitSid>${endID}</exitSid>
                        <username>${MRT_USERNAME}</username>
                        <password>${MRT_PASSWORD}</password>
                    </GetRecommandRoute>
                </soap:Body>
            </soap:Envelope>
        """.trimIndent()
            "Path"-> soapRequest="""
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                           xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                           xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                <soap:Body>
                    <GetRecommandRoute xmlns="http://tempuri.org/">
                        <entrySid>${startID}</entrySid>
                        <exitSid>${endID}</exitSid>
                        <username>${MRT_USERNAME}</username>
                        <password>${MRT_PASSWORD}</password>
                    </GetRecommandRoute>
                </soap:Body>
            </soap:Envelope>
        """.trimIndent()

            "Crowding"-> soapRequest="""
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xmlns:xsd="http://www.w3.org/2001/XMLSchema"
            xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                <soap:Body>
                    <getCarWeightByInfo xmlns="http://tempuri.org/">
                        <userName>${MRT_USERNAME}</userName>
                        <passWord>${MRT_PASSWORD}</passWord>
                    </getCarWeightByInfo>
                </soap:Body>
            </soap:Envelope>
        """.trimIndent()
            "TrackInfo"-> soapRequest="""
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xmlns:xsd="http://www.w3.org/2001/XMLSchema"
            xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                <soap:Body>
                    <getTrackInfo xmlns="http://tempuri.org/">
                        <userName>${MRT_USERNAME}</userName>
                        <passWord>${MRT_PASSWORD}</passWord>
                    </getTrackInfo>
                </soap:Body>
            </soap:Envelope>
        """.trimIndent()

        }

        sendSoapRequest(type,baseUrl, soapRequest, callback)
    }

    fun update_Arrival_time(baseUrl: String,  stationName:String, callback: (JSONArray?) -> Unit) {
        var soapRequest="""
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xmlns:xsd="http://www.w3.org/2001/XMLSchema"
            xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                <soap:Body>
                    <getTrackInfo xmlns="http://tempuri.org/">
                        <userName>${MRT_USERNAME}</userName>
                        <passWord>${MRT_PASSWORD}</passWord>
                    </getTrackInfo>
                </soap:Body>
            </soap:Envelope>
        """.trimIndent()
        val client = OkHttpClient.Builder().build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
        val service = retrofit.create(ApiService::class.java)
        val mediaType = "text/xml; charset=utf-8".toMediaTypeOrNull()
        val requestBody = RequestBody.create(mediaType, soapRequest)
        val call = service.getRecommendedRoute(baseUrl, requestBody)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        try{
                            var newresopse=it.substring(0,it.indexOf("""<?xml version="1.0" encoding="utf-8"?>"""))
                            val jsonArray = JSONArray(newresopse)
                            var name=stationName
                            if(name.indexOf("站")==-1){
                                name+="站"
                            }
                            TrainInfoList.clear() // 清空列表，防重複增加
                            val filteredJsonArray = JSONArray()
                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                if(jsonObject.getString("StationName")==name){
                                    val filteredJsonObject = JSONObject()
                                    filteredJsonObject.put("TrainNumber", jsonObject.getString("TrainNumber"))
                                    filteredJsonObject.put("StationName", jsonObject.getString("StationName"))
                                    filteredJsonObject.put("DestinationName", jsonObject.getString("DestinationName"))
                                    filteredJsonObject.put("countDown", jsonObject.getString("CountDown"))
                                    filteredJsonObject.put("NowDateTime", jsonObject.getString("NowDateTime"))
                                    filteredJsonArray.put(filteredJsonObject)
                                }
                            }
                            callback(filteredJsonArray)
                        }catch (e:Exception){
                        }
                    }
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("API Call", "Error: ${t.message}", t)
                callback(null)
                Log.d("titles","出錯3${t.message}")

            }
        })

    }

    private fun sendSoapRequest(type:String,baseUrl: String, soapRequest: String, callback: (String?) -> Unit) {
        val client = OkHttpClient.Builder().build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
        val service = retrofit.create(ApiService::class.java)
        val mediaType = "text/xml; charset=utf-8".toMediaTypeOrNull()
        val requestBody = RequestBody.create(mediaType, soapRequest)
        val call = service.getRecommendedRoute(baseUrl, requestBody)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        Log.d("SOAP Response", it)
                        when(type){
                            "total_time"->
                                try {
                                    val jsonObject = JSONObject(it)
                                    val path = jsonObject.getString("Path")
                                    var time = jsonObject.getString("Time")
                                    val transferStations = jsonObject.getString("TransferStations")
                                    if (time.toInt() < 60) {
                                        time = time+" "+ context.getString(R.string.minute)
                                    } else {
                                        time = "1小時" + (time.toInt() - 60).toString() + "分"
                                    }
                                    callback(time) // 使用回调返回结果
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                    Log.e("JSON Result", "Error parsing JSON result", e)
                                    callback(null)
                                }
                                ?: run {
                                    callback(null)
                                }
                            "Path"->
                                try {
                                    val jsonObject = JSONObject(it)
                                    val path = jsonObject.getString("Path")
                                    val transferStations = jsonObject.getString("TransferStations")

                                    Log.d("JSON Result", "Path: $path")
                                    Log.d("JSON Result", "TransferStations: $transferStations")
                                    Log.d("JSON Result", "jsonObject: $jsonObject")


//                                    callback("${path} ${transferStations}") // 使用回调返回结果
                                    callback(jsonObject.toString())
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                    Log.e("JSON Result", "Error parsing JSON result", e)
                                    callback(null)
                                }
                                    ?: run {
                                        callback(null)
                                    }
                            "Crowding"->
                                try{


                                    var newresopse=it.substring(1,it.indexOf("""<?xml version="1.0" encoding="utf-8"?>""")).replace("\\\"", "\"")

                                    val jsonObject = JSONObject(newresopse)
                                    Log.d("titles",jsonObject.toString())

//                                    for (i in 0 until jsonArray.length()) {
//                                        val jsonObject = jsonArray.getJSONObject(i)
////                                        val TrainNumber = jsonObject.getString("TrainNumber")
//
//                                        Log.d("titles","\n擁擠度:")
//                                        Log.d("titles",jsonObject.toString())
//
////                                        Log.d("titles","StationName: ${jsonObject.getString("StationName")}  CountDown: ${jsonObject.getString("CountDown")} NowDateTime: ${jsonObject.getString("NowDateTime")}")
////                                        Log.d("titles","\n===============les")
//                                    }
                                }catch (e:Exception){
                                    Log.d("titles","擁擠度：${type} 是 ${e}")

                                }?:run{
                                    callback(null)
                                }
                            "TrackInfo"->
                                try{


                                    var newresopse=it.substring(0,it.indexOf("""<?xml version="1.0" encoding="utf-8"?>"""))
                                    Log.d("titles",it.indexOf("""<?xml version="1.0" encoding="utf-8"?>""").toString())
                                    Log.d("titles",newresopse)
                                    val jsonArray = JSONArray(newresopse)
                                    Log.d("titles","到站資訊"+jsonArray.toString())
                                    for (i in 0 until jsonArray.length()) {
                                        val jsonObject = jsonArray.getJSONObject(i)
//                                        val TrainNumber = jsonObject.getString("TrainNumber")

                                        Log.d("titles","\n到站資訊:")
                                        Log.d("titles",jsonObject.toString())

//                                        Log.d("titles","StationName: ${jsonObject.getString("StationName")}  CountDown: ${jsonObject.getString("CountDown")} NowDateTime: ${jsonObject.getString("NowDateTime")}")
//                                        Log.d("titles","\n===============les")
                                    }
                                }catch (e:Exception){

                                }?:run{
                                    callback(null)
                                }

                            else->{
                                Log.d("titles","其他")

                            }
                        }
                    }
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("API Call", "Error: ${t.message}", t)
                callback(null)
            }
        })
    }
}
