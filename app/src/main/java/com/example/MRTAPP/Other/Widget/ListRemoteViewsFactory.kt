package com.example.MRTAPP.Other.Widget

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.example.MRTAPP.Other.GetStationNameLanguage
import com.example.MRTAPP.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream

class ListRemoteViewsFactory(private val context: Context, private val intent: Intent) : RemoteViewsService.RemoteViewsFactory {

    private var trainList: List<Widget_Trainlist> = emptyList()

    init {
        // 嘗試從 Intent 中獲取 train_data
        loadTrainDataFromIntent()
    }

    private fun loadTrainDataFromIntent() {

        trainList = loadDataFromSharedPreferences(context)

        Log.d("trainList2",trainList.toString())
    }

    override fun onCreate() {
        // 初始化不需要特別處理
    }

    override fun onDataSetChanged() {
        Log.d("ListRemoteViewsFactory", "onDataSetChanged 被觸發")

        // 獲取更新的 train_data
        trainList = loadDataFromSharedPreferences(context)

        loadTrainDataFromIntent()
//        trainList = emptyList()

        Log.d("ListRemoteViewsFactory", "接收到的資料: ${trainList}")
        Log.d("trainList1",trainList.toString())

    }

    override fun getCount(): Int = trainList.size

    override fun getItemId(position: Int): Long = position.toLong()

    override fun hasStableIds(): Boolean = true

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewAt(position: Int): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_list_item)
        val trainInfo = trainList[position]
        val GetLanguage = GetStationNameLanguage(context)
        val languages=GetLanguage.getsaveLanguage2(context)
        views.setTextViewText(R.id.Train_Info_Station_Name, trainInfo.DestinationName.dropLast(1))
        var text=""
        when (languages) {
            "Zh_tw" -> {
                text = "To"
            }
            "Zh-Hans" -> {
                text = "往"
            }
            "En" -> {
                text = "To"
            }
            "Ja" -> {
                text = "行き"
            }
            "Ko" -> {
                text = "향해"
            }
            else -> {
                text = "To"
            }
        }
        views.setTextViewText(R.id.To_language,text)
        when{
            trainInfo.countDown.contains("列車進站中")-> {
                views.setFloat(R.id.Train_Info_Station_Time, "setTextSize", 14f)
                views.setTextViewText(R.id.Train_Info_Station_Time, context.getString(R.string.Train_Approaching))

            }
            trainInfo.countDown.contains("營運時間已過")-> {
                views.setFloat(R.id.Train_Info_Station_Time, "setTextSize", 14f)
                views.setTextViewText(R.id.Train_Info_Station_Time, context.getString(R.string.End_Service))

            }
            trainInfo.countDown.contains("資料擷取中")-> {
                views.setFloat(R.id.Train_Info_Station_Time, "setTextSize", 14f)
                views.setTextViewText(R.id.Train_Info_Station_Time, context.getString(R.string.Loading_Data))

            }
            trainInfo.countDown.contains(":")-> {
                views.setFloat(R.id.Train_Info_Station_Time, "setTextSize", 16f)
                views.setTextViewText(R.id.Train_Info_Station_Time, trainInfo.countDown)
            }
        }
        val backgroundResource = when {
            formatDestinationName(trainInfo.DestinationName).contains("R22A") -> R.drawable.circle_layout_bg_r22a
            formatDestinationName(trainInfo.DestinationName).contains("G03A") -> R.drawable.circle_layout_bg_g03a
            formatDestinationName(trainInfo.DestinationName).contains("BL") -> R.drawable.circle_layout_bg_bl
            formatDestinationName(trainInfo.DestinationName).contains("BR") -> R.drawable.circle_layout_bg_br
            formatDestinationName(trainInfo.DestinationName).contains("R") -> R.drawable.circle_layout_bg_r
            formatDestinationName(trainInfo.DestinationName).contains("G") -> R.drawable.circle_layout_bg_g
            formatDestinationName(trainInfo.DestinationName).contains("O") -> R.drawable.circle_layout_bg_o
            formatDestinationName(trainInfo.DestinationName).contains("Y") -> R.drawable.circle_layout_bg_y
            else -> R.drawable.circle_layout_bg_bl
        }
        views.setInt(R.id.Train_Info_Station_Id_widget_1, "setBackgroundResource", backgroundResource)

        when{
            trainInfo.DestinationName.contains("南港展覽館")->{
                views.setFloat(R.id.Train_Info_Station_Id_widget_1, "setTextSize", 14f)
                views.setFloat(R.id.train_Info_Station_Id_widget_2, "setTextSize", 14f)
                views.setViewVisibility(R.id.train_Info_Station_Id_widget_2, View.VISIBLE)
                views.setTextViewText(R.id.Train_Info_Station_Id_widget_1,"BL23")
                views.setTextViewText(R.id.train_Info_Station_Id_widget_2,"BR24")
                Log.d("DestinationName","有${trainInfo.DestinationName}")
                views.setInt(R.id.Train_Info_Station_Id_widget_1, "setBackgroundResource", R.drawable.circle_layout_bg_bl)
                views.setInt(R.id.train_Info_Station_Id_widget_2, "setBackgroundResource", R.drawable.circle_layout_bg_br)

            }!trainInfo.DestinationName.contains("南港展覽館")->{
                views.setFloat(R.id.Train_Info_Station_Id_widget_1, "setTextSize", 16f)
                views.setFloat(R.id.train_Info_Station_Id_widget_2, "setTextSize", 14f)
                views.setViewVisibility(R.id.train_Info_Station_Id_widget_2, View.GONE)
                Log.d("DestinationName","沒有${trainInfo.DestinationName}")
                views.setTextViewText(R.id.Train_Info_Station_Id_widget_1,formatDestinationName(trainInfo.DestinationName))

            }
        }
        Log.d("newback","trainInfo.DestinationName${trainInfo.DestinationName}\n" +
                "ormatDestinationName(trainInfo.DestinationName${formatDestinationName(trainInfo.DestinationName)}")
        var englishName = ""

        if(languages=="Zh_tw"){
            englishName = GetLanguage.getStationName2(context,trainInfo.DestinationName.dropLast(1),"Zh_tw","En")

        }else{
            englishName = GetLanguage.getStationName(context, trainInfo.DestinationName.dropLast(1))
        }

//            getStationName(context, trainInfo.DestinationName.dropLast(1), "En")
        Log.d("englishName","languages${languages}\nenglishName${trainInfo.DestinationName.dropLast(1)}" +
                " englishName:${englishName}")


        views.setTextViewText(R.id.Train_Info_Station_Name_en,englishName)

        // 根據站名設定對應的背景資源


        // 設置對應的背景資源

        Log.d("trainInfo","position:${position}  StationName${trainInfo.StationName} " +
                "DestinationName${trainInfo.DestinationName}" +
                "countDown${trainInfo.countDown}")
        return views
    }

    override fun getViewTypeCount(): Int = 1

    override fun onDestroy() {
        trainList = emptyList()
    }
    private fun convertJsonStringToList(jsonString: String?): List<Widget_Trainlist> {
        val list = mutableListOf<Widget_Trainlist>()
        jsonString?.let {
            val jsonArray = JSONArray(it)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val trainInfo = Widget_Trainlist(
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

    // 從 SharedPreferences 獲取數據
    private fun loadDataFromSharedPreferences(context: Context): List<Widget_Trainlist> {
        val sharedPreferences = context.getSharedPreferences("MRTWidgetPrefs", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("trainData", null)

        return if (json != null) {
            val type = object : TypeToken<List<Widget_Trainlist>>() {}.type
            Gson().fromJson(json, type)
        } else {
            emptyList() // 返回空列表
        }
    }
    private fun formatDestinationName(destinationName: String): String {
        return when {
            destinationName.contains("新北投站") -> "R22A"
            destinationName.contains("小碧潭站") -> "G03A"
            destinationName.contains("南港展覽館站") -> "BL23"
            destinationName.contains("動物園站") -> "BR01"
            destinationName.contains("淡水站") -> "R28"
            destinationName.contains("象山站") -> "R02"
            destinationName.contains("北投站") -> "R22"
            destinationName.contains("大安站") -> "R05"
            destinationName.contains("松山站") -> "G19"
            destinationName.contains("新店站") -> "G01"
            destinationName.contains("台電大樓站") -> "G08"
            destinationName.contains("蘆洲站") -> "O54"
            destinationName.contains("迴龍站") -> "O21"
            destinationName.contains("南勢角站") -> "O01"
            destinationName.contains("頂埔站") -> "BL01"
            destinationName.contains("亞東醫院站") -> "BL05"
            destinationName.contains("新北產業園區站") -> "Y20"
            destinationName.contains("大坪林站") -> "Y07"

            destinationName.contains("七張站") -> "G03"
            destinationName.contains("中和站") -> "Y12"
            destinationName.contains("昆陽站") -> "BL21"
            else -> destinationName
        }
    }
    fun loadStationJson(context: Context): JSONObject {
        val inputStream: InputStream = context.assets.open("mrt_language.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        return JSONObject(jsonString)
    }
    fun getStationName(context: Context, chineseName: String, language: String): String {
        val stationJson = loadStationJson(context)

        stationJson.keys().forEach { lineKey ->
            val lineObject = stationJson.getJSONObject(lineKey)

            lineObject.keys().forEach { stationKey ->
                val stationObject = lineObject.getJSONObject(stationKey)
                val zhTwName = stationObject.getString("Zh_tw")

                if (zhTwName == chineseName) {
                    return when (language) {
                        "En" -> stationObject.getString("En")
                        "Ja" -> stationObject.getString("Ja")
                        "zh-Hans" -> stationObject.getString("zh-Hans")
                        "ko" -> stationObject.getString("ko")

                        else -> zhTwName  // 默认返回中文
                    }
                }
            }
        }
        return "Station not found"
    }

}
