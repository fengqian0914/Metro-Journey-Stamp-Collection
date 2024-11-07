package com.example.MRTAPP.UI

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.MRTAPP.UI.Login.Login
import com.example.MRTAPP.R
import com.example.MRTAPP.UI.Setting.About_Us.Setting_About_Us
import com.example.MRTAPP.UI.Setting.Coupons.Coupons_data
import com.example.MRTAPP.UI.Setting.FAQ.Setting_FAQ
import com.example.MRTAPP.UI.Setting.Language.Setting_Language
import com.example.MRTAPP.UI.Setting.Other.Setting_Other
import com.example.MRTAPP.UI.Setting.Station.Setting_station
import com.example.MRTAPP.UI.Setting.User.Personal_information
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private var nightMode:Boolean=false;

/**
 * A simple [Fragment] subclass.
 * Use the [Settings_Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Settings_Fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var editor:SharedPreferences.Editor?=null
    private var sharedPreferences:SharedPreferences?=null
    private lateinit var databaseReference: DatabaseReference

    private lateinit var storage: FirebaseStorage
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {






        val view = inflater.inflate(R.layout.fragment_settings_, container, false)

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
//        ShowUserData(view)
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            getUserData(view,userId)
        }

        val sharedPreferences = context?.getSharedPreferences("Login", Context.MODE_PRIVATE)
        val Guest = sharedPreferences?.getBoolean("Guest",false)
        val User_btn = view.findViewById<LinearLayout>(R.id.setting_user_btn)
        val Station_btn = view.findViewById<LinearLayout>(R.id.setting_Station_btn)
        val Coupon_btn = view.findViewById<LinearLayout>(R.id.setting_Coupon_btn)
        val Language_btn = view.findViewById<LinearLayout>(R.id.setting_language_btn)
        val Darkmode_btn = view.findViewById<LinearLayout>(R.id.setting_darkmode_btn)
        val FAQ_btn = view.findViewById<LinearLayout>(R.id.setting_FAQ_btn)
        val About_btn = view.findViewById<LinearLayout>(R.id.setting_about_btn)
        val Other_btn = view.findViewById<LinearLayout>(R.id.setting_other_btn)
        val Logout_btn = view.findViewById<LinearLayout>(R.id.setting_logout_btn)

        if(Guest==true){
            val toastMessage = view.context.getString(R.string.after_login)

            User_btn.setOnClickListener {
                Toast.makeText(view.context, toastMessage, Toast.LENGTH_LONG).show()
            }
            Coupon_btn.setOnClickListener {
                Toast.makeText(view.context, toastMessage, Toast.LENGTH_LONG).show()
            }
            view.findViewById<TextView>(R.id.user_name).text=view.context.getString(R.string.Guest_Title)
            view.findViewById<TextView>(R.id.user_coin).text="0"


        }else {
            getUserData(view, userId.toString())

            User_btn.setOnClickListener {
                val intent = Intent(context, Personal_information::class.java)
                startActivity(intent)
            }
            Coupon_btn.setOnClickListener {
                val intent = Intent(context, Coupons_data::class.java)
                startActivity(intent)
            }

            editor?.apply()
        }

        Station_btn.setOnClickListener {
            val intent = Intent(context, Setting_station::class.java)
            startActivity(intent)
        }
        val currentNightMode =
            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_YES -> {
                // 黑暗模式
                nightMode = true
                Log.d("ModeCheck", "目前是黑暗模式")
            }

            Configuration.UI_MODE_NIGHT_NO -> {
                // 白模式（淺色模式）
                nightMode = false
                Log.d("ModeCheck", "目前是白模式")
            }

            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                // 未定義的模式
                Log.d("ModeCheck", "目前模式未定義")
            }
        }


        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        Darkmode_btn.setOnClickListener {
            if (nightMode) {//是否開啟黑暗
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                editor = sharedPreferences?.edit()
                nightMode = false;
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                editor = sharedPreferences?.edit()
                nightMode = true;
            }
        }
        Language_btn.setOnClickListener {
            val intent = Intent(context, Setting_Language::class.java)
            startActivity(intent)
        }

        FAQ_btn.setOnClickListener {
            val intent = Intent(context, Setting_FAQ::class.java)
            startActivity(intent)
        }
        About_btn.setOnClickListener {
            val intent = Intent(context, Setting_About_Us::class.java)
            startActivity(intent)
        }
        Other_btn.setOnClickListener {
            val intent = Intent(context, Setting_Other::class.java)
            startActivity(intent)
        }

        Logout_btn.setOnClickListener {
            val sharedPreferences =
                requireContext().getSharedPreferences("Login", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("account", null)
            editor.putString("password", null)
            editor.putBoolean("Guest",false)

            editor.apply()
            val intent = Intent(activity, Login::class.java)
            startActivity(intent)


            clearAllSharedPreferences()

        }


        return view
    }

    private fun clearAllSharedPreferences() {
        // 列出所有需要清除的 SharedPreferences 名稱
        val sharedPrefsNames = listOf("Login", "tdx", "my_prefs","stationInfo","AccessToken") // 根據實際專案情況增減

        for (prefName in sharedPrefsNames) {
            val sharedPreferences = requireContext().getSharedPreferences(prefName, Context.MODE_PRIVATE)
            sharedPreferences.edit().clear().apply()
        }
        // 登出 Firebase 使用者
        FirebaseAuth.getInstance().signOut()

    }

    override fun onResume() {
        super.onResume()

        // 使用 fragment 的現有視圖
        val view = view

        // 確保 view 不為空
        if (view != null) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                getUserData(view, userId)  // 使用已經膨脹的視圖來顯示數據
            }
        }
    }

    private fun getUserData(view:View,userId:String) {
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val userName = document.getString("userName")
                    val userCoin = document.getLong("usercoin")?.toInt()
                    val imageUrl = document.getString("profileImageUrl")

                    val imageView = view.findViewById<ImageView>(R.id.Photo_stickers)

                    if (imageUrl != null) {
                        Picasso.get()
                            .load(imageUrl)
                            .placeholder(R.drawable.default_photo) // 可選的占位符
                            .error(R.drawable.default_photo) // 可選的錯誤圖片
                            .into(imageView)
                    }

                    view.findViewById<TextView>(R.id.user_name).text=userName
                    view.findViewById<TextView>(R.id.user_coin).text=userCoin.toString()
                    // 將資料傳回
                } else {
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(),context?.getString(R.string.get_user_data_failed), Toast.LENGTH_SHORT).show()
            }
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
        // TODO: Rename and change types and number of parameters
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