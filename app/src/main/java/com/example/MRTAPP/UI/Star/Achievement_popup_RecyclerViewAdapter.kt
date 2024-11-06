package com.example.MRTAPP.UI.Star

import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.MRTAPP.Other.GetStationNameLanguage
import com.example.MRTAPP.R
import com.example.MRTAPP.UI.Cameras.MRT_Station_item
import com.example.MRTAPP.UI.Cameras.recyclerViewAdapter

class Achievement_popup_RecyclerViewAdapter(
    private var mlist: ArrayList<MRT_Station_item>,
    private var route: String? = null
) : RecyclerView.Adapter<Achievement_popup_RecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val VH_item_card_route_text: TextView = itemView.findViewById(R.id.item_card_route_text)
        val VH_item_station_name: TextView = itemView.findViewById(R.id.item_station_name)
        val VH_item_station_truefalseImage: ImageView = itemView.findViewById(R.id.item_truefalse)
        val VH_item_card_bg: LinearLayout = itemView.findViewById(R.id.item_card_bg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_route, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = mlist.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val myItem = mlist[position]
        val GetLanguage = GetStationNameLanguage(holder.itemView.context)

        val savelanauge=GetLanguage.getsaveLanguage(holder.itemView.context)

        holder.VH_item_card_route_text.text = myItem.Route
        holder.VH_item_station_name.text = if(myItem.station.contains("（")==true)
        {
            Log.d("myItem.station.substringBefore(\"（\")","${myItem.station.substringBefore("（")}")
            GetLanguage.getStationName2(holder.itemView.context,
                myItem.station.substringBefore("（"),
                "Zh_tw",savelanauge
            )+"（"+myItem.station.substringAfter("（")
        }else{
            Log.d("myItem.station.substringBefore(\"（\")","2${myItem.station}")

            GetLanguage.getStationName2(holder.itemView.context,
                myItem.station,
                "Zh_tw",savelanauge
            )
        }

        holder.VH_item_station_truefalseImage.setImageResource(
            if (myItem.condition == "未完成") R.drawable.station_false else R.drawable.station_true
        )

        // 根据站名设置文本大小
        val textSize = getTextSizeForTextLength(myItem.station.length)
        holder.VH_item_station_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
        holder.VH_item_card_route_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)

        // 设置背景颜色
        setStationBgColor(holder.itemView.context, holder.VH_item_card_bg, getColor(myItem.Route))
        Log.d("color","postion${position}\n" +
                "getColor(myItem.station)${getColor(myItem.Route)}\n" +
                "myItem.station${myItem.Route}\n")
    }

    private fun getTextSizeForTextLength(textLength: Int): Float {
        return when {
            textLength < 10 -> 20f
            textLength < 20 -> 16f
            else -> 14f
        }
    }

    private fun setStationBgColor(context: Context, layout: LinearLayout, colorResId: Int) {
        val colorStateList = ColorStateList.valueOf(ContextCompat.getColor(context, colorResId))
        ViewCompat.setBackgroundTintList(layout, colorStateList)
    }

    private fun getColor(station: String): Int {
        return when {
            station.contains("R22A") -> R.color.mrt_route_R22A
            station.contains("G03A") -> R.color.mrt_route_G03A
            station.startsWith("BL") -> R.color.mrt_route_BL
            station.startsWith("BR") -> R.color.mrt_route_BR
            station.startsWith("R") -> R.color.mrt_route_R
            station.startsWith("G") -> R.color.mrt_route_G
            station.startsWith("O") -> R.color.mrt_route_O
            station.startsWith("Y") -> R.color.mrt_route_Y
            else -> R.color.mrt_route_BL
        }
    }
}
