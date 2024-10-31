package com.example.MRTAPP.UI.Home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.MRTAPP.API.MRT_API

import com.example.MRTAPP.R
import com.example.MRTAPP.UI.Home.Info_RecyclerView.TrainInfoList
import com.example.MRTAPP.UI.Home.Info_RecyclerView.TrainInfo_RecyclerViewAdapter
import org.json.JSONArray
import java.util.Timer
import java.util.TimerTask

class Fragment_Train_info : Fragment() {
    private var TrainParam1: String? = null
    private var TrainParam2: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var trainInfoRecyclerViewAdapter: TrainInfo_RecyclerViewAdapter
    private val trainInfoList = mutableListOf<TrainInfoList>()
    private var timer_10s: Timer? = null
    private var timer_1s: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            TrainParam1 = it.getString(TRAIN_PARAM1)
            TrainParam2 = it.getString(TRAIN_PARAM1)
        }
    }

    override fun onResume() {
        super.onResume()
        // 當 Fragment 可見時開始計時
        // startTimer() // 可以選擇保留這行，如果你希望在 Fragment 可見時自動開始計時
        val sharedPreferences = requireContext().getSharedPreferences("stationInfo",
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString("ErrorStation",null)
        editor.apply()
    }

    override fun onPause() {
        super.onPause()
        stopTimer() // 當 Fragment 不可見時停止計時

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment__train_info, container, false)


        val sharedPreferences = requireContext().getSharedPreferences("stationInfo",
            Context.MODE_PRIVATE
        )
        val stationName = sharedPreferences.getString("stationName", null).toString()
        Log.d("ErrorStation",stationName)
        val errorText=view.findViewById<TextView>(R.id.ErrorStation)

        if(stationName=="板新"||stationName=="中原"||stationName=="橋和"||stationName=="板橋(環狀)"){
            errorText.visibility=View.VISIBLE
            errorText.text=getString(R.string.errorStation)
            view.findViewById<RecyclerView>(R.id.Train_Station_Recyclerview).visibility=View.GONE
            errorText.isSelected = true

            Log.d("ErrorStation","1")
        }else if(stationName=="板橋(環狀)"||stationName=="新埔民生"||stationName=="頭前庄"||
            stationName=="幸福"||stationName=="新北產業園區"){
            errorText.visibility=View.VISIBLE
            Log.d("ErrorStation","2")
            errorText.isSelected = true

            errorText.text=getString(R.string.errorStation2)
            view.findViewById<RecyclerView>(R.id.Train_Station_Recyclerview).visibility=View.VISIBLE

            recyclerView = view.findViewById(R.id.Train_Station_Recyclerview)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            trainInfoRecyclerViewAdapter =
                TrainInfo_RecyclerViewAdapter(requireContext(), trainInfoList)
            recyclerView.adapter = trainInfoRecyclerViewAdapter
        }
        else{

            view.findViewById<TextView>(R.id.ErrorStation).visibility=View.GONE
            view.findViewById<RecyclerView>(R.id.Train_Station_Recyclerview).visibility=View.VISIBLE
            recyclerView = view.findViewById(R.id.Train_Station_Recyclerview)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            trainInfoRecyclerViewAdapter =
                TrainInfo_RecyclerViewAdapter(requireContext(), trainInfoList)
            recyclerView.adapter = trainInfoRecyclerViewAdapter
        }





        return view
    }

    fun startTimer() {
        // 確保計時器不會重複啟動
        if (timer_10s == null) {
            val sharedPreferences = requireContext().getSharedPreferences("stationInfo",
                Context.MODE_PRIVATE
            )
            val stationName = sharedPreferences.getString("stationName", null).toString()
            val dynamicUrl = "https://api.metro.taipei/metroapi/TrackInfo.asmx"

            // 設定 10 秒的計時器
            timer_10s = Timer()
            val timerTask_10s = object : TimerTask() {
                override fun run() {
                    Api10s(view!!, dynamicUrl, stationName)
                }
            }
            timer_10s?.schedule(timerTask_10s, 0, 10000)
        }

        // 設定 1 秒的倒數計時器
        if (timer_1s == null) {
            timer_1s = Timer()
            val timerTask_1s = object : TimerTask() {
                override fun run() {
                    activity?.runOnUiThread {
                        val timeTextView = view?.findViewById<TextView>(R.id.Train_arrives_times)
                        try {
                            val timeText = timeTextView?.text.toString().toInt()
                            Log.d("titles", "時間倒數${timeText}")
                            timeTextView?.text = if (timeText <= 0) "10" else (timeText - 1).toString()
                        } catch (e: Exception) {
                            Log.d("titles", "倒數錯誤" + e.toString())
                        }
                    }
                }
            }
            timer_1s?.schedule(timerTask_1s, 0, 1000)
        }
    }

    fun stopTimer() {
        timer_10s?.cancel()
        timer_10s = null // 記得設置為 null，方便重新啟動
        timer_1s?.cancel()
        timer_1s = null // 記得設置為 null，方便重新啟動
    }

    fun Api10s(view: View, dynamicUrl: String, stationName: String) {
        val MRTApi = MRT_API(requireContext())
        MRTApi.update_Arrival_time(dynamicUrl, stationName) { response ->
            val jsons = convertJsonArrayToList(response)
            Log.d("MRT_API_data", jsons.toString())
            trainInfoList.clear() // 清空之前的資料
            trainInfoList.addAll(jsons) // 添加新的資料
            trainInfoRecyclerViewAdapter.notifyDataSetChanged() // 更新 RecyclerView
        }
    }

    fun convertJsonArrayToList(jsonArray: JSONArray?): List<TrainInfoList> {
        val list = mutableListOf<TrainInfoList>()
        jsonArray?.let {
            for (i in 0 until it.length()) {
                val jsonObject = it.getJSONObject(i)
                val trainInfo = TrainInfoList(
                    TrainNumber = jsonObject.getString("TrainNumber"),
                    StationName = jsonObject.getString("StationName"),
                    DestinationName = jsonObject.getString("DestinationName"),
                    countDown = jsonObject.getString("countDown"),
                    NowDateTime = jsonObject.getString("NowDateTime")
                )
                list.add(trainInfo)
            }
        }
        return list
    }

    companion object {
        private const val TRAIN_PARAM1 = "Train_param1"
        private const val TRAIN_PARAM2 = "Train_param2"
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Fragment_Train_info().apply {
                arguments = Bundle().apply {
                    putString(TRAIN_PARAM1, param1)
                    putString(TRAIN_PARAM2, param2)
                }
            }
    }
}