package com.example.MRTAPP.UI.Setting.User

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.MRTAPP.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Setting_Fragment_Tab_Password.newInstance] factory method to
 * create an instance of this fragment.
 */
class Setting_Fragment_Tab_Password : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
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
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_setting_tab_password, container, false)

        storage = FirebaseStorage.getInstance()
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val ReviseBtn=view.findViewById<Button>(R.id.mail_btn)
        val EmailView=view.findViewById<TextView>(R.id.Setting_Email)
        ReviseBtn.setOnClickListener {
            val Email = EmailView.text.toString().trim()
            if(!TextUtils.isEmpty(Email)) {
                ResetPassword(Email)
            }else{
                Toast.makeText(requireContext(), context?.getString(R.string.enter_new_name), Toast.LENGTH_SHORT).show()

            }
        }

        return  view
    }
    private fun ResetPassword(email:String) {
        // 發送密碼重置郵件
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 發送成功
                    Log.d("PasswordReset", "重置密碼郵件已發送至: $email")
                    Toast.makeText(context, this.getString(R.string.reset_password_email_sent), Toast.LENGTH_SHORT).show()
                } else {
                    // 發送失敗
                    Log.d("PasswordReset", "密碼重置郵件發送失敗: ${task.exception?.message}")
                    Toast.makeText(context, context?.getString(R.string.reset_password_email_failed), Toast.LENGTH_SHORT).show()
                }
            }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Setting_Fragment_Tab_Password.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Setting_Fragment_Tab_Password().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}