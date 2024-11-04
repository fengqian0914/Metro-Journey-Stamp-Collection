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
        val levelData = achievement.level

        // 綁定等級數據
        updateData(holder, levelData, achievement.existsquantity.toLong(),position)

        holder.Achievement_CardView.setOnClickListener {
            Log.d("Achievement_CardView", "成就: ${achievement.Name}, ID: ${achievement.id}")
        }
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
        var imageUrl = levelImageUrl.optString("no", "")
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
        val achievementTitle: TextView = itemView.findViewById(R.id.Achievenent_Title)
        val achievementName: TextView = itemView.findViewById(R.id.Achievenent_Name)
        val achievementImage: ImageView = itemView.findViewById(R.id.Achievenent_Image)
        val achievementHave: TextView = itemView.findViewById(R.id.Achievement_Have)
        val achievementDemand: TextView = itemView.findViewById(R.id.Achievement_Demand)
        val achievementPercentage: TextView = itemView.findViewById(R.id.Achievement_percentage)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        val Achievement_CardView: CardView = itemView.findViewById(R.id.Achievement_CardView)
    }
}
