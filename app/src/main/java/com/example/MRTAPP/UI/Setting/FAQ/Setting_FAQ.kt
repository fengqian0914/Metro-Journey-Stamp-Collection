package com.example.MRTAPP.UI.Setting.FAQ

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.MRTAPP.R
import com.example.MRTAPP.UI.Cameras.recyclerViewAdapter
import com.example.MRTAPP.UI.Home.Info_RecyclerView.StationInfo_RecylerViewAdapter
import com.example.MRTAPP.UI.Home.Info_RecyclerView.TrainInfoList
import com.example.MRTAPP.UI.Home.MainActivity

class Setting_FAQ : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var settingFAQRecylerViewAdapter: Setting_FAQ_RecylerViewAdapter
    private val FAQList = mutableListOf(
        Setting_FAQ_List(
            "這個 APP 有哪些功能？",
            "積分蒐集：使用者可以透過掃描車站 QR 碼來賺取積分。\n" +
                    "兌換商品：累積的積分可在 APP 中兌換精選商品。（模擬）\n" +
                    "捷運資訊查詢：提供即時車站資訊、票價查詢、預估行程時間、路線。\n" +
                    "小工具：提供捷運到站時間的即時查詢，方便用戶隨時掌握行程。"
        ),
        Setting_FAQ_List(
            "是否可以修改資料？",
            "答案：用戶可以修改個人資料，包括名稱、密碼和照片。請至「設定」頁面選擇「編輯個人資料」進行更新。"
        ),
        Setting_FAQ_List(
            "如何獲取積分？",
            "方法一：在車站掃描 QR 碼，每次掃描成功後將獲得相應的積分。\n" +
                    "方法二：蒐集特定站點成就，例如完成名稱有「山」的站點。\n" +
                    "注意：每個 QR 碼一天只能掃描一次。掃描時請確保網路連線正常，並授權相機訪問權限。"
        ),
        Setting_FAQ_List(
            "如何查詢車站資訊、列車資訊或票價？",
            "車站資訊：在 APP 中的首頁中選擇您的站點，點擊「i」圖示後至車站資訊，即可查看每個車站的詳細資訊，包括出入口位置、便利設施等。\n" +
                    "列車資訊：在 APP 中的首頁中選擇您的站點，點擊「i」圖示後至車站資訊，向左滑動至列車資訊後，即可查看該車站的列車到站時間。\n" +
                    "票價查詢：在 APP 中的首頁中選擇起點和終點站，系統會自動計算票價和預估行程時間，幫助您規劃旅程。"
        ),
        Setting_FAQ_List(
            "無法掃描 QR 碼怎麼辦？",
            "確認是否曾抵達過該站點：確保您已到達指定站點，且該站點的 QR 碼尚未被掃描，只有首次掃描的站點 QR 碼才有效。\n" +
                    "確認 QR 碼是否正確：請確認掃描的 QR 碼對應該捷運站點。\n" +
                    "其他解決方法：\n" +
                    "1. 清潔相機鏡頭。\n" +
                    "2. 檢查網路連線穩定性。\n" +
                    "3. 若仍無法掃描，請重啟 APP 再次嘗試。"
        ),
        Setting_FAQ_List(
            "小工具如何使用？",
            "添加小工具：首次使用時，需至「設定」頁面選擇「常用站點」，選擇您要的路線和站點，按下儲存並授權後，將小工具放置於桌面。\n" +
                    "站別修改：如需修改站點，可在「設定」頁面中重新選擇站別後儲存。"
        ),
        Setting_FAQ_List(
            "如何使用兌換功能？",
            "進入兌換中心：打開 APP，選擇「商城」頁面，瀏覽可兌換商品。\n" +
                    "確認兌換：點選「立即兌換」按鈕，取得商品兌換碼或 QR 碼。在兌換紀錄介面中，選擇未使用商品，按下「兌換」，並確定兌換。\n" +
                    "如何查看我兌換的商品？：進入「設定」頁面後，點選「兌換紀錄」即可查看尚未使用或已失效的商品。\n" +
                    "注意事項：商品兌換功能為模擬功能，並無法實際取得該商品。"
        ),
        Setting_FAQ_List(
            "環狀線異常該怎麼辦？",
            "環狀線捷運部分路段於 0403 地震中受損，導致板橋-中和路段無法運行，造成部分路段的列車到站資訊異常，敬請見諒。"
        ),
        Setting_FAQ_List(
            "如何聯絡客服或提供意見？",
            "聯絡方式：在 APP 的「設定」頁面中，點擊「關於我們」選項，查看聯繫方式，並可透過問卷提交問題或建議。若遇到技術問題，建議附上螢幕截圖或錯誤描述，以便我們更快速地協助您。"
        ),
        Setting_FAQ_List(
            "遇到問題或錯誤，該如何重啟或重新安裝？",
            "重啟 APP：若 APP 無法正常使用，可嘗試關閉後重新打開。\n" +
                    "清除快取：在手機「設定」>「應用程式」> 找到該 APP，點選「儲存空間」，然後點擊「清除快取」。\n" +
                    "重新安裝：若問題依舊，建議移除 APP 並重新安裝。重新安裝後，請再次登入您的帳戶。\n" +
                    "回報我們：如問題持續發生，請聯絡客服並附上問題說明，讓我們協助解決。"
        ),
        Setting_FAQ_List(
            "你們的API的資料來源為何處",
            " 我們主要從兩個部分取得\n" +
                    "台北捷運API: 台北捷運API提供了捷運的搭乘所需時間、到站時間等資訊，讓我們可以透過呼叫API取得所需的站點資訊。\n" +
                    "資料介接「交通部TDX平臺」" +
                    "運輸資料流通服務TDX API： 運輸資料流通服務TDX API提供了提供眾多公共交通運輸數據的API，我們使用了捷運的站點內設施、票價等資訊，" +
                    "讓我們可以透過呼叫API取得所需的站點資訊。\n"

        )
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_faq)
        supportActionBar?.hide()

        recyclerView = findViewById(R.id.FAQ_RecylerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        settingFAQRecylerViewAdapter = Setting_FAQ_RecylerViewAdapter(this, FAQList)
        recyclerView.adapter = settingFAQRecylerViewAdapter
        val goback=findViewById<LinearLayout>(R.id.goback)
        goback.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish() // 結束當前 activity
        }
    }
}