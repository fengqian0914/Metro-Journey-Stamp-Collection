package com.example.MRTAPP.UI

import android.Manifest
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.MRTAPP.UI.Cameras.MRT_Station_item
import com.example.MRTAPP.R
import com.example.MRTAPP.databinding.FragmentCameraBinding
import com.example.MRTAPP.UI.Cameras.recyclerViewAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import org.json.JSONObject


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var  binding:FragmentCameraBinding



/**
 * A simple [Fragment] subclass.
 * Use the [Camera.newInstance] factory method to
 * create an instance of this fragment.
 */
class Camera : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
//    private var activityResultLauncher: ActivityResultLauncher<Array<String>>
    lateinit var cameraManager: CameraManager
    lateinit var cameraCaptureSession:CameraDevice
    lateinit var cameraRequest: CaptureRequest
    lateinit var handler: Handler
    lateinit var handlerThread: HandlerThread
    lateinit var texttureView: TextView
    private lateinit var layout: View
    private lateinit var view: View
//  private  var stationFinish=false;
    private lateinit var databaseReference: DatabaseReference

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){
            isGranted :Boolean ->
            if(isGranted){
                showCamera(view)
            }else{
                    // Explain why you need permission
            }
        }
    private val scanLauncher=
        registerForActivityResult(ScanContract()){
                result:ScanIntentResult->
            run {
                if (result.contents == null) {
//                    Toast.makeText(this, "取消", Toast.LENGTH_SHORT).show()
                } else {
                    Resultverify(result.contents)
                }
            }
        }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        view = inflater.inflate(R.layout.fragment_camera, container, false)


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 創建並顯示確認對話框
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.backdialog_title))
                    .setIcon(R.drawable.logo)
                    .setMessage(getString(R.string.backdialog_msg))
                    .setPositiveButton(getString(R.string.backdialog_y)) { dialog, _ ->
                        // 使用者點選「是」時，關閉對話框並執行返回操作
                        dialog.dismiss()
                        requireActivity().finish() // 結束 Activity，關閉應用程式

                    }
                    .setNegativeButton(getString(R.string.backdialog_n)) { dialog, _ ->
                        // 使用者點選「否」時，僅關閉對話框
                        dialog.dismiss()
                    }
                    .show()
            }
        })
        val Loginshared = context?.getSharedPreferences("Login", Context.MODE_PRIVATE)
        val Guest = Loginshared?.getBoolean("Guest",false)
        if(Guest==true){
            val toastMessage = view.context.getString(R.string.after_login)


            view.findViewById<Button>(R.id.qrcodeScan_btn).apply {
                setOnClickListener {
                    Toast.makeText(view.context, toastMessage, Toast.LENGTH_LONG).show()
                }
            }

            listOf(
                R.id.BL_route, R.id.BR_route, R.id.R_route, R.id.G_route, R.id.O_route, R.id.Y_route
            ).forEach { id ->
                view.findViewById<CardView>(id).apply {
                    setOnClickListener {
                        Toast.makeText(view.context, toastMessage, Toast.LENGTH_LONG).show()
                        return@setOnClickListener
                    }
                }
            }

        }else {

            initBinding()
            val sharedPreferences_start =
                requireContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
            // 從 SharedPreferences 中讀取資料
            initViews(view)
            val BL_route = view.findViewById<CardView>(R.id.BL_route)
            val BR_route = view.findViewById<CardView>(R.id.BR_route)
            val G_route = view.findViewById<CardView>(R.id.G_route)
            val R_route = view.findViewById<CardView>(R.id.R_route)
            val Y_route = view.findViewById<CardView>(R.id.Y_route)
            val O_route = view.findViewById<CardView>(R.id.O_route)
            val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()

            setButtonClickListeners(
                BL_route to "BL",
                BR_route to "BR",
                G_route to "G",
                R_route to "R",
                Y_route to "Y",
                O_route to "O"
            ) { route ->
                recyclerview_Fun(route, userId)

            }
            // 在 Fragment 中讀取 SharedPreferences
            val sharedPreferences =
                requireContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE)


            val db = FirebaseFirestore.getInstance()

            val path = db.collection("users")
                .document(userId)
                .collection("StationData")
                .document("stationValue")

            path.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        // 將 stationValue 轉換為 Map<String, Int>
                        val stationData = documentSnapshot.data as? Map<String, Int>
                        if (stationData != null) {
                            routeValue(stationData.toString())

                        } else {
                            Log.d("Firestore", "stationValue 格式不正確或為空")
                        }
                    } else {
                        Log.d("Firestore", "stationValue 文檔不存在")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Firestore", "讀取失敗: ${exception.message}")
                }


            // 從 SharedPreferences 中讀取資料
//        val stationvalue = sharedPreferences.getString("stationValue", "default_value_value")
//        routeValue(stationvalue!!)
//        Log.d("title","stationvalue$stationvalue")
        }
        return view  //回傳view
    }
    private fun Resultverify(string: String) {
        val jsonObject = JSONObject(string)
        val qrDataObject = jsonObject.getJSONObject("QrData")
        val name = qrDataObject.getString("name")
        val verify = qrDataObject.optString("verify") // 使用 optString 進行空值檢查
        val station = JSONObject(qrDataObject.getString("station"))

        // 獲取所有鍵
        val keys = station.keys()

        while (keys.hasNext()) {
            val route = keys.next() // 獲取當前鍵
            Log.d("title", "鍵: $route") // 打印鍵
            // 如果你想要獲取該鍵對應的值
            val stationName = station.getString(route)
            Log.d("title", "值: $stationName") // 打印值

            Log.d("title", "!!!name$name station$station verify$verify }")
            val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
            // 讀取指定路徑的數據
            val db = FirebaseFirestore.getInstance()

            val stationPath = db.collection("users")
                .document(userId)
                .collection("StationData")
                .document("stations")



            stationPath.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // 檢查指定的路線(route)是否存在於文檔中
                        val stationData = documentSnapshot.data?.get(route) as? Map<*, *>

                        if (stationData != null && stationData.containsKey(stationName)) {
                            // 若站點存在，檢查其是否為 true 或 false
                            val stationValue = stationData[stationName] as? Boolean ?: false
                            if (stationValue) {
                                Toast.makeText(context, "無法登記，該站已抵達過", Toast.LENGTH_LONG).show()
                                Log.d("Firestore", "該站 $stationName 已經抵達過")
                            } else {
                                // 若為 false，則可以更新為 true 並顯示登記成功
                                updateStationValue(userId,route)
                                updateCoin(userId)
                                registerStation(userId, route, stationName)
                            }
                        } else {
                            Toast.makeText(context, "該站不存在", Toast.LENGTH_LONG).show()
                            Log.d("Firestore", "該站 $stationName 不存在")
                        }
                    } else {
                        Toast.makeText(context, "該文檔不存在", Toast.LENGTH_LONG).show()
                        Log.d("Firestore", "該文檔不存在")
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, "讀取失敗: ${exception.message}", Toast.LENGTH_LONG).show()
                    Log.d("Firestore", "讀取失敗: ${exception.message}")
                }

        }
        view.findViewById<TextView>(R.id.Qrcode_textResult).text = name


    }
    private fun registerStation(userId: String, route: String, stationKey: String) {
        val db = FirebaseFirestore.getInstance()

        // 定義 Firestore 路徑
        val stationPath = db.collection("users")
            .document(userId)
            .collection("StationData")
            .document("stations")

        // 準備更新的數據，將指定的站點更新為 true
        val updatedStationData = mapOf("$route.$stationKey" to true)

        // 更新 Firestore 中的站點數據
        stationPath.update(updatedStationData)
            .addOnSuccessListener {
                Toast.makeText(context, "登記成功，$stationKey 的值已更新為 true", Toast.LENGTH_LONG).show()
                Log.d("Firestore", "$stationKey 的值已更新為 true")
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "登記失敗: ${exception.message}", Toast.LENGTH_LONG).show()
                Log.d("Firestore", "登記失敗: ${exception.message}")
            }
    }
    private fun updateStationValue(userId:String,route:String) {

        val db = FirebaseFirestore.getInstance()

        // 定義 Firestore 路徑
        val stationPath = "users/$userId/StationData/stationValue"

        // 讀取指定路徑的數據
        db.document(stationPath).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // 獲取站點值的數據
                    val currentStationValue = documentSnapshot.data?.get(route) as? Long ?: 0 // 獲取具體站的當前值
                    val updatedStationValue = mapOf(route to currentStationValue + 1) // 更新站點的值為當前值 + 1
                    // 更新 Firestore
                    db.document(stationPath)
                            .update(updatedStationValue) // 更新指定站點的值
                            .addOnSuccessListener {
                                // 登記成功
                                Toast.makeText(context, "登記成功！", Toast.LENGTH_LONG).show()
                            }
                            .addOnFailureListener {
                                // 寫入失敗時的處理
                                Toast.makeText(context, "登記失敗: ${it.message}", Toast.LENGTH_LONG).show()
                                Log.d("title", "登記失敗: ${it.message}")
                            }

                } else {
                    // 如果該文檔不存在
                    Toast.makeText(context, "該文檔不存在", Toast.LENGTH_LONG).show()
                    Log.d("title", "該文檔不存在")
                }
            }
            .addOnFailureListener { exception ->
                // 處理讀取失敗錯誤
                Toast.makeText(context, "讀取失敗: ${exception.message}", Toast.LENGTH_LONG).show()
                Log.d("title", "讀取失敗: ${exception.message}")
            }
    }
    private fun updateCoin(userId:String) {
        var usercoin:Int=0
        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        usercoin = document.getLong("usercoin")!!.toInt()
                        // 將資料傳回
                        db.collection("users").document(userId)
                            .update("usercoin", usercoin+100)
                            .addOnSuccessListener {

                            }
                            .addOnFailureListener {
                            }
                    }
                }
                .addOnFailureListener { exception ->

                }
        }

    }
    private fun routeValue(stationvalue:String) {
        // 將 JSON 解析成 Map
        val map: Map<String, Int> = Gson().fromJson(stationvalue, Map::class.java) as Map<String, Int>
        for ((key, value) in map) {
            when (key.toString()){
                "BL" ->  {
                    view.findViewById<TextView>(R.id.BL_value).text=value.toString()
                    view.findViewById<ProgressBar>(R.id.BL_value_bar).progress=(value*100/23).toInt()
                }
                "BR" -> {
                    view.findViewById<TextView>(R.id.BR_value).text=value.toString()
                    view.findViewById<ProgressBar>(R.id.BR_value_bar).progress=(value*100/24).toInt()

                }
                "G" -> {
                    view.findViewById<TextView>(R.id.G_value).text=value.toString()
                    view.findViewById<ProgressBar>(R.id.G_value_bar).progress=(value*100/20).toInt()

                }
                "R" -> {
                    view.findViewById<TextView>(R.id.R_value).text=value.toString()
                    view.findViewById<ProgressBar>(R.id.R_value_bar).progress=(value*100/28).toInt()

                }
                "O" -> {
                    view.findViewById<TextView>(R.id.O_value).text=value.toString()
                    view.findViewById<ProgressBar>(R.id.O_value_bar).progress=(value*100/26).toInt()

                }
                "Y" -> {
                    view.findViewById<TextView>(R.id.Y_value).text=value.toString()
                    view.findViewById<ProgressBar>(R.id.Y_value_bar).progress=(value*100/14).toInt()

                }

            }

        }
        Log.d("title",stationvalue.toString())
    }

    fun setButtonClickListeners(vararg buttons: Pair<CardView, String>, onClick: (String) -> Unit) {
        for ((button, route) in buttons) {
            button.setOnClickListener { onClick(route) }
        }
    }
    private fun recyclerview_Fun(route:String,userId: String) {
        val window=PopupWindow(context)
        val views=layoutInflater.inflate(R.layout.activity_popup_route,null)
        window.contentView=views
        val dismiss_Btn=views.findViewById<Button>(R.id.dismiss_btn)
        val recyclerView=views.findViewById<RecyclerView>(R.id.route_recyclerView)
        val testData=ArrayList<MRT_Station_item>()
        window.isFocusable = true
        window.update()
        views.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                window.dismiss()
                true
            } else {
                false
            }
        }
        // 讀取json檔案
        val jsonString = context?.assets?.open("mrt_language.json")?.bufferedReader().use { it?.readText() }
        val json = JSONObject(JSONObject(jsonString)[route].toString())
        val jsonkeys = json.keys()

        val db = FirebaseFirestore.getInstance()
        val stationPath = db.collection("users")
            .document(userId)
            .collection("StationData")
            .document("stations")

        val sharedPreferences = requireContext().getSharedPreferences("Settings", MODE_PRIVATE)
        val savedLanguage = sharedPreferences.getString("My_Lang", "default_language")
        val savedCountry = sharedPreferences.getString("My_Country", "default_country")
        var language="zh_tw"
        when(savedCountry){
            "TW" -> language="Zh_tw"// zh-TW
            "CN" -> language="Zh-Hans" // zh-CN
            "US"-> language="En"
            "JP"->language="Ja"
            "KR"->language="Ko"
            else ->language="Zh_tw"
        }
        Log.d("language","language${language} savedLanguage${savedLanguage}")
        stationPath.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val stationData = documentSnapshot.data?.get(route) as? Map<*, *>
                    for (key in jsonkeys) {
                        val valueObject = json.optJSONObject(key)
                        val Translation = valueObject?.optString(language)

                        if (stationData != null && stationData.containsKey(key)) {
                            val stationCondition = stationData[key] as? Boolean ?: false
                            val status = if (stationCondition) "已完成" else "未完成"

                            // 將站點資料加入列表
                            testData.add(MRT_Station_item(route, Translation.toString(), status))


                        } else {
                            Log.d("Firestore", "站點 $key 不存在於路線 $route 中")
                        }
                    }

                    // 完成資料讀取後設置 RecyclerView 的適配器
                    val layoutManager = LinearLayoutManager(context)
                    val adapter = recyclerViewAdapter(testData, route)
                    recyclerView.layoutManager = layoutManager
                    recyclerView.adapter = adapter
                } else {
                    Toast.makeText(context, "該文檔不存在", Toast.LENGTH_LONG).show()
                    Log.d("Firestore", "該文檔不存在")
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "讀取失敗: ${exception.message}", Toast.LENGTH_LONG).show()
                Log.d("Firestore", "讀取失敗: ${exception.message}")
            }


        val camera_layout=view.findViewById<LinearLayout>(R.id.camera_layout)
        val All_width = Resources.getSystem().displayMetrics.widthPixels
        val All_height_margin = Resources.getSystem().displayMetrics.heightPixels
        val window_layout=layoutInflater.inflate(R.layout.item_route,null)

        window_layout.findViewById<TextView>(R.id.item_station_name).setTextColor(Color.RED)
        window.width=All_width.toInt()
        window.height=All_height_margin.toInt()
        dismiss_Btn.setOnClickListener {
            window.dismiss()
        }
        window.showAtLocation(camera_layout, Gravity.CENTER ,0,100) // 最後兩個參數為 x 和 y 偏移量
    }




    private fun showCamera(view: View) {
        val options=ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Scan QR code")
        options.setCameraId(0)
        options.setBeepEnabled(false)
        options.setBarcodeImageEnabled(true)
        options.setOrientationLocked(false)

        scanLauncher.launch(options)
    }

    private fun initViews(view: View) {
        view.findViewById<Button>(R.id.qrcodeScan_btn).setOnClickListener {
            checkPermissionCamera(this,view)
        }
    }

    private fun checkPermissionCamera(context: Camera,view: View) {
    // 這是透過 getActivity() 來取得 fragment 所在的 activity
        val context_Fragment = requireContext()


        if(ContextCompat.checkSelfPermission(context_Fragment,Manifest.permission.CAMERA )== PackageManager.PERMISSION_GRANTED){
            showCamera(view)
        }else if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
            Toast.makeText(activity,"必須開啟相機才可使用",Toast.LENGTH_SHORT).show()
        }else{
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun initBinding() {
            binding=FragmentCameraBinding.inflate(layoutInflater)

//            setContentView(binding.root)

    }

    companion object {
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Settings_Fragment.
     */
    @JvmStatic
    fun newInstance(param1: String, param2: String) =
        Settings_Fragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, param1)
                putString(ARG_PARAM2, param2)
            }
        }
}


}
