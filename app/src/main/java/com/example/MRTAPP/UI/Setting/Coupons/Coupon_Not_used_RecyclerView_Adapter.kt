package com.example.MRTAPP.UI.Setting.Coupons

import android.content.Context
import android.content.Intent
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.MRTAPP.R
import com.example.MRTAPP.UI.Settings_Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.NonDisposableHandle.parent

class Coupon_Not_used_RecyclerView_Adapter(
    private val context: Context,
    private val mList: List<Coupon_list>
) : RecyclerView.Adapter<Coupon_Not_used_RecyclerView_Adapter.MyViewHolder>() {

    private lateinit var dialog: BottomSheetDialog
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_recycler_view_coupon_notused, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = mList[position]
        try {
            // 更新 ViewHolder 的內容
            holder.tCoupon_Name.text = currentItem.productName
            holder.tCoupon_Quantity.text = currentItem.quantity.toString()
            holder.tCoupon_ExpiryTime.text = currentItem.expiryTime
            holder.tCoupon_ID.text = currentItem.redeemCode

            // 載入圖片
            Glide.with(holder.tCouponImage)
                .load(currentItem.productImage) // 這裡是 Firebase Storage 的圖片 URL
                .placeholder(R.drawable.placeholder) // 替換為你的佔位符圖片
                .error(R.drawable.loading_error) // 替換為你的錯誤圖片
                .into(holder.tCouponImage)

            // 檢查列表是否為空
            if (mList.isEmpty()) {
                holder.tEmptyDatas.visibility = View.VISIBLE
                holder.tEmptyDatas.text = "目前尚無兌換券"
            } else {
                holder.tEmptyDatas.visibility = View.GONE
            }

            // 設置按鈕的點擊事件
            holder.tCouponBtn.setOnClickListener {
                Log.d("CouponID", currentItem.CouponId)
                showBottomSheet_recyclerview(
                    currentItem.productName,
                    currentItem.quantity.toString(),
                    currentItem.redeemCode,
                    currentItem.CouponId
                )
            }
        } catch (e: Exception) {
            Log.e("BindingError", e.message.toString())
        }
    }

    private fun showBottomSheet_recyclerview(ProductName: String, quantity: String, redeemCode: String, CouponId: String) {
        // 載入佈局
        try {
            val inflater = LayoutInflater.from(context)
            val dialogView = inflater.inflate(R.layout.activity_qrcode_coupon_recyclerview, null)

            // 設置文本
            dialogView.findViewById<TextView>(R.id.QR_ProductName_recyclerview).text = ProductName
            dialogView.findViewById<TextView>(R.id.QR_quantity).text = quantity
            dialogView.findViewById<TextView>(R.id.QR_redeemCode).text = redeemCode

            val Product_QRcode = dialogView.findViewById<ImageView>(R.id.Product_QRcode_recyclerview)

            // 生成 QR 碼
            try {
                val barcodeEncoder = BarcodeEncoder()
                val bitmap = barcodeEncoder.encodeBitmap(redeemCode, BarcodeFormat.QR_CODE, 400, 400)
                Product_QRcode.setImageBitmap(bitmap)
                adjustImageViewSize(Product_QRcode, bitmap)
            } catch (e: WriterException) {
                Log.e("QRCodeError", "無法生成 QR 碼: ${e.message}")
                Toast.makeText(context, context.getString(R.string.qr_code_generation_failed), Toast.LENGTH_SHORT).show()
            }

            // 設置最終按鈕的點擊事件
            val final_qrcodebtn = dialogView.findViewById<Button>(R.id.final_qrcodebtn)
            val userId = auth.currentUser?.uid

            final_qrcodebtn.setOnClickListener {
                db.collection("users").document(userId.toString()).collection("coupons").document(CouponId)
                    .update("status", "已兌換")
                    .addOnSuccessListener {
                        dialog.dismiss()





                    }
                    .addOnFailureListener {
                        Toast.makeText(context, context.getString(R.string.failure_retry_or_report), Toast.LENGTH_SHORT).show()
                        Log.e("DatabaseError", "更新失敗")
                    }

            }


            // 建立 BottomSheetDialog
            dialog = BottomSheetDialog(context, R.style.BottomsheetDialogTheme)
            dialog.setContentView(dialogView)
            dialog.show()

        } catch (e: Exception) {
            Log.e("ShowBottomSheetError", e.toString())
        }
    }

    private fun adjustImageViewSize(imageView: ImageView, bitmap: Bitmap) {
        val targetWidth = imageView.width

        // 如果 ImageView 的寬度為 0 (尚未佈局完成)，則在佈局完成後再調整大小
        if (targetWidth == 0) {
            imageView.post {
                val finalWidth = imageView.width
                val scaledBitmap = scaleBitmapToFitWidth(bitmap, finalWidth)
                imageView.setImageBitmap(scaledBitmap)
            }
        } else {
            val scaledBitmap = scaleBitmapToFitWidth(bitmap, targetWidth)
            imageView.setImageBitmap(scaledBitmap)
        }
    }

    private fun scaleBitmapToFitWidth(bitmap: Bitmap, targetWidth: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val scale = targetWidth.toFloat() / width
        val matrix = Matrix().apply {
            postScale(scale, scale)
        }
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tCouponImage: ImageView = itemView.findViewById(R.id.NotUsed_CouponImage)
        val tCoupon_Name: TextView = itemView.findViewById(R.id.NotUsed_Coupon_Name)
        val tCoupon_Quantity: TextView = itemView.findViewById(R.id.NotUsed_Coupon_Quantity)
        val tCoupon_ID: TextView = itemView.findViewById(R.id.NotUsed_coupon_ID)
        val tCoupon_ExpiryTime: TextView = itemView.findViewById(R.id.NotUsed_Coupon_ExpiryTime)
        val tCouponBtn: Button = itemView.findViewById(R.id.Coupon_btn)
        val tEmptyDatas: TextView = itemView.findViewById(R.id.NotUsed_EmptyDatas)
    }
}
