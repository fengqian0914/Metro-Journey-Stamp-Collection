package com.example.MRTAPP.UI.Login

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.MRTAPP.Data.MRTStationData
import com.example.MRTAPP.R
import com.example.MRTAPP.databinding.ActivityRegisterBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.util.UUID

class Register : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var storage: FirebaseStorage
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var imageView: ImageView

    private var imageUri: Uri? = null // 初始化為 null，表示尚未選擇圖片

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding =ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 初始化 Firebase
        storage = FirebaseStorage.getInstance()
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // 設定預設圖片的 URI
        val defaultImageUri = Uri.parse("android.resource://${packageName}/drawable/default_photo")

        imageView = findViewById(R.id.Photo_stickers)
        val selectButton = findViewById<Button>(R.id.selectImageButton)
        val registerButton = findViewById<Button>(R.id.register_btn)
        val emailInput = findViewById<TextInputEditText>(R.id.user_email)
        val nameInput = findViewById<TextInputEditText>(R.id.user_name)
        val passwordInput = findViewById<TextInputEditText>(R.id.register_password)
        val registerPasswordRepeat = findViewById<TextInputEditText>(R.id.register_password_repeat)
        val errorText=findViewById<TextView>(R.id.register_error_text)
        val usercoin = 500 // 預設金幣


        val goback=findViewById<LinearLayout>(R.id.goback)

        goback.setOnClickListener {
            val  intent= Intent(this, Login::class.java)
            startActivity(intent)
        }

        // 選擇圖片
        selectButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1000)
        }


        // 註冊按鈕的點擊事件
        registerButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val name = nameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val password_Repeat = registerPasswordRepeat.text.toString().trim()

            // 確認是否有輸入信箱、名稱和密碼
            if (email.isEmpty() || name.isEmpty() || password.isEmpty()||password_Repeat.isEmpty()) {
                Toast.makeText(this, this.getString(R.string.input_all_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(password!=password_Repeat) {
                errorText.setVisibility(View.VISIBLE)
                errorText.text="兩次密碼不一致，請重新輸入"
                return@setOnClickListener
            }
            // 呼叫註冊功能
            errorText.setVisibility(View.GONE)

            registerUser(email, name, password, usercoin,defaultImageUri)
        }
    }
    private fun registerUser(email: String, name: String, password: String, usercoin: Int, defaultImageUri: Uri) {
        // 使用 email 和 password 創建新用戶
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 註冊成功，獲取使用者 ID
                    val userId = auth.currentUser?.uid.toString()
                    if (userId != null) {
                        // 建立使用者資料的 HashMap
                        val userMap = hashMapOf(
                            "userId" to userId,
                            "userName" to name,
                            "email" to email,
                            "usercoin" to usercoin,
                        )
                        // 將使用者資料儲存到 Firestore
                        db.collection("users").document(userId)
                            .set(userMap)
                            .addOnSuccessListener {
                                Toast.makeText(this, this.getString(R.string.registration_success), Toast.LENGTH_SHORT).show()
                                // 上傳使用者圖片
                                uploadImageToFirebase(imageUri ?: defaultImageUri) // 使用預設圖片
                                startActivity(Intent(this@Register, Login::class.java))
                                addStation_value(userId)
                                finish()

                            }
                            .addOnFailureListener {
                                Toast.makeText(this, R.string.save_user_data_failed, Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    // 註冊失敗，顯示錯誤訊息
                    if (task.exception?.message=="The email address is already in use by another account.") {
                        // 郵箱已經被註冊過
                        Toast.makeText(this, this.getString(R.string.email_already_registered), Toast.LENGTH_SHORT).show()
                    }else{ // 顯示其他錯誤訊息
                        Toast.makeText(this, this.getString(R.string.registration_failed), Toast.LENGTH_SHORT).show()

                    }

                }
            }
    }

    private fun addStation_value(userId:String) {


        // 使用 Activity 的上下文來訪問 assets
        val inputStream: InputStream = baseContext.assets.open("mrt_station.json")
        val json = inputStream.bufferedReader().use { it.readText() }

        // 將 JSON 字串解析為 JSONObject
        val stationData = JSONObject(json)
        AchievementData(userId)

        // 獲取站點資料
        val stations = stationData.getJSONObject("station").toMap().toTypedMap()
        val stationsValue = MRTStationData.stationValue

        // 建立 StationData 集合
        val stationDataRef = db.collection("users").document(userId).collection("StationData")

        // 建立 stations 子集合
        stationDataRef.document("stations").set(stations)
            .addOnSuccessListener {
                Log.d(TAG, "Stations 成功寫入！")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "寫入 Stations 出錯", e)
            }

        // 建立 stationValue 子集合
        stationDataRef.document("stationValue").set(stationsValue)
            .addOnSuccessListener {
                Log.d(TAG, "StationValue 成功寫入！")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "寫入 StationValue 出錯", e)
            }
    }

    private fun AchievementData(userId: String) {
        // 初始化 Firestore
        val db = FirebaseFirestore.getInstance()

        // 讀取 `/Data/Achievement/Item/` 集合的項目長度
        db.collection("/Data/Achievement/Item")
            .get()
            .addOnSuccessListener { result ->
                val itemCount = result.size() // 獲取集合的文檔數量
                println("Item count: $itemCount")

                // 構建巢狀結構的初始資料
                val achievementData = mutableMapOf<String, Map<String, Boolean>>()
                for (i in 1..itemCount) {
                    val key = "Achievement%02d".format(i) // 格式化鍵，例如 "Achievement01"
                    achievementData[key] = mapOf(
                        "Bronze" to false,
                        "Silver" to false,
                        "Gold" to false
                    )
                }

                // 將資料寫入 `/users/a0T5uR8cfhVBIVmrTnxOHkpo47m2/Achievement/Items/`
                db.document("/users/a0T5uR8cfhVBIVmrTnxOHkpo47m2/Achievement/Items")
                    .set(achievementData)
                    .addOnSuccessListener {
                        println("Document successfully written!")
                    }
                    .addOnFailureListener { e ->
                        println("Error writing document: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                println("Error reading items: ${e.message}")
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data // 獲取選擇的圖片 URI
            imageView.setImageURI(imageUri)  // 顯示選擇的圖片
        }
    }

    // 將圖片上傳到 Firebase Storage
    private fun uploadImageToFirebase(fileUri: Uri) {
        val userId = auth.currentUser?.uid ?: return
        val storageRef = storage.reference.child("user_images/$userId/${UUID.randomUUID()}.jpg")
        storageRef.putFile(fileUri)
            .addOnSuccessListener {
                // 獲取下載 URL 並存入 Firestore
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    saveImageUrlToFirestore(downloadUrl)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, this.getString(R.string.image_upload_failed), Toast.LENGTH_SHORT).show()
            }
    }

    // 將圖片下載 URL 存入 Firestore
    private fun saveImageUrlToFirestore(downloadUrl: String) {
        val userId = auth.currentUser?.uid ?: return
        // 將 HashMap<String, String> 改為 HashMap<String, Any>
        val userMap = hashMapOf<String, Any>(
            "profileImageUrl" to downloadUrl
        )

        db.collection("users").document(userId)
            .update(userMap)
            .addOnSuccessListener {
                Toast.makeText(this, this.getString(R.string.image_saved_successfully), Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, this.getString(R.string.image_save_failed), Toast.LENGTH_SHORT).show()
            }
    }

    // 將 JSONObject 轉換為 Map
    fun JSONObject.toMap(): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>()
        val keys = keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val value = get(key)
            map[key] = when (value) {
                is JSONObject -> value.toMap()
                is JSONArray -> value.toList()
                else -> value
            }
        }
        return map
    }

    // 將 Map 轉換為 Typed Map
    fun Map<String, Any?>.toTypedMap(): Map<String, Map<String, Boolean>> {
        return this.mapValues { entry ->
            (entry.value as? Map<String, Any?>)?.mapValues { it.value as? Boolean ?: false } ?: emptyMap()
        }
    }

    // 將 JSONArray 轉換為 List
    fun JSONArray.toList(): List<Any?> {
        val list = mutableListOf<Any?>()
        for (i in 0 until length()) {
            list.add(get(i))
        }
        return list
    }
}