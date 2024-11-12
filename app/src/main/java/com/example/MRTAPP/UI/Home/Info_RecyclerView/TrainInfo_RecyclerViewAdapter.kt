package com.example.MRTAPP.UI.Home.Info_RecyclerView

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.MRTAPP.MRTArrivalReceiver
import com.example.MRTAPP.Other.GetStationNameLanguage
import com.example.MRTAPP.R

class TrainInfo_RecyclerViewAdapter(
    private val context: Context,
    private val mList: List<TrainInfoList>
) : RecyclerView.Adapter<TrainInfo_RecyclerViewAdapter.MyViewHolder>() {

    private val CHANNEL_ID = "MRT_CHANNEL_ID"
    private val notificationId = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_traininfo, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = mList[position]

        val GetStationNameLanguage = GetStationNameLanguage(context)
        when{
            currentItem.DestinationName.contains("南港展覽館")->{
                holder.tTrain_Info_Station_Id.setTextSize(14f)
                holder.tTrain_Info_Station_Id_2.setTextSize(14f)
                holder.tTrain_Info_Station_Id_2.visibility=View.VISIBLE
                holder.tTrain_Info_Station_Id.text="BL23"
                holder.tTrain_Info_Station_Id_2.text="BR24"
                // 設置背景顏色
                val colorStateList = ContextCompat.getColorStateList(context, R.color.mrt_route_BL)
                if (colorStateList != null) {
                    ViewCompat.setBackgroundTintList(holder.tTrain_Info_Station_Id, colorStateList)
                } else {
                    holder.tTrain_Info_Station_Id.setBackgroundColor(ContextCompat.getColor(context, R.color.mrt_route_BL))
                    holder.tTrain_Info_Station_Id_2.setBackgroundColor(ContextCompat.getColor(context, R.color.mrt_route_BR))
                }

            }!currentItem.DestinationName.contains("南港展覽館")->{
                holder.tTrain_Info_Station_Id.setTextSize(16f)
                holder.tTrain_Info_Station_Id_2.visibility=View.GONE
                holder.tTrain_Info_Station_Id.text = formatDestinationName(currentItem.DestinationName)
            }
        }
        // 設置車站代碼
        val backgroundTint = when {
            holder.tTrain_Info_Station_Id.text.contains("R22A") -> R.color.mrt_route_R22A
            holder.tTrain_Info_Station_Id.text.contains("G03A") -> R.color.mrt_route_G03A
            holder.tTrain_Info_Station_Id.text.contains("BL") -> R.color.mrt_route_BL
            holder.tTrain_Info_Station_Id.text.contains("BR") -> R.color.mrt_route_BR
            holder.tTrain_Info_Station_Id.text.contains("R") -> R.color.mrt_route_R
            holder.tTrain_Info_Station_Id.text.contains("G") -> R.color.mrt_route_G
            holder.tTrain_Info_Station_Id.text.contains("O") -> R.color.mrt_route_O
            holder.tTrain_Info_Station_Id.text.contains("Y") -> R.color.mrt_route_Y
            holder.tTrain_Info_Station_Id.text.contains("Y12") -> R.color.mrt_route_Y


            else -> R.color.mrt_route_BL
        }

        // 設置背景顏色
        val colorStateList = ContextCompat.getColorStateList(context, backgroundTint)
        if (colorStateList != null) {
            ViewCompat.setBackgroundTintList(holder.tTrain_Info_Station_Id, colorStateList)
        } else {
            holder.tTrain_Info_Station_Id.setBackgroundColor(ContextCompat.getColor(context, backgroundTint))
        }
        holder.tTrain_Info_Station_Name.text = GetStationNameLanguage.getStationName(context,currentItem.DestinationName.dropLast(1))
        holder.tTrain_Info_Station_Time.text = formatCountDown(currentItem.countDown)

        // 提醒按鈕邏輯
        holder.tTrain_Remind_Btn.setOnClickListener {
            val EndName = currentItem.DestinationName
            val Time = currentItem.countDown
            if (Time.contains(":")) {
                // 解析並計算觸發時間
                val parts = Time.split(":")
                val minutes = parts[0].toInt()
                val seconds = parts[1].toInt()
                val totalSeconds = minutes * 60 + seconds
                // 計算倒數時間減去60秒的觸發時間
                if (totalSeconds > 60) {
                    val triggerTimeInMillis = (totalSeconds - 60) * 1000L + System.currentTimeMillis()
                    Toast.makeText(context,context.getString(R.string.setting_success_reminder),Toast.LENGTH_LONG).show()
                    setMRTArrivalAlarm(context, EndName, Time, triggerTimeInMillis)
                    showMRTArrivalNotification(EndName, Time)
                } else {
                    Toast.makeText(context,context.getString(R.string.train_approaching),Toast.LENGTH_LONG).show()
                }
            } else if(Time=="列車進站") {
                Toast.makeText(context,context.getString(R.string.Train_Approaching),Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(context,context.getString(R.string.train_reciprocal_error),Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tTrain_Info_Station_Id: TextView = itemView.findViewById(R.id.Train_Info_Station_Id_1)
        val tTrain_Info_Station_Id_2: TextView = itemView.findViewById(R.id.Train_Info_Station_Id_2)

        val tTrain_Info_Station_Name: TextView = itemView.findViewById(R.id.Train_Info_Station_Name)
        val tTrain_Info_Station_Time: TextView = itemView.findViewById(R.id.Train_Info_Station_Time)
        val tTrain_Remind_Btn: Button = itemView.findViewById(R.id.Remind_btn)
    }

    // 格式化站名顯示
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
            destinationName.contains("中和站") -> "Y12"
            destinationName.contains("板橋站") -> "Y16"

            destinationName.contains("昆陽站") -> "BL21"
            else -> destinationName
        }
    }

    // 倒數計時格式化
    private fun formatCountDown(countDown: String): String {
        return when {
            countDown.contains("列車進站") ->context.getString(R.string.Train_Approaching)
            countDown.contains("資料擷取中") -> context.getString(R.string.Loading_Data)
            countDown.contains(":") -> {
                val parts = countDown.split(":")
                val minutes = parts[0].toInt()
                val seconds = parts[1].toInt()
                "${minutes} ${context.getString(R.string.minute)} ${seconds} ${context.getString(R.string.Second)}"
            }
            else -> countDown
        }
    }

    // 顯示捷運到站提醒通知
    private fun showMRTArrivalNotification(EndName: String, Time: String) {
        val getLanguage=GetStationNameLanguage(context)
        val language=getLanguage.getsaveLanguage2(context)
        val languageStationName=getLanguage.getStationName2(context,EndName.dropLast(1),
            "Zh_tw",language)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(context.getString(R.string.remindbtn))
            .setContentText( context.getString(R.string.arriving_in_minutes, languageStationName, Time))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        // 設置大圖示（圖片資源或位圖）
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.logo)
        builder.setLargeIcon(bitmap)
        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(notificationId, builder.build())
        }
    }

    // 設定捷運到站提醒
    fun setMRTArrivalAlarm(context: Context, endName: String, time: String, triggerTimeInMillis: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MRTArrivalReceiver::class.java).apply {
            putExtra("EndName", endName)
            putExtra("Time", time)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0, // requestCode
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 設定在指定時間觸發的警報
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTimeInMillis, pendingIntent)
    }
}
