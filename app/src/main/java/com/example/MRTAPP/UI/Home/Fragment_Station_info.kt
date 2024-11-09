package com.example.MRTAPP.UI.Home

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.VideoView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.MRTAPP.API.TDX_API
import com.example.MRTAPP.Other.GetStationNameLanguage
import com.example.MRTAPP.R
import com.example.MRTAPP.UI.Cameras.MRT_Station_item
import com.example.MRTAPP.UI.Home.Info_RecyclerView.StationInfoList
import com.example.MRTAPP.UI.Home.Info_RecyclerView.StationInfo_RecylerViewAdapter
import com.example.MRTAPP.UI.Home.Info_RecyclerView.TrainInfoList
import com.example.MRTAPP.UI.Home_fragment
import com.example.MRTAPP.UI.Star.Achievement_RecyclerViewAdapter
import com.example.MRTAPP.UI.Star.Achievement_popup_RecyclerViewAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject
import java.lang.reflect.Type

/**
 * A simple [Fragment] subclass.
 * Use the [Fragment_Station_info.newInstance] factory method to
 * create an instance of this fragment.
 */

class Fragment_Station_info : Fragment() {
    // 更具描述性的參數名稱
    private var stationParam1: String? = null
    private var stationParam2: String? = null
    private lateinit var stationName: String
    private var exitList = mutableListOf<Map<String, Any>>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var StationInfoRecyclerViewAdapter: StationInfo_RecylerViewAdapter
    private var StationInfoList = mutableListOf<TrainInfoList>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            stationParam1 = it.getString(STATION_PARAM1)
            stationParam2 = it.getString(STATION_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val views = inflater.inflate(R.layout.fragment__station_info, container, false)

        val sharedPreferences = requireContext().getSharedPreferences("stationInfo", Context.MODE_PRIVATE)
        val stationName = sharedPreferences.getString("stationName", null).toString()
        var startID:String?=null
        val getlanguage=GetStationNameLanguage(requireContext())
        val getsavelanguage=getlanguage.getsaveLanguage(requireContext())
        val stationInfoTextBtn=views.findViewById<ImageView>(R.id.stationInfoTextBtn)
        val languageText=getlanguage.getsaveLanguage2(requireContext())
        if(stationName=="板橋(環狀)"){
            startID = "Y16"
        }else{
            startID = getStationIdForScame(requireContext(), stationName).toString()
        }

        stationInfoTextBtn.setOnClickListener {
            getStationInfoText(requireContext(),views,startID,languageText)
        }

        val tdxApi = TDX_API(requireContext())
        tdxApi.callApiExit(startID, stationName, "exit") { response ->
            if (response != null) {
                try {
                    exitList = response
                    activity?.runOnUiThread {
                        val response_Array = JSONArray(response)
                        var StationID = ""
                        var StationName_Zh_tw = ""
                        var StationName_en = ""
                        var StationName_Exit_number = ""
                        recyclerView = views.findViewById(R.id.ExitRecyclerView)
                        var ExitListDataArray = JSONArray()
                        for (i in 0 until response_Array.length()) {
                            val jsonObject = response_Array.getJSONObject(i)
                            val LocationDescription = jsonObject.getString("LocationDescription")
                            val Stair = jsonObject.getBoolean("Stair")
                            val Escalator = jsonObject.getInt("Escalator")
                            val Elevator = jsonObject.getBoolean("Elevator")
                            StationID = jsonObject.getString("StationID")
                            if(StationID=="Y16"){
                                StationName_Zh_tw = "板橋(環狀)"
                            }else{
                                StationName_Zh_tw = jsonObject.getString("StationName_zh")
                            }
                            if(getsavelanguage=="Zh_tw"){
                                StationName_en = jsonObject.getString("StationName_En")
                            }else{
                                StationName_en = getlanguage.getStationName2(requireContext(),jsonObject.getString("StationName_En"),"En",getsavelanguage)
                            }
                            StationName_Exit_number=jsonObject.getString("ExitNumber")
                            val ExitListDataObject = JSONObject()
                            ExitListDataObject.put("ExitId", StationName_Exit_number)
                            ExitListDataObject.put("ExitName", LocationDescription)
                            ExitListDataObject.put("ExitType1", Stair)
                            ExitListDataObject.put("ExitType2", Escalator)
                            ExitListDataObject.put("ExitType3", Elevator)
                            ExitListDataObject.put("StationName", StationName_Zh_tw)
                            ExitListDataArray.put(ExitListDataObject)
                        }
                        val data = convertJsonArrayToList(ExitListDataArray)
                        try {
                            StationInfoRecyclerViewAdapter = StationInfo_RecylerViewAdapter(requireContext(), data, StationName_Zh_tw)
                            recyclerView.layoutManager = LinearLayoutManager(requireContext())
                            recyclerView.adapter = StationInfoRecyclerViewAdapter
                            StationInfoList.addAll(StationInfoList)
                            StationInfoRecyclerViewAdapter.notifyDataSetChanged()
                        } catch (e: Exception) {
                            Log.d("title", "Errorrecy:${e}\n ${data}")
                        }

                        try {
                            views.findViewById<TextView>(R.id.station_ID).text = StationID

                            views.findViewById<TextView>(R.id.station_name).text = StationName_Zh_tw
                            views.findViewById<TextView>(R.id.station_name_en).text = StationName_en
                        } catch (e: Exception) {
                            Log.d("titlesy", "textess${e.toString()}")
                        }
                        val station_ID_view=views.findViewById<LinearLayout>(R.id.station_ID_layout)
                        var bgcolor=StationIDbackground(StationID)
                        station_ID_view.backgroundTintList= ColorStateList.valueOf(bgcolor)
                    }
                } catch (e: Exception) {
                    Log.d("title", "Error: $e")
                }
            }

        }
        return views
    }

    private fun getStationInfoText(context: Context,views: View?,Id:String,Language:String) {
        val json=context.assets.open("station_info.json").bufferedReader().use { it.readText() }
        val jsonObject=JSONObject(json)
        val StationData=jsonObject.getJSONObject(Id)
        val StationText=StationData.getString(Language)
        showdialog(context,StationText,context.getString(R.string.Station_informationText))
    }
    private fun showdialog(context: Context,StationText:String,Title:String) {
        val dialog= Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.activity_dialogs)
        dialog.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context, R.color.transparent)))
        val dialog_title=dialog.findViewById<TextView>(R.id.dialog_wait_title)
        val dialog_msg=dialog.findViewById<TextView>(R.id.dialog_wait_msg)
        val dialog_close=dialog.findViewById<ImageView>(R.id.dialog_close)
        val dialogs_videoview=dialog.findViewById<VideoView>(R.id.videoView)
        val dialog_bg=dialog.findViewById<LinearLayout>(R.id.dialog_bg)
        val dialog_video_layout=dialog.findViewById<LinearLayout>(R.id.video_btn_layout)
        val Previousbtn=dialog.findViewById<Button>(R.id.dialog_Previous_btn)
        val Nextbtn=dialog.findViewById<Button>(R.id.dialog_Next_btn)
        dialog_bg.setBackgroundResource(R.color.startgradient)
        dialog_title.text=Title
        dialog_msg.text=StationText
        dialog_msg.gravity = Gravity.LEFT
        dialog_msg.setTextColor(Color.WHITE)
        dialogs_videoview.visibility=View.GONE
        dialog_video_layout.visibility=View.GONE
        Previousbtn.visibility=View.GONE
        Nextbtn.visibility=View.GONE
        dialog_close.setOnClickListener {
            dialog.dismiss()

        }
        dialog.show()
    }
    private fun StationIDbackground(stationID: String):Int{
        return when {
            stationID.contains("R22A") -> ContextCompat.getColor(requireContext(),R.color.mrt_route_R22A)
            stationID.contains("G03A") -> ContextCompat.getColor(requireContext(),R.color.mrt_route_G03A)

            stationID.contains("BL") -> ContextCompat.getColor(requireContext(),R.color.mrt_route_BL)
            stationID.contains("BR") -> ContextCompat.getColor(requireContext(),R.color.mrt_route_BR)

            stationID.contains("R") -> ContextCompat.getColor(requireContext(),R.color.mrt_route_R)
            stationID.contains("G") -> ContextCompat.getColor(requireContext(),R.color.mrt_route_G)
            stationID.contains("O") -> ContextCompat.getColor(requireContext(),R.color.mrt_route_O)
            stationID.contains("Y") -> ContextCompat.getColor(requireContext(),R.color.mrt_route_Y)
            else -> ContextCompat.getColor(requireContext(),R.color.mrt_route_BL)
        }

    }

    fun convertJsonArrayToList(jsonArray: JSONArray?): List<StationInfoList> {
        val list = mutableListOf<StationInfoList>()
        jsonArray?.let {
            for (i in 0 until it.length()) {
                val jsonObject = it.getJSONObject(i)
                val stationInfo = StationInfoList(
                    ExitId = jsonObject.getInt("ExitId"),
                    ExitName = jsonObject.getString("ExitName"),
                    ExitType1 = jsonObject.getBoolean("ExitType1"),
                    ExitType2 = jsonObject.getInt("ExitType2"),
                    ExitType3 = jsonObject.getBoolean("ExitType3")
                )
                list.add(stationInfo)
            }
        }
        return list
    }

    fun getStationIdForScame(context: Context, scame: String): String? {
        return try {
            val jsonString = context.assets.open("mrt_time_number.json").bufferedReader().use { it.readText() }
            val type: Type = object : TypeToken<Map<String, Map<String, String>>>() {}.type
            val stationMap: Map<String, Map<String, String>> = Gson().fromJson(jsonString, type)
            stationMap.entries.find { it.value["SCAME"] == scame }?.key
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val textView = view.findViewById<TextView>(R.id.station_name)
        textView.text = stationName
    }

    fun setStationName(name: String) {
        stationName = name
    }

    companion object {
        private const val STATION_PARAM1 = "station_param1"
        private const val STATION_PARAM2 = "station_param2"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Fragment_Station_info().apply {
                arguments = Bundle().apply {
                    putString(STATION_PARAM1, param1)
                    putString(STATION_PARAM2, param2)
                }
            }
    }
}
