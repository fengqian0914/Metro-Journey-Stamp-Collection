// Home_fragment.kt
package com.example.MRTAPP.UI

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.example.MRTAPP.API.MRT_API
import com.example.MRTAPP.API.TDX_API
import com.example.MRTAPP.Other.GetStationNameLanguage
import com.example.MRTAPP.R
import com.example.MRTAPP.UI.Home.station_data
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import com.example.MRTAPP.UI.Home.MapView
import com.example.MRTAPP.UI.Home.route_planning.route_plannings

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Home_fragment : Fragment(), MapView.StationTextListener {
    private val BASE_URL = "https://ws.metro.taipei/trtcBeaconBE/"

    private var param1: String? = null
    private var param2: String? = null
    private var nightMode: Boolean = false
    private var Price_grade_index: Int = 0
    private var start_code: String = ""
    private var end_code: String = ""
    private var priceList = mutableListOf<Double>()
    private var views: View? = null

    lateinit var imageView: SubsamplingScaleImageView

    // XML
    // `ApiService` 介面已經放在 Data 目錄中的 ApiService.kt 文件中

    // `GetRecommandRouteResponse` 類別已經放在 Data 目錄中的 GetRecommandRouteResponse.kt 文件中

    //XML

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_fragment, container, false)
        views = view

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 創建並顯示確認對話框
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.backdialog_title))
                    .setIcon(R.drawable.logo)
                    .setMessage(getString(R.string.backdialog_msg))
                    .setPositiveButton(getString(R.string.backdialog_y)) { dialog, _ ->
                        // 使用者點選「是」時，關閉對話框並執行返回操作
                        dialog.dismiss()
                        requireActivity().finish() // 結束 Activity，關閉應用程式

                    }
                    .setNegativeButton(getString(R.string.backdialog_n)) { dialog, _ ->
                        // 使用者點選「否」時，僅關閉對話框
                        dialog.dismiss()
                    }
                    .show()
            }
        })
        val startStation = view.findViewById<TextView>(R.id.start_station)
        val endStation = view.findViewById<TextView>(R.id.end_station)
        val mapView = view.findViewById<MapView>(R.id.mapView)
        val GetLanguage = GetStationNameLanguage(requireContext())
        val InputLanguage=GetLanguage.getsaveLanguage(requireContext())

        mapView.setStationTexts(startStation.text.toString(), endStation.text.toString())

        view.findViewById<ImageView>(R.id.change_btn).setOnClickListener {
            val tempstr = startStation.text
            startStation.text = endStation.text
            endStation.text = tempstr.toString()
            val temp=start_code
            start_code = end_code
            end_code=temp
        }
        view.findViewById<Button>(R.id.return_station_btn).setOnClickListener {
            startStation.text = ""
            endStation.text = ""
            view.findViewById<TextView>(R.id.Home_Arrivaltime).text=""
            view.findViewById<TextView>(R.id.Home_Price).text=""
            view.findViewById<TextView>(R.id.Home_Price_type).text=""
            mapView.returnbtn()

        }

        view.findViewById<ImageView>(R.id.startInfo).setOnClickListener {
//            val stationname = startStation.text
//            val stationname = GetLanguage.getStationName(requireContext(),startStation.text.toString())

            val stationname = GetLanguage.getStationName2(requireContext(),startStation.text.toString(),InputLanguage,"Zh_tw")
//            val stationname = startStation.text

            Log.d("clickInfo","輸入語言${InputLanguage} \n" +
                    "輸入文字${startStation.text}\n" +
                    "輸出語言${"Zh_tw"} \n" +
                    "輸出文字${stationname}")



//            val stationname = startStation.text
            if (stationname.isEmpty()){
                Toast.makeText(context,context?.getString(R.string.please_select_station_name),Toast.LENGTH_LONG).show()
            }else{
                stationdata_layout(stationname.toString())

            }
        }
        view.findViewById<ImageView>(R.id.endInfo).setOnClickListener {
//            val stationname = endStation.text

            val stationname = GetLanguage.getStationName2(requireContext(), endStation.text.toString(),InputLanguage,"Zh_tw")

            if (stationname.isEmpty()){
                Toast.makeText(context,context?.getString(R.string.please_select_station_name),Toast.LENGTH_LONG).show()
            }else{
                stationdata_layout(stationname.toString())

            }

        }
        mapView.setStationTextListener(this)

        view.findViewById<ImageView>(R.id.home_grade_left).setOnClickListener {
            Price_grade_index--
            if (Price_grade_index == -1) {
                Price_grade_index = 2
            }
            view.findViewById<TextView>(R.id.Home_Price).text = priceList[Price_grade_index].toString() + " "+requireContext().getString(R.string.dollar)
            when (Price_grade_index) {
                0 -> {
                    view?.findViewById<TextView>(R.id.Home_Price_type)?.text = requireContext().getString(R.string.unanimous_vote)
                }
                1 -> {
                    view?.findViewById<TextView>(R.id.Home_Price_type)?.text = requireContext().getString(R.string.Discount_tickets)
                }
                2 -> {
                    view?.findViewById<TextView>(R.id.Home_Price_type)?.text = requireContext().getString(R.string.Taipei_City_Children_Ticket)
                }
            }
        }
        view.findViewById<ImageView>(R.id.home_grade_right).setOnClickListener {
            Price_grade_index++
            if (Price_grade_index == 3) {
                Price_grade_index = 0
            }
            view.findViewById<TextView>(R.id.Home_Price).text = priceList[Price_grade_index].toInt().toString() + " " + requireContext().getString(R.string.dollar)
            when (Price_grade_index) {
                0 -> {
                    view?.findViewById<TextView>(R.id.Home_Price_type)?.text = requireContext().getString(R.string.unanimous_vote)
                }
                1 -> {
                    view?.findViewById<TextView>(R.id.Home_Price_type)?.text = requireContext().getString(R.string.Discount_tickets)
                }
                2 -> {
                    view?.findViewById<TextView>(R.id.Home_Price_type)?.text = requireContext().getString(R.string.Taipei_City_Children_Ticket)
                }
            }
        }
        view.findViewById<Button>(R.id.route_planning_btn).setOnClickListener {
            val startName = startStation.text
            val EndName = endStation.text


            if (startName.isEmpty()||EndName.isEmpty()){
                Toast.makeText(context,context?.getString(R.string.please_select_station_name),Toast.LENGTH_LONG).show()
            }else{
                val intent = Intent(requireContext(), route_plannings::class.java)

                intent.putExtra("startStation", start_code)
                intent.putExtra("EndStation", end_code)
                startActivity(intent)
            }





        }
        return view
    }

    data class StationInfo(
        val SCAME: String,
        val SID: String
    )

    fun getSIDForStation(context: Context, stationId: String): String? {
        return try {
            // 讀取 JSON 檔案
            val jsonString = context.assets.open("mrt_time_number.json").bufferedReader().use { it.readText() }

            // 定義 JSON 資料的類型
            val type: Type = object : TypeToken<Map<String, StationInfo>>() {}.type

            // 使用 Gson 解析 JSON 資料
            val stationMap: Map<String, StationInfo> = Gson().fromJson(jsonString, type)

            // 根據 stationId 查找對應的 SID
            stationMap[stationId]?.SID
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun TDX_API_Price(view: View, Start_Station_code: String, End_Station_code: String) {
        val tdxApi = TDX_API(requireContext())
        tdxApi.getAccessToken { response ->
            if (response != null) {
                println("API 回應：$response")
                Log.d("title","LoginLayout:${response}")
                // 取得 SharedPreferences 實例
                val tdx_sharedPreferences = context?.getSharedPreferences("tdx", Context.MODE_PRIVATE)

                // 編輯 SharedPreferences
                val tdx_editor =  tdx_sharedPreferences?.edit()
                tdx_editor?.putString("AccessToken",response)

                tdx_editor?.apply()

            } else {
                println("呼叫 API 失敗")
            }
        }
        tdxApi.callApi(Start_Station_code, End_Station_code,"price") { response->
            if (response != null) {
                println("API 回應：$response")
                try {
                    priceList=response
                    activity?.runOnUiThread {
                        views?.findViewById<TextView>(R.id.Home_Price)?.text = priceList[0].toInt().toString()  + " "+ requireContext().getString(R.string.dollar)
                        when (Price_grade_index) {
                            0 -> {
                                view?.findViewById<TextView>(R.id.Home_Price_type)?.text = requireContext().getString(R.string.unanimous_vote)
                            }
                            1 -> {
                                view?.findViewById<TextView>(R.id.Home_Price_type)?.text = requireContext().getString(R.string.Discount_tickets)
                            }
                            2 -> {
                                view?.findViewById<TextView>(R.id.Home_Price_type)?.text = requireContext().getString(R.string.Taipei_City_Children_Ticket)
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.d("title", "Error: $e")
                }
            } else {
                println("Price:呼叫 API 失敗2-1")
            }
        }
        MRT_TIME(Start_Station_code, End_Station_code)
    }

    private fun MRT_TIME(startName: String, endName: String) {

        val startID = getSIDForStation(requireContext(), startName).toString()
        val endID = getSIDForStation(requireContext(), endName).toString()
        val MRTApi = MRT_API(requireContext())
        var dynamicUrl = "https://ws.metro.taipei/trtcBeaconBE/RouteControl.asmx"
        var type="total_time"
        MRTApi.ApiCall(type,dynamicUrl, startID, endID) { response ->
            views?.findViewById<TextView>(R.id.Home_Arrivaltime)?.text = response ?: "無法獲取時間"
            Log.d("ApiCall","h type${type} dynamicUrl${dynamicUrl} startID${startID} endID${endID} startName${startName} endName${endName}")

            Log.d("ApiCall","h response${response}")
        }

//        dynamicUrl = "https://api.metro.taipei/metroapi/CarWeight.asmx"
//        type="Crowding"
//        MRTApi.ApiCall(type,dynamicUrl, startID, endID) { response ->
//            println("Crowding:${response}")
////            views?.findViewById<TextView>(R.id.Home_Arrivaltime)?.text = response ?: "無法獲取時間"
//        }

        dynamicUrl = "https://api.metro.taipei/metroapi/TrackInfo.asmx"
        type="TrackInfo"
//        MRTApi.ApiCall(type,dynamicUrl, startID, endID) { response ->
//            println("Crowding:${response}")
////            views?.findViewById<TextView>(R.id.Home_Arrivaltime)?.text = response ?: "無法獲取時間"
//        }




    }
    private fun stationdata_layout(name: String) {
        val intent = Intent(requireContext(), station_data::class.java)
        intent.putExtra("name", name)
        val sharedPreferences = requireContext().getSharedPreferences("stationInfo", Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        editor.putString("stationName",name)
        editor.apply()

        startActivity(intent)
    }

    override fun onStationTextChanged(Station: String, location: String, code: String) {
        Log.d("title", "main_onStationTextChanged")
        activity?.runOnUiThread {
            if (location == "start") {
                view?.findViewById<TextView>(R.id.start_station)?.text = Station
                start_code = code
                priceList.clear()
            } else {
                view?.findViewById<TextView>(R.id.end_station)?.text = Station
                end_code = code
                view?.let {
                    TDX_API_Price(it, start_code, end_code)
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Home_fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}
