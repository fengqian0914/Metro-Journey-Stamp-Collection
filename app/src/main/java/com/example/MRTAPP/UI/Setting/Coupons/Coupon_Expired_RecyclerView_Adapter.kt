//package com.example.MRTAPP.UI.Setting.Coupons
//
//import android.content.Context
//
//class Coupon_Expired_RecyclerView_Adapter(
//    private val context: Context,
//    private val mList: List<Coupon_list>
//) :RecyclerView.Adapter<Coupon_Not_used_RecyclerView_Adapter.MyViewHolder>() {
//    private lateinit var dialog:BottomSheetDialog {
//}
package com.example.MRTAPP.UI.Setting.Coupons

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.MRTAPP.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder

class Coupon_Expired_RecyclerView_Adapter(
    private val context: Context,
    private val mList: List<Coupon_list>
) : RecyclerView.Adapter<Coupon_Expired_RecyclerView_Adapter.MyViewHolder>() {
    private lateinit var dialog:BottomSheetDialog

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_recycler_view_coupon_expired, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = mList[position]
        try {
            // 更新 ViewHolder 的內容
            holder.tCoupon_Name.text = "${currentItem.productName}"
            holder.tCoupon_Quantity.text = "${currentItem.quantity}"
            holder.tCoupon_ExpiryTime.text = "${currentItem.expiryTime.toString()}"
            holder.tCoupon_ID.text = "${currentItem.redeemCode}"



            Glide.with(holder.tCouponImage)
                .load(currentItem.productImage) // 這裡是 Firebase Storage 的圖片 URL
                .placeholder(R.drawable.placeholder) // 請替換為你的佔位符圖片
                .error(R.drawable.loading_error) // 請替換為你的錯誤圖片
                .into(holder.tCouponImage)


            // 如果列表為空，顯示「目前尚無兌換券」
            if (mList.isEmpty()) {
                holder.tEmptyData?.apply {
                    text = "目前尚無兌換券"
                    visibility = View.VISIBLE
                }
            } else {
                holder.tEmptyData?.visibility = View.GONE
            }
        }
        catch (e:Exception){
            Log.d("errorssss2",e.toString())
        }
    }



    override fun getItemCount(): Int {
        return mList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tCouponImage: ImageView = itemView.findViewById(R.id.CouponImage)
        val tCoupon_Name: TextView = itemView.findViewById(R.id.Coupon_Name)
        val tCoupon_Quantity: TextView = itemView.findViewById(R.id.Coupon_Quantity)
        val tCoupon_ExpiryTime: TextView = itemView.findViewById(R.id.Coupon_ExpiryTime)
        val tCoupon_ID: TextView = itemView.findViewById(R.id.coupon_ID)
        val tEmptyData:TextView=itemView.findViewById(R.id.EmptyDatas)
    }

    private fun formatDestinationName(destinationName: String): String {
        return when {
            destinationName.contains("新北投站") -> "R22A"
            destinationName.contains("小碧潭站") -> "G03A"
            destinationName.contains("南港展覽館站") -> "BL23/BR24"
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
            else -> destinationName
        }
    }

    private fun formatCountDown(countDown: String): String {
        return when {
            countDown.contains("列車進站") -> "列車進站中"
            countDown.contains("資料讀取中") -> "資料讀取中"
            countDown.contains(":") -> {
                val parts = countDown.split(":")
                val minutes = parts[0].toInt()
                val seconds = parts[1].toInt()
                "${minutes}分${seconds}秒"
            }
            else -> countDown
        }
    }
}
