package com.example.MRTAPP.UI.Login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.MRTAPP.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class forget_password : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)
        supportActionBar?.hide()

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val ReviseBtn=findViewById<Button>(R.id.mail_btn)
        val EmailView=findViewById<TextView>(R.id.Setting_Email)
        ReviseBtn.setOnClickListener {
            val Email = EmailView.text.toString().trim()
            if(!TextUtils.isEmpty(Email)) {
                ResetPassword(Email)
            }else{
                Toast.makeText(this, this.getString(R.string.enter_new_name), Toast.LENGTH_SHORT).show()

            }
        }
        findViewById<LinearLayout>(R.id.goback).setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }
    private fun ResetPassword(email:String) {
        // 發送密碼重置郵件
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 發送成功
                    Log.d("PasswordReset", "重置密碼郵件已發送至: $email")
                    Toast.makeText(this, this.getString(R.string.reset_password_email_sent), Toast.LENGTH_SHORT).show()
                } else {
                    // 發送失敗
                    Log.d("PasswordReset", "密碼重置郵件發送失敗: ${task.exception?.message}")
                    Toast.makeText(this, this.getString(R.string.reset_password_email_failed), Toast.LENGTH_SHORT).show()
                }
            }
    }
}