package com.example.MRTAPP.UI.Setting.User

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.example.MRTAPP.UI.Home.MainActivity
import com.example.MRTAPP.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Setting_Fragment_Tab_Image.newInstance] factory method to
 * create an instance of this fragment.
 */
class Setting_Fragment_Tab_Image : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var storage: FirebaseStorage
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var imageUri: Uri? = null // 初始化為 null，表示尚未選擇圖片
    private lateinit var imageView: ImageView

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
        val view =inflater.inflate(R.layout.fragment_setting_tab_image, container, false)

        storage = FirebaseStorage.getInstance()
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid.toString()

        val selectImageBtn=view.findViewById<Button>(R.id.selectImageButton)
        val submitBtn=view.findViewById<Button>(R.id.Image_btn)
        imageView = view.findViewById(R.id.Photo_stickers)

        val defaultImageUri = Uri.parse("android.resource://${requireContext().packageName}/drawable/default_photo")

        selectImageBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1000)
        }
        submitBtn.setOnClickListener {

            db.collection("users").document(userId)
                .update("profileImageUrl",imageUri)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "更改成功", Toast.LENGTH_SHORT).show()
                    // 上傳使用者圖片
                    uploadImageToFirebase(imageUri ?: defaultImageUri) // 使用預設圖片



                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "保存使用者資料失敗", Toast.LENGTH_SHORT).show()
                }
        }





        return  view
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
                Toast.makeText(requireContext(), "圖片上傳失敗", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(requireContext(), "圖片 URL 已存儲到 Firestore", Toast.LENGTH_SHORT).show()
                startActivity(Intent(requireContext(), MainActivity::class.java))
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "圖片 URL 儲存失敗", Toast.LENGTH_SHORT).show()
            }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Setting_Fragment_Tab_Image.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Setting_Fragment_Tab_Image().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}