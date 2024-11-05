package com.example.MRTAPP.UI.Star

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.MRTAPP.R
import com.example.MRTAPP.UI.Cameras.MRT_Station_item
import com.example.MRTAPP.UI.Cameras.recyclerViewAdapter
import com.example.MRTAPP.UI.Mall.Product_RecyclerViewAdapter
import com.example.MRTAPP.UI.Star_Fragment
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject
import kotlin.math.max


class Achievement_RecyclerViewAdapter(
    private val getActivity: Star_Fragment,
    private val achievementList: List<Achievement_List>
) : RecyclerView.Adapter<Achievement_RecyclerViewAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.activity_achievement_recycler_view_adapter,
            parent,
            false
        )
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val achievement = achievementList[position]
        val levelData = achievement.level

        // 綁定等級數據
        updateData(holder, levelData, achievement.existsquantity.toLong(),position)




        holder.Achievement_CardView.setOnClickListener {
            val toastMessage = holder.itemView.context.getString(R.string.Guest_toast_msg)

            val sharedPreferences = holder.itemView.context.getSharedPreferences("Login", Context.MODE_PRIVATE)
            val Guest = sharedPreferences?.getBoolean("Guest",false)
            if(Guest==true){
                Toast.makeText(holder.itemView.context, toastMessage, Toast.LENGTH_LONG).show()
            }else{
                PopUpwindows(holder.itemView.context, achievement.station,holder)

            }
        }
    }

    private fun PopUpwindows(context: Context,stationObject:JSONObject,holder: MyViewHolder) {
            val window= PopupWindow(context)
            val views = LayoutInflater.from(context).inflate(R.layout.activity_popup_route, null)
            window.contentView=views
            val dismiss_Btn=views.findViewById<Button>(R.id.dismiss_btn)
            val recyclerView=views.findViewById<RecyclerView>(R.id.route_recyclerView)
            val testData=ArrayList<MRT_Station_item>()

            window.isFocusable = true
            window.update()
            views.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                    window.dismiss()
                    true
                } else {
                    false
                }
            }

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



            for (key in stationObject.keys()) {
                 val stationInfo = stationObject.getJSONObject(key)
                 val stationName = stationInfo.getString("Name")
                 val exists = stationInfo.getBoolean("exists")
                 val status = if (exists) "已完成" else "未完成"

                 // 添加到 testData 列表
                 testData.add(MRT_Station_item(key, stationName, status))
            }

                        // 完成資料讀取後設置 RecyclerView 的適配器
            val layoutManager = LinearLayoutManager(context)
            val adapter = Achievement_popup_RecyclerViewAdapter(testData)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter





            val layout=holder.Achievement_CardView
            val All_width = Resources.getSystem().displayMetrics.widthPixels
            val All_height_margin = Resources.getSystem().displayMetrics.heightPixels
            val window_layout=LayoutInflater.from(context).inflate(R.layout.item_route,null)

            window_layout.findViewById<TextView>(R.id.item_station_name).setTextColor(Color.RED)
            window.width=All_width.toInt()
            window.height=All_height_margin.toInt()
            dismiss_Btn.setOnClickListener {
                window.dismiss()
            }
            window.showAtLocation(layout, Gravity.CENTER ,0,100) // 最後兩個參數為 x 和 y 偏移量


    }

    private fun updateData(holder: MyViewHolder, levelData: Map<String, Any>, existsquantity: Long,position:Int) {
        val achievement = achievementList[position]
        holder.progressBar.scaleY = 10f

        val bronzeData = levelData["Bronze"] as? Map<String, Any> ?: emptyMap()
        val silverData = levelData["Silver"] as? Map<String, Any> ?: emptyMap()
        val goldData = levelData["Gold"] as? Map<String, Any> ?: emptyMap()

        val bronzeDemand = bronzeData["demand"] as? Long ?: 0
        val silverDemand = silverData["demand"] as? Long ?: 0
        val goldDemand = goldData["demand"] as? Long ?: 0

        val levelImageUrl = achievement.Image
        var imageUrl = "https://firebasestorage.googleapis.com/v0/b/mrt-app-55dac.appspot.com/o/Data%2FAchievement%2Fno.png?alt=media&token=69ba10f4-89f8-43b6-80e6-4071f9fff82b"
        var maxDemand: Long
        var levelName: String
        var backgroundResource = R.drawable.ribbon_bg_nolavel

        when {
            existsquantity < bronzeDemand -> {
                maxDemand = bronzeDemand
                levelName = holder.itemView.context.getString(R.string.ribbonName)
            }
            existsquantity < silverDemand -> {
                imageUrl = levelImageUrl.optString("Bronze", imageUrl)
                maxDemand = silverDemand
                backgroundResource = R.drawable.ribbon_bg_bronze
                levelName = bronzeData["Name"] as? String ?: "入門者"
            }
            existsquantity < goldDemand -> {
                imageUrl = levelImageUrl.optString("Silver", imageUrl)
                maxDemand = goldDemand
                backgroundResource = R.drawable.ribbon_bg_silver
                levelName = silverData["Name"] as? String ?: "達人"
            }
            else -> {
                imageUrl = levelImageUrl.optString("Gold", imageUrl)
                maxDemand = goldDemand
                backgroundResource = R.drawable.ribbon_bg_gold
                levelName = goldData["Name"] as? String ?: "大師"
            }
        }

        holder.achievementName.text = levelName
        holder.achievementName.setBackgroundResource(backgroundResource)
        holder.achievementTitle.text = achievement.Name
        holder.achievementDemand.text = maxDemand.toString()
        holder.achievementHave.text = existsquantity.toString()
        holder.achievementPercentage.text = calculatePercentage(existsquantity, maxDemand).toString()

        // 使用 Glide 加載圖像
        Glide.with(holder.achievementImage)
            .load(imageUrl.toUri())
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.loading_error)
            .into(holder.achievementImage)

        // 設置進度條
        holder.progressBar.progress = calculatePercentage(existsquantity, maxDemand)
    }

    override fun getItemCount(): Int = achievementList.size

    private fun calculatePercentage(have: Long, demand: Long): Int {
        return if (demand > 0) ((have * 100) / demand).toInt() else 0
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val achievementTitle: TextView = itemView.findViewById(R.id.achievenent_Title)
        val achievementName: TextView = itemView.findViewById(R.id.Achievenent_Name)
        val achievementImage: ImageView = itemView.findViewById(R.id.Achievenent_Image)
        val achievementHave: TextView = itemView.findViewById(R.id.Achievement_Have)
        val achievementDemand: TextView = itemView.findViewById(R.id.Achievement_Demand)
        val achievementPercentage: TextView = itemView.findViewById(R.id.Achievement_percentage)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        val Achievement_CardView: CardView = itemView.findViewById(R.id.Achievement_CardView)
    }

}
