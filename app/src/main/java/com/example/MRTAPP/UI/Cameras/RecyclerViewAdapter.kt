package com.example.MRTAPP.UI.Cameras

import android.content.res.ColorStateList
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.MRTAPP.R

class recyclerViewAdapter(var  mlist:ArrayList<MRT_Station_item>, var route:String):RecyclerView.Adapter<recyclerViewAdapter.ViewHolder>()  {
    class ViewHolder(RecyclerView:View):RecyclerView.ViewHolder(RecyclerView) {



        val VH_item_card_route_text:TextView=itemView.findViewById(R.id.item_card_route_text)
        val VH_item_station_name:TextView=itemView.findViewById(R.id.item_station_name)
        val VH_item_station_truefalseImage:ImageView=itemView.findViewById(R.id.item_truefalse)
        val VH_item_card_bg:LinearLayout=itemView.findViewById(R.id.item_card_bg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_route,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  mlist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myItem=mlist[position]
        holder.VH_item_card_route_text.text=myItem.Route
        holder.VH_item_station_name.text=myItem.station


        if(myItem.condition=="未完成")
            holder.VH_item_station_truefalseImage.setImageResource(R.drawable.station_false)
        else
            holder.VH_item_station_truefalseImage.setImageResource(R.drawable.station_true)

        val textLength = myItem.station.length
        val textSize = getTextSizeForTextLength(textLength)
        holder.VH_item_station_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)

        val colorResourceId = holder.itemView.context.resources.getIdentifier("mrt_route_$route", "color", holder.itemView.context.packageName)
        val color = ContextCompat.getColor(holder.itemView.context, colorResourceId)
        holder.VH_item_card_bg.backgroundTintList = ColorStateList.valueOf(color)


    }
    private fun getTextSizeForTextLength(textLength: Int): Float {
        return when {
            textLength < 10 -> 20f // 文字長度小於10時設置文字大小為20sp
            textLength < 20 -> 16f // 文字長度在10到20之間時設置文字大小為16sp
            else -> 14f // 文字長度大於等

        }
    }
}