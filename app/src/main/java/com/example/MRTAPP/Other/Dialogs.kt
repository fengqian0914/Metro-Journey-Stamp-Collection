package com.example.MRTAPP.Other

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.MRTAPP.R

class dialogs(private val context: Context) {

   // 显示对话框的函数
   fun showDialog(dialogTitle: String, dialogsMsg: String) {
      val dialog = Dialog(context)
      dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
      dialog.setCancelable(false)
      dialog.setContentView(R.layout.activity_dialogs)
      dialog.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context, R.color.transparent)))

      // 获取对话框的视图元素
      val dialogTitleView = dialog.findViewById<TextView>(R.id.dialog_wait_title)
      val dialogMsgView = dialog.findViewById<TextView>(R.id.dialog_wait_msg)
      val dialogClose = dialog.findViewById<ImageView>(R.id.dialog_close)
      val cardView = dialog.findViewById<CardView>(R.id.dialog_cardview)

      // 设置对话框宽度为屏幕宽度的90%
      val screenWidth = getScreenWidth(context)
      val cardWidth = (screenWidth * 0.9).toInt()
      cardView.layoutParams.width = cardWidth

      // 设置对话框的标题和消息
      dialogTitleView.text = dialogTitle
      dialogMsgView.text = dialogsMsg

      // 关闭按钮的点击事件
      dialogClose.setOnClickListener {
         dialog.dismiss()
      }

      // 显示对话框
      dialog.show()
   }

   // 获取屏幕宽度的辅助函数
   private fun getScreenWidth(context: Context): Int {
      val displayMetrics = DisplayMetrics()
      val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
      @Suppress("DEPRECATION")
      windowManager.defaultDisplay.getMetrics(displayMetrics)
      return displayMetrics.widthPixels
   }
}
