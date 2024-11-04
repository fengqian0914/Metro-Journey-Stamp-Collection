package com.example.MRTAPP.UI.Star

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.MRTAPP.R
import com.example.MRTAPP.UI.Mall.Product_RecyclerViewAdapter
import com.example.MRTAPP.UI.Star_Fragment
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

        // 綁定成就名稱
        holder.achievementName.text = achievement.Name


        val stationdata=achievement.station
//        val keys = stationdata.keys() // 獲取所有的鍵
        val levelData=achievement.level
//
//        while (keys.hasNext()) {
//            val key = keys.next() // 每個站點代碼，例如 "BL11"
//            val stationDetails = stationdata.getJSONObject(key) // 獲取每個站點的詳細資料
//
//            // 提取詳細資料中的 Name 和 exists 值
//            val name = stationDetails.getString("Name")
//            val exists = stationDetails.getBoolean("exists")
//
//            println("Station Code: $key, Name: $name, Exists: $exists")
//        }
        updateData(holder,position,levelData,achievement.existsquantity.toLong())





        //
        holder.Achievement_CardView.setOnClickListener {
            Log.d("Achievement_CardView","achievement.Name${achievement.Name}\n" +
                    "achievement.id${achievement.id}\n" +
                    "achievement.Image${achievement.Image}\n" +
                    "achievement.station${achievement.station}\n"+
                    "achievement.level${achievement.level}\n" +
                    "Image${achievement.Image}\n" +
                    "existsquantity${achievement.existsquantity}"






            )
        }
        // 如果 `Image` 是一個 URL，使用 Glide 或 Picasso 來載入圖片
        // Glide.with(holder.itemView.context).load(achievement.Image).into(holder.achievementImage)
    }

    private fun updateData(holder:MyViewHolder, position: Int, levelData: Map<String, Any>,existsquantity:Long) {
        val achievement = achievementList[position]

        val bronzeData = levelData["Bronze"] as? Map<String, Any> ?: emptyMap()
        val silverData = levelData["Silver"] as? Map<String, Any> ?: emptyMap()
        val goldData = levelData["Gold"] as? Map<String, Any> ?: emptyMap()

        // 獲取 Bronze 等級的 `demand` 和 `Name`
        val bronzeDemand = bronzeData["demand"] as? Long ?: 0
        val bronzeName = bronzeData["Name"] as? String ?: ""

        // 獲取 Silver 等級的 `demand` 和 `Name`
        val silverDemand = silverData["demand"] as? Long ?: 0
        val silverName = silverData["Name"] as? String ?: ""

        // 獲取 Gold 等級的 `demand` 和 `Name`
        val goldDemand = goldData["demand"] as? Long ?: 0
        val goldName = goldData["Name"] as? String ?: ""

        val ImageJSONObject=achievement.Image
        var Imageurl:String
        var maxdemand:Long

        val lavels:String
        if (existsquantity < bronzeDemand) {
            Imageurl = ImageJSONObject.getString("Bronze")
            maxdemand = bronzeDemand
            lavels="銅"
        } else if (existsquantity < silverDemand) {
            Imageurl = ImageJSONObject.getString("Silver")
            maxdemand = silverDemand
            lavels="銀"
        } else {
            Imageurl = ImageJSONObject.getString("Gold")
            maxdemand = goldDemand
            lavels="金"
        }
        Log.d("Achievement_CardView","等級${lavels}\n" +
                "image${Imageurl}\n" +
                "持有${existsquantity}\n" +
                "total${maxdemand}\n" +
                "需求銅${bronzeDemand}\n" +
                "需求銀${silverDemand}\n" +
                "需求金${goldDemand}\n" +
                "max:${maxdemand}" )

        Glide.with(holder.achievementImage)
            .load(Imageurl.toUri()) // 這裡是 Firebase Storage 的圖片 URL
            .placeholder(R.drawable.placeholder) // 請替換為你的佔位符圖片
            .error(R.drawable.loading_error) // 請替換為你的錯誤圖片
            .into(holder.achievementImage)

//         綁定需求和完成數量
        holder.achievementDemand.text = maxdemand.toString()
        holder.achievementHave.text = achievement.existsquantity.toString() // 假設這個值從其他數據來源取得
        holder.achievementPercentage.text = calculatePercentage(existsquantity.toInt(), maxdemand.toInt()).toString()

        // 更新進度條
        holder.progressBar.progress = calculatePercentage(existsquantity.toInt(),maxdemand.toInt())

    }

    override fun getItemCount(): Int {
        return achievementList.size
    }

    private fun calculatePercentage(have: Int, demand: Int): Int {
        return if (demand > 0) (have * 100) / demand else 0
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val achievementName: TextView = itemView.findViewById(R.id.Achievenent_Name)
        val achievementImage: ImageView = itemView.findViewById(R.id.Achievenent_Image)
        val achievementHave: TextView = itemView.findViewById(R.id.Achievement_Have)
        val achievementDemand: TextView = itemView.findViewById(R.id.Achievement_Demand)
        val achievementPercentage: TextView = itemView.findViewById(R.id.Achievement_percentage)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        val Achievement_CardView:CardView=itemView.findViewById(R.id.Achievement_CardView)
    }
}


