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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Setting_Fragment_Tab_Name.newInstance] factory method to
 * create an instance of this fragment.
 */
class Setting_Fragment_Tab_Name : Fragment() {
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
        val view=inflater.inflate(R.layout.fragment_setting_tab_name, container, false)
        // Inflate the layout for this fragment
        val ReviseBtn=view.findViewById<Button>(R.id.Revise_btn)
        val NewNameView=view.findViewById<TextView>(R.id.user_name)
        // 初始化 Firebase
        storage = FirebaseStorage.getInstance()
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()



        ReviseBtn.setOnClickListener {
            val NewNameText = NewNameView.text.toString().trim()
            if(!TextUtils.isEmpty(NewNameText)) {
                val userId = auth.currentUser?.uid.toString()
                Log.d("SSSS", "userId${userId}")
                db.collection("users").document(userId)
                    .update("userName", NewNameText)
                    .addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            "成功更改名字為${NewNameText}",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "更改名字失敗", Toast.LENGTH_SHORT).show()
                    }
            }else{
                Toast.makeText(requireContext(), "請輸入欲更改名稱", Toast.LENGTH_SHORT).show()

            }



        }





        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Setting_Fragment_Tab_Name.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Setting_Fragment_Tab_Name().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}