package com.example.MRTAPP.UI.Setting.Language

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import com.example.MRTAPP.UI.Login.Login
import com.example.MRTAPP.UI.Home.MainActivity
import com.example.MRTAPP.R
import java.util.Locale

class Setting_Language : AppCompatActivity() {
    private lateinit var radioGroup: RadioGroup
    private lateinit var changeLanguageButton: Button
    private lateinit var radioButtonZh: RadioButton
    private lateinit var radioButtonCn: RadioButton
    private lateinit var radioButtonUs: RadioButton
    private lateinit var radioButtonJp: RadioButton
    private lateinit var radioButtonKo: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_language)

        supportActionBar?.hide()
        // 绑定 UI 元素
        radioGroup = findViewById(R.id.radio_language_Group)
        changeLanguageButton = findViewById(R.id.btn_change_language)
        radioButtonZh = findViewById(R.id.radioButton_zh)
        radioButtonCn = findViewById(R.id.radioButton_cn)
        radioButtonUs = findViewById(R.id.radioButton_us)
        radioButtonJp = findViewById(R.id.radioButton_jp)
        radioButtonKo = findViewById(R.id.radioButton_ko)

        val goback=findViewById<LinearLayout>(R.id.goback)
        goback.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish() // 結束當前 activity
        }

        // 當點擊更改語言按鈕時
        changeLanguageButton.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId  // 獲取選中的 RadioButton ID
            // 根據選擇的 RadioButton 設定相應的語言
            val locale = when (selectedId) {
                R.id.radioButton_zh -> Locale("zh", "TW") // 繁體中文
                R.id.radioButton_cn -> Locale("zh", "CN") // 簡體中文
                R.id.radioButton_us -> Locale("en", "US") // 英文
                R.id.radioButton_jp -> Locale("ja", "JP") // 日文
                R.id.radioButton_ko -> Locale("ko", "KR") // 韓文
                else -> Locale.getDefault()  // 默認語言
            }
            setLocale(locale)  // 設定語言
        }

    }
    // 設定語言並儲存
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
}