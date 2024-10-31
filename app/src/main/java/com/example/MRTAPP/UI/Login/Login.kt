package com.example.MRTAPP.UI.Login

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.MRTAPP.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.content.Context
import android.os.Build
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.airbnb.lottie.LottieAnimationView
import com.example.MRTAPP.API.TDX_API
import com.example.MRTAPP.UI.Home.MainActivity
import com.example.MRTAPP.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.Locale


class Login : AppCompatActivity() {
    private lateinit var binding:ActivityLoginBinding
   private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var storage: FirebaseStorage
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 初始化 Firebase
        storage = FirebaseStorage.getInstance()
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

            val animationView =
                findViewById<LottieAnimationView>(R.id.animation_view) // 獲取 LottieAnimationView
            animationView.setAnimation(R.raw.anis)
            animationView.loop(false)  // 確保只播放一次

            // 淡入動畫
            val fadeIn = ObjectAnimator.ofFloat(animationView, "alpha", 0f, 1f)
            fadeIn.duration = 500 // 設置淡入時間

            fadeIn.start() // 開始淡入動畫

            // 添加動畫結束的監聽器
            animationView.addAnimatorListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)

                    // 淡出動畫
                    val fadeOut = ObjectAnimator.ofFloat(animationView, "alpha", 1f, 0f)
                    fadeOut.duration = 500 // 設置淡出時間

                    fadeOut.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            animationView.visibility = View.GONE // 動畫播放結束後隱藏
                        }
                    })

                    fadeOut.start() // 開始淡出動畫
                }
            })

            animationView.playAnimation() // 開始播放動畫


        binding.btnLogin.setOnClickListener {
            val account=binding.loginAccount.text.toString()
            val password=binding.loginPassword.text.toString()
            try {
                if (account.isNotEmpty() && password.isNotEmpty()) {
                    loginUser(account, password)
                } else {
                    Toast.makeText(this@Login, "所有欄位皆必填", Toast.LENGTH_LONG).show()
                }
            }
            catch (e:Exception){
                Log.d("LoginError","1:${e.message}")
            }
        }
        binding.btnLoginRegister.setOnClickListener {
            val  intent= Intent(this, Register::class.java)
            startActivity(intent)
        }
        binding.forgetPassword.setOnClickListener {
            val  intent= Intent(this, forget_password::class.java)
            startActivity(intent)
        }



    }


    //
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        signInResultLauncher.launch(signInIntent)
    }

    private val signInResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleSignInResult(task)
        }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.result
            firebaseAuthWithGoogle(account.idToken!!)
            Log.d("FireBase_user","account${account}")

        } catch (e: Exception) {
            // 處理錯誤
            Log.d("FireBase_user","ERROR1 ${e}")

        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 登入成功
                    val user = firebaseAuth.currentUser
                    Log.d("FireBase_user",user.toString())
                } else {
                    // 登入失敗
                    Log.d("FireBase_user","ERROR")

                }
            }
    }
    private fun saveUserData(user: FirebaseUser) {
        val db = FirebaseFirestore.getInstance()
        val userData = hashMapOf(
            "uid" to user.uid,
            "name" to user.displayName,
            "email" to user.email
        )

        db.collection("users").document(user.uid)
            .set(userData)
            .addOnSuccessListener {
                // 儲存成功
            }
            .addOnFailureListener { e ->
                // 儲存失敗
            }
    }



    //


    // 定義 Kotlin 對象來表示 JSON 數據
    data class StationStatus(
        val BR: Map<String, Boolean>,
        val R: Map<String, Boolean>,
        val G: Map<String, Boolean>,
        val Y: Map<String, Boolean>,
        val BL: Map<String, Boolean>,
        val O: Map<String, Boolean>
    );
    data class StationValue(
        val BR:  Int,
        val R: Int,
        val G: Int,
        val Y: Int,
        val BL:Int,
        val O: Int,
    )
    private fun loginUser(account:String,password:String){
        try {
            auth.signInWithEmailAndPassword(account, password)
                .addOnCompleteListener(this) { task ->

                    if (task.isSuccessful) {
//                    // 登入成功
                    val user = auth.currentUser
//                    // 可進行其他操作，例如跳轉頁面
//                  Toast.makeText(this@Login, "@string/loginText", Toast.LENGTH_LONG).show()
                    loadLocale() //載入語言包

                    // 存入預先輸入
                    val sharedPreferences_login = getSharedPreferences("Login", Context.MODE_PRIVATE)
                    val editor_login = sharedPreferences_login.edit()
                    editor_login.putString("account", account)
                    editor_login.putString("password", password)
                    editor_login.apply()

                    // API token
                    val tdxApi = TDX_API(this@Login)
                    tdxApi.getAccessToken { response ->
                        if (response != null) {
                            println("API 回應：$response")
                            Log.d("title","LoginLayout:${response}")
                            // 取得 SharedPreferences 實例
                            val tdx_sharedPreferences = getSharedPreferences("tdx", Context.MODE_PRIVATE)

                            // 編輯 SharedPreferences
                            val tdx_editor =  tdx_sharedPreferences.edit()
                            tdx_editor.putString("AccessToken",response)

                            tdx_editor.apply()

                        } else {
                            println("呼叫 API 失敗")
                        }
                    }
                    startActivity(Intent(this@Login, MainActivity::class.java))
                    finish()


                    } else {
                        // 登入失敗，顯示錯誤訊息
                        Toast.makeText(
                            this,
                            "登入失敗：${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
        catch (e:Exception){
            Log.d("LoginError","2:${e.message}")
        }

    }

    override fun onStart() {
        super.onStart()
        val sharedPreferences_login = getSharedPreferences("Login", Context.MODE_PRIVATE)
        val account = sharedPreferences_login.getString("account", null)
        val password = sharedPreferences_login.getString("password", null)
        Log.d("UserData","${account}....${password}")
        if (account != null && password != null) {
            loginUser(account,password)

        }
        Log.d("title","Login username$account password$password")

    }
    private fun setLocale(locale: Locale) {
        val config = resources.configuration  // 獲取當前的配置

        // 根據 Android 版本，設定語言
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
        }

        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)  // 更新配置

        // 儲存用戶選擇的語言到 SharedPreferences
        val editor = getSharedPreferences("Settings", MODE_PRIVATE).edit()
        editor.putString("My_Lang", locale.language)
        editor.putString("My_Country", locale.country)
        editor.apply()

        // 重新啟動 Activity 來使更改生效
        restartActivity()
    }
    // 重新啟動當前的 Activity
    private fun restartActivity() {
        val intent = Intent(this, Login::class.java)  // 創建一個新的 Intent
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)  // 清除當前 Activity 堆棧
        startActivity(intent)  // 啟動 Activity
        finish()  // 結束當前 Activity
    }

    // 加載已儲存的語言設定
    private fun loadLocale() {
        val sharedPreferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val language = sharedPreferences.getString("My_Lang", Locale.getDefault().language)
        val country = sharedPreferences.getString("My_Country", Locale.getDefault().country)
        val locale = Locale(language ?: Locale.getDefault().language, country ?: Locale.getDefault().country)
        setLocale(locale)  // 設定語言
    }

}