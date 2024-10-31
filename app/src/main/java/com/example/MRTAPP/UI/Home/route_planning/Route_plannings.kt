package com.example.MRTAPP.UI.Home.route_planning

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.MRTAPP.API.MRT_API
import com.example.MRTAPP.Data.getSIDForStation
import com.example.MRTAPP.Other.GetStationNameLanguage
import com.example.MRTAPP.R
import com.example.MRTAPP.UI.Home.MainActivity
import org.json.JSONObject

class route_plannings : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var routeTransferRecyclerViewAdapter: Route_transfer_RecylerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_planning)

        // 初始化 RecyclerView
        recyclerView = findViewById(R.id.tranfet_RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 獲取傳遞的起始站和終點站
        val startStation = intent.getStringExtra("startStation").toString()
        val endStation = intent.getStringExtra("EndStation").toString()

        // 隱藏標題欄
        supportActionBar?.hide()

        // 呼叫 API 獲取路線資料
        API_Path(startStation, endStation)
        findViewById<LinearLayout>(R.id.goback).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun API_Path(startStation: String, endStation: String) {
        val getSIDFun = getSIDForStation()
        val startID = getSIDFun.getSID(this, startStation).toString()
        val endID = getSIDFun.getSID(this, endStation).toString()
        val mrtApi = MRT_API(this)
        val dynamicUrl = "https://ws.metro.taipei/trtcBeaconBE/RouteControl.asmx"
        val type = "Path"

        mrtApi.ApiCall(type, dynamicUrl, startID, endID) { response ->
            response?.let {
                Log.d("Route_planning", response)

                // 解析 JSON 回應
                val responseArray_Path = JSONObject(response).getString("Path")
                val responseArray_TransferStations = JSONObject(response).getString("TransferStations")

                // 將路徑和轉乘站分割成列表
                val pathStations = responseArray_Path.split("-").filter { it.isNotEmpty() }
                val transferStations = responseArray_TransferStations.split("-").filter { it.isNotEmpty() }

                // 將路徑和轉乘站分段
                val sectionedPath = convertPathToSectionedList(pathStations, transferStations)

                // 設置適配器並附加到 RecyclerView
                routeTransferRecyclerViewAdapter = Route_transfer_RecylerViewAdapter(this, sectionedPath)
                recyclerView.adapter = routeTransferRecyclerViewAdapter

                // 確認生成的分段結果
                Log.d("sectionedPath", "分段結果如下:")
                sectionedPath.forEachIndexed { index, routeTransferList ->
                    Log.d("sectionedPath", "段落 ${index + 1}: 轉乘站 - ${routeTransferList.TransferStation}")
                    routeTransferList.Transition_station.forEach { station ->
                        Log.d("sectionedPath", "    - ${station.stationName}")
                    }
                }

            } ?: run {
                Log.e("ApiCall", "Failed to fetch data")
            }
        }
    }

    // 將路徑和轉乘站分段
    private fun convertPathToSectionedList(pathStations: List<String>, transferStations: List<String>): List<Route_transfer_list> {
        val sections = mutableListOf<Route_transfer_list>()
        var startIdx = 0
        val GetLanguage = GetStationNameLanguage(this)
        var endIdx:Int
        var Circular_Line_BanqiaoNext=false
        var Circular_Line_Banqiao=false
        for ((index, station) in transferStations.withIndex()) {


            if(index < transferStations.size - 1){
                if(station=="板橋"&&Circular_Line_Banqiao==false){
                    Circular_Line_Banqiao=true
                    endIdx= pathStations.indexOf(transferStations[index+1])
                }else{
                    endIdx= pathStations.indexOf(transferStations[index+1])

                }
                Log.d("endIdx","板橋 ${endIdx}")
            }else{
                endIdx=pathStations.size

            }




            val segmentStations = if(Circular_Line_Banqiao==true &&Circular_Line_BanqiaoNext==false) {
                Circular_Line_BanqiaoNext=true
                Log.d("sectionedPath","1 station${station}")

                pathStations.subList(startIdx , endIdx)

            }else if(Circular_Line_BanqiaoNext==true){
                Log.d("sectionedPath","2 station${station}")
                Circular_Line_BanqiaoNext=false
                Circular_Line_Banqiao=false
                pathStations.subList(startIdx+2, endIdx)
            }else{
                Log.d("sectionedPath","3 station${station}")

                pathStations.subList(startIdx + 1, endIdx)
            }

            Log.d("sectionedPath","segmentStations${segmentStations}")


            val TransferStation_translate_zhtw=GetLanguage.getStationName(this,station,"Zh_tw")

            val transitionStations = segmentStations.map {
                Route_transition_station_list(GetLanguage.getStationName(this,it))
            }
            val TransferStation_translate=GetLanguage.getStationName(this,station)

            sections.add(Route_transfer_list(
                TransferStation = "${TransferStation_translate}-${TransferStation_translate_zhtw}",
                Transition_station = transitionStations
            ))

            startIdx = endIdx
            Log.d("sectionsArray","sections${sections}")
        }

        return sections
    }
}
