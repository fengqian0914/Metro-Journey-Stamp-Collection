package com.example.MRTAPP.UI

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.iwgang.countdownview.CountdownView
import com.example.MRTAPP.R
import com.example.MRTAPP.UI.Mall.ProductList
import com.example.MRTAPP.UI.Mall.Product_RecyclerViewAdapter
import com.example.MRTAPP.UI.Star.Achievement_List
import com.example.MRTAPP.UI.Star.Achievement_RecyclerViewAdapter
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Objects
import java.util.TimeZone


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var countdownText: TextView
private var AchievementList = mutableListOf<Achievement_List>()
private var recyclerView:RecyclerView?=null
private var recyclerViewAdapter: Achievement_RecyclerViewAdapter?=null

private val handler = Handler(Looper.getMainLooper())

/**
 * A simple [Fragment] subclass.
 * Use the [Star_Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Star_Fragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: Achievement_RecyclerViewAdapter
    private val AchievementList = mutableListOf<Achievement_List>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_star_, container, false)

        // 初始化 RecyclerView
        recyclerView = view.findViewById(R.id.Achievement_RecyclerVIew)
        recyclerViewAdapter = Achievement_RecyclerViewAdapter(this, AchievementList)
        recyclerView.layoutManager = GridLayoutManager(context, 1)
        recyclerView.adapter = recyclerViewAdapter

        // 加載數據
        ListData()

        return view  // 回傳 view
    }




    private fun ListData() {
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection("Data").document("Achievement").collection("Item")
        val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        var existsquantity = 0

        // 使用 get() 進行單次資料讀取
        collectionRef.get().addOnSuccessListener { snapshot ->
            Log.e("Achievement_Fragment", "Snapshot contains data") // 測試日誌

            if (snapshot != null && !snapshot.isEmpty) {
                val tempAchievementList = mutableListOf<Achievement_List>()
                val stationPath = db.collection("users")
                    .document(userId)
                    .collection("StationData")
                    .document("stations")

                val DataObject = JSONObject()
                val achievementTasks = mutableListOf<Task<Void>>() // 用於追蹤所有的 Firebase 異步操作

                for (AchievementSnapshot in snapshot.documents) {
                    val achievementId = AchievementSnapshot.id
                    val achievementName = AchievementSnapshot.getString("Name").toString()

                    val StationData = AchievementSnapshot.get("Station") as? Map<String, Any> ?: emptyMap()
                    val StationObject = JSONObject(StationData)

                    val keys = StationObject.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        val value = StationObject.get(key)
                        val route = when {
                            key.contains("BL") -> "BL"
                            key.contains("BR") -> "BR"
                            key.contains("R") -> "R"
                            key.contains("G") -> "G"
                            key.contains("O") -> "O"
                            key.contains("Y") -> "Y"
                            else -> continue
                        }

                        val stationTask: Task<Void> = stationPath.get()
                            .addOnSuccessListener { documentSnapshot ->
                                if (documentSnapshot.exists()) {
                                    val stationData = documentSnapshot.data?.get(route) as? Map<*, *>

                                    if (stationData != null && stationData.containsKey(key)) {
                                        val stationValue = stationData[key] as? Boolean ?: false
                                        if (stationValue) {
                                            existsquantity += 1 // 更新 existsquantity
                                            val stationDetails = JSONObject().apply {
                                                put("Name", value)
                                                put("exists", true)
                                            }
                                            StationObject.put(key, stationDetails)

                                        } else {
                                            val stationDetails = JSONObject().apply {
                                                put("Name", value)
                                                put("exists", false)
                                            }
                                            StationObject.put(key, stationDetails)
                                        }
                                    } else {
                                        Log.d("Firestore", "該站不存在")
                                    }
                                } else {
                                    Log.d("Firestore", "該文檔不存在")
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.d("Firestore", "讀取失敗: ${exception.message}")
                            }
                            .continueWithTask { Tasks.forResult(null) } // 確保轉換為 Task<Void>

                        achievementTasks.add(stationTask) // 添加到任務列表中
                    }

                    val levelData = AchievementSnapshot.get("Level") as? Map<String, Any> ?: emptyMap()
                    val LevelBronze = levelData["Bronze"] as? Map<String, Any> ?: emptyMap()
                    val LevelSilver = levelData["Silver"] as? Map<String, Any> ?: emptyMap()
                    val LevelGold = levelData["Gold"] as? Map<String, Any> ?: emptyMap()
                    val BronzeUri = LevelBronze["ImageUrl"] as? String ?: ""
                    val SilverUri = LevelSilver["ImageUrl"] as? String ?: ""
                    val GoldUri = LevelGold["ImageUrl"] as? String ?: ""

                    val ImageJSONObject = JSONObject().apply {
                        put("Bronze", BronzeUri)
                        put("Silver", SilverUri)
                        put("Gold", GoldUri)
                    }

                    // 等所有異步操作完成後執行
                    Tasks.whenAllComplete(achievementTasks).addOnCompleteListener {
                        // 所有異步操作完成後再添加 Achievement_List 項目
                        val achievement = Achievement_List(
                            id = achievementId,
                            Name = achievementName,
                            station = StationObject,
                            level = levelData,
                            Image = ImageJSONObject,
                            existsquantity = existsquantity
                        )
                        existsquantity = 0
                        tempAchievementList.add(achievement)
                        Log.e("Achievement_Fragment", "Achievement loaded: ${achievement}") // 測試日誌

                        // 更新主列表並通知 RecyclerView
                        AchievementList.clear()
                        AchievementList.addAll(tempAchievementList)
                        recyclerViewAdapter?.notifyDataSetChanged()
                    }
                }
            } else {
                Log.d("Achievement_Fragment", "No data found in snapshot") // 測試日誌
            }
        }.addOnFailureListener { e ->
            Log.w("Firestore", "獲取成就資料失敗", e)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Star_Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
