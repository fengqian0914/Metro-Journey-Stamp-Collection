package com.example.MRTAPP.UI.Setting.FAQ

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.MRTAPP.R
import com.example.MRTAPP.UI.Setting.Coupons.Coupon_Expired_RecyclerView_Adapter
import com.example.MRTAPP.UI.Setting.Coupons.Coupon_list

class Setting_FAQ_RecylerViewAdapter (
    private val context: Context,
    private val mList: List<Setting_FAQ_List>):
    RecyclerView.Adapter<Setting_FAQ_RecylerViewAdapter.MyViewHolder>() {
    class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        val tFAQTitle: TextView =itemView.findViewById(R.id.FAQ_Title)
        val tFAQTitleView: LinearLayout =itemView.findViewById(R.id.FAQ_Title_View)
        val tFAQAns: TextView =itemView.findViewById(R.id.FAQ_Ans)
        val tFAQAnsView: LinearLayout =itemView.findViewById(R.id.FAQ_Ans_View)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_setting_faq_recyler_view_adapter, parent, false)

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  mList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = mList[position]
        holder.tFAQTitle.text=currentItem.FAQTitle
        holder.tFAQAns.text=currentItem.FAQText
        // 預設隱藏答案
        holder.tFAQAnsView.visibility = View.GONE

        // 設置點擊事件以展開或收起答案
        holder.tFAQTitleView.setOnClickListener {
            if (holder.tFAQAnsView.visibility == View.GONE) {
                holder.tFAQAnsView.visibility = View.VISIBLE
            } else {
                holder.tFAQAnsView.visibility = View.GONE
            }
        }
        if(currentItem.FAQTitle=="你們的API的資料來源為何處"){
            Ans_view_Image(holder,position,"API")

        }

    }

    private fun Ans_view_Image(holder: MyViewHolder, position: Int,type:String) {
        if(type=="API"){
            val textView1 = TextView(holder.itemView.context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                text = "資料介接「台北捷運」"
                textSize = 16f
                setTextColor(ContextCompat.getColor(context, R.color.black))
            }
            val imageView1 = ImageView(holder.itemView.context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                scaleType = ImageView.ScaleType.CENTER_INSIDE

                // 根据 imageType 设置不同的图片资源
                setImageResource(
                    when (type) {
                        "API" -> R.drawable.metro // 假设你有一个名为 tdx 的 drawable
                        // 其他类型可以在这里扩展
                        else -> R.drawable.default_photo // 默认图片资源
                    }
                )
            }
            val textView2 = TextView(holder.itemView.context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                text = "資料介接「交通部TDX平臺」"
                textSize = 16f
                setTextColor(ContextCompat.getColor(context, R.color.black))
            }
            val imageView2 = ImageView(holder.itemView.context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                scaleType = ImageView.ScaleType.CENTER_INSIDE

                // 根据 imageType 设置不同的图片资源
                setImageResource(
                    when (type) {
                        "API" -> R.drawable.tdx // 假设你有一个名为 tdx 的 drawable
                        // 其他类型可以在这里扩展
                        else -> R.drawable.default_photo // 默认图片资源
                    }
                )
            }
            holder.tFAQAnsView.addView(textView1)
            holder.tFAQAnsView.addView(imageView1)
            holder.tFAQAnsView.addView(textView2)
            holder.tFAQAnsView.addView(imageView2)

        }
    }

}