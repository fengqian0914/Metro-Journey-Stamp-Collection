package com.example.MRTAPP.UI.Home.Info_RecyclerView

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.MRTAPP.R

class StationInfo_RecylerViewAdapter(
    private val context: Context,
    private val mList: List<StationInfoList>,
    private val StationName: String,
) : RecyclerView.Adapter<StationInfo_RecylerViewAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_stationinfo, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = mList[position]
        holder.tExitName.text = currentItem.ExitName

        // 重置所有出口圖像和可見性
        holder.tExitImage.setImageDrawable(null)
        holder.tExitType1.visibility = View.INVISIBLE
        holder.tExitType2.visibility = View.INVISIBLE
        holder.tExitType3.visibility = View.INVISIBLE

        // 根據 StationName 設定出口圖像
        if (StationName == "台北車站") {
            when (currentItem.ExitId) {
                1 -> holder.tExitImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.exit_m1))
                2 -> holder.tExitImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.exit_m2))
                3 -> holder.tExitImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.exit_m3))
                4 -> holder.tExitImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.exit_m4))
                5 -> holder.tExitImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.exit_m5))
                6 -> holder.tExitImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.exit_m6))
                7 -> holder.tExitImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.exit_m7))
                8 -> holder.tExitImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.exit_m8))
            }
        } else {
            when (currentItem.ExitId) {
                0 -> holder.tExitImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.exit_1))

                1 -> holder.tExitImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.exit_1))
                2 -> holder.tExitImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.exit_2))
                3 -> holder.tExitImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.exit_3))
                4 -> holder.tExitImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.exit_4))
                5 -> holder.tExitImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.exit_5))
                6 -> holder.tExitImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.exit_6))
                7 -> holder.tExitImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.exit_7))
                8 -> holder.tExitImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.exit_8))
                9 -> holder.tExitImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.exit_9))
                10 -> holder.tExitImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.exit_10))
                11 -> holder.tExitImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.exit_11))
            }
        }

        // 設定樓梯圖像
        holder.tExitType1.visibility = if (currentItem.ExitType1) {
            holder.tExitType1.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.stairs))
            holder.tExitTypeText1.text = context.getString(R.string.ExitType1_0)
            View.VISIBLE
        } else {
            holder.tExitTypeText1.text = context.getString(R.string.ExitType1_1)
            View.INVISIBLE
        }


        // 設定手扶梯圖像
        when (currentItem.ExitType2) {
            0 -> {
                holder.tExitType2.visibility = View.INVISIBLE
                holder.tExitTypeText2.text = context.getString(R.string.ExitType2_0)

            }
            1 -> {
                holder.tExitType2.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.escalator_1))
                holder.tExitType2.visibility = View.VISIBLE
                holder.tExitTypeText2.text = context.getString(R.string.ExitType2_1)

            }
            2 -> {
                holder.tExitType2.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.escalator_2))
                holder.tExitType2.visibility = View.VISIBLE
                holder.tExitTypeText2.text = context.getString(R.string.ExitType2_2)

            }
            3 -> {
                holder.tExitType2.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.escalator_3))
                holder.tExitType2.visibility = View.VISIBLE
                holder.tExitTypeText2.text = context.getString(R.string.ExitType2_3)

            }
        }

        // 設定電梯圖像
        holder.tExitType3.visibility = if (currentItem.ExitType3) {
            holder.tExitType3.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.elevator))
            holder.tExitTypeText3.text = context.getString(R.string.ExitType3_0)
            View.VISIBLE

        } else {
            holder.tExitTypeText3.text = context.getString(R.string.ExitType3_1)
            View.INVISIBLE
        }

        // 設定可展開區域的可見性
        holder.tContext.visibility = if (currentItem.expandable) View.VISIBLE else View.GONE

        // 設定點擊事件來切換可展開性
        holder.tholder_view.setOnClickListener {
            currentItem.expandable = !currentItem.expandable
            notifyItemChanged(position) // 刷新當前項目以更新可見性
        }
        holder.tholder_view_layout.setOnClickListener {
            currentItem.expandable = !currentItem.expandable
            notifyItemChanged(position) // 刷新當前項目以更新可見性
        }
        holder.tExitName.setOnClickListener {
            currentItem.expandable = !currentItem.expandable
            notifyItemChanged(position) // 刷新當前項目以更新可見性
        }
        holder.tExitImage.setOnClickListener {
            currentItem.expandable = !currentItem.expandable
            notifyItemChanged(position) // 刷新當前項目以更新可見性
        }
        holder.tdownicon.setOnClickListener {
            currentItem.expandable = !currentItem.expandable
            notifyItemChanged(position) // 刷新當前項目以更新可見性
        }
        // Log for debugging
        Log.d("ExitType1", currentItem.ExitType1.toString())
        Log.d("ExitType2", currentItem.ExitType2.toString())
        Log.d("ExitType3", currentItem.ExitType3.toString())
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tExitImage: ImageView = itemView.findViewById(R.id.ExitImageView)
        val tExitName: TextView = itemView.findViewById(R.id.exitText)
        val tExitType1: ImageView = itemView.findViewById(R.id.ExitType1)
        val tExitType2: ImageView = itemView.findViewById(R.id.ExitType2)
        val tExitType3: ImageView = itemView.findViewById(R.id.ExitType3)
        val tholder_view: CardView = itemView.findViewById(R.id.holder_view)
        val tholder_view_layout:LinearLayout=itemView.findViewById(R.id.holder_view_layout)
        val tdownicon:ImageView=itemView.findViewById(R.id.downicon)

        val tContext: LinearLayout = itemView.findViewById(R.id.tContext)
        val tExitTypeText1:TextView=itemView.findViewById(R.id.ExitTypeText1)
        val tExitTypeText2:TextView=itemView.findViewById(R.id.ExitTypeText2)
        val tExitTypeText3:TextView=itemView.findViewById(R.id.ExitTypeText3)

    }


}
