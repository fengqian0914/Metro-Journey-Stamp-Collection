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
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLocale() // 確保在登入前載入語言設定
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化 Firebase
        storage = FirebaseStorage.getInstance()
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        setupAnimation()

        binding.btnLogin.setOnClickListener {
            val account = binding.loginAccount.text.toString()
            val password = binding.loginPassword.text.toString()
            try {
                if (account.isNotEmpty() && password.isNotEmpty()) {
                    loginUser(account, password)
                } else {
                    Toast.makeText(this@Login, this.getString(R.string.input_all_fields), Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.d("LoginError", "1:${e.message}")
            }
        }

        binding.btnLoginRegister.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
        }

        binding.forgetPassword.setOnClickListener {
            startActivity(Intent(this, forget_password::class.java))
        }

        binding.GuestLogin.setOnClickListener {
            val sharedPreferences_login = getSharedPreferences("Login", Context.MODE_PRIVATE)
            val editor_login = sharedPreferences_login.edit()
            editor_login.putBoolean("Guest", true)
            editor_login.apply()
            navigateToMainActivity()
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
    private fun loginUser(account: String, password: String) {
        try {
            auth.signInWithEmailAndPassword(account, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // 登入成功
                        val user = auth.currentUser
                        saveLoginDetails(account, password)
                        fetchApiToken()
                        navigateToMainActivity()
                    } else {
                        // 登入失敗
                        Toast.makeText(this, "登入失敗：${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        } catch (e: Exception) {
            Log.d("LoginError", "2:${e.message}")
        }
    }

    private fun saveLoginDetails(account: String, password: String) {
        val sharedPreferences_login = getSharedPreferences("Login", Context.MODE_PRIVATE)
        val editor_login = sharedPreferences_login.edit()
        editor_login.putString("account", account)
        editor_login.putString("password", password)
        editor_login.apply()
    }

    private fun fetchApiToken() {
        val tdxApi = TDX_API(this@Login)
        tdxApi.getAccessToken { response ->
            if (response != null) {
                Log.d("API Response", "LoginLayout: $response")
                val tdx_sharedPreferences = getSharedPreferences("tdx", Context.MODE_PRIVATE)
                val tdx_editor = tdx_sharedPreferences.edit()
                tdx_editor.putString("AccessToken", response)
                tdx_editor.apply()
            } else {
                Log.d("API Response", "呼叫 API 失敗")
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this@Login, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun setupAnimation() {
        val animationView = findViewById<LottieAnimationView>(R.id.animation_view)
        animationView.setAnimation(R.raw.anis)
        animationView.loop(false)
        val fadeIn = ObjectAnimator.ofFloat(animationView, "alpha", 0f, 1f).apply {
            duration = 500
        }
        fadeIn.start()

        animationView.addAnimatorListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                val fadeOut = ObjectAnimator.ofFloat(animationView, "alpha", 1f, 0f).apply {
                    duration = 500
                }
                fadeOut.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        animationView.visibility = View.GONE
                    }
                })
                fadeOut.start()
            }
        })

        animationView.playAnimation()
    }

    private fun setLocale(locale: Locale) {
        val config = resources.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
        }
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)

        val editor = getSharedPreferences("Settings", MODE_PRIVATE).edit()
        editor.putString("My_Lang", locale.language)
        editor.putString("My_Country", locale.country)
        editor.apply()
    }

    private fun loadLocale() {
        val sharedPreferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val language = sharedPreferences.getString("My_Lang", Locale.getDefault().language)
        val country = sharedPreferences.getString("My_Country", Locale.getDefault().country)
        val locale = Locale(language ?: Locale.getDefault().language, country ?: Locale.getDefault().country)
        setLocale(locale)
    }
}
