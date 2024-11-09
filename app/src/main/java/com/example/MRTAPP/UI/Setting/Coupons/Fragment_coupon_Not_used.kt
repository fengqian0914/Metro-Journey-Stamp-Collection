package com.example.MRTAPP.UI.Setting.Coupons

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.MRTAPP.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONArray
import org.json.JSONObject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Fragment_coupon_Not_used : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var databaseReference: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var CouponRecyclerViewAdapter: Coupon_Not_used_RecyclerView_Adapter
    private val Coupon_list = mutableListOf<Coupon_list>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        // Initialize the database reference
        databaseReference = FirebaseDatabase.getInstance().reference
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_coupon__not_used, container, false)
        val sharedPreferences = requireContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val account = sharedPreferences.getString("account", "defaultaccount")
        if (account.isNullOrEmpty()) {
            Log.d("filedatas", "未找到帳戶信息")
            return view
        }
        val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val db = FirebaseFirestore.getInstance()
        // 定義 Firestore 路徑
        val FirestorePath = db.collection("users")
            .document(userId)
            .collection("coupons")
        FirestorePath.get()
            .addOnSuccessListener { querySnapshot ->
                try {
                    recyclerView = view.findViewById(R.id.coupon_notused_RecylerView)
                    val CouponListDataArray = JSONArray()
                    for (document in querySnapshot.documents) {
                        val data = document.data
                        if (data != null) {
                            val CouponListDataObject = JSONObject()
                            if (data["status"] == "未兌換") {
                                CouponListDataObject.put("redeemCode", data["QRcode"])
                                CouponListDataObject.put("expiryTime", data["expiryTime"])
                                CouponListDataObject.put("productId", data["productId"])
                                CouponListDataObject.put("productImage", data["productImage"])
                                CouponListDataObject.put("productName", data["productName"])
                                CouponListDataObject.put("quantity", data["quantity"])
                                CouponListDataObject.put("timestamp", data["timestamp"])
                                CouponListDataObject.put("status", data["status"])
                                CouponListDataObject.put("CouponId", data["CouponId"])
                            }
                            CouponListDataArray.put(CouponListDataObject)
                        } else {
                        }
                    }
                    val data_list = convertJsonArrayToList(CouponListDataArray)
                    CouponRecyclerViewAdapter = Coupon_Not_used_RecyclerView_Adapter(requireContext(), data_list)
                    recyclerView.layoutManager = LinearLayoutManager(requireContext())
                    recyclerView.adapter = CouponRecyclerViewAdapter


                    if (CouponListDataArray.length() == 0) {
                        view.findViewById<TextView>(R.id.EmptyData).visibility = View.VISIBLE

                    } else {
                        view.findViewById<TextView>(R.id.EmptyData).visibility = View.GONE
                    }
                    // 添加到列表
                    Coupon_list.clear()
                    Coupon_list.addAll(data_list)
                    CouponRecyclerViewAdapter.notifyDataSetChanged()

                    // 更新 TextView
                } catch (e: Exception) {
                    Log.d("filedatas", "錯誤: ${e.message}")
                }
            }
            .addOnFailureListener { exception ->
                println("取得文件時出錯: $exception")
            }

        return view
    }

    fun convertJsonArrayToList(jsonArray: JSONArray?): List<Coupon_list> {
        val list = mutableListOf<Coupon_list>()

        jsonArray?.let {
            for (i in 0 until it.length()) {
                val jsonObject = it.getJSONObject(i)
                try {

                    val CouponInfo = Coupon_list(
                        redeemCode = jsonObject.getString("redeemCode"),
                        expiryTime = jsonObject.getString("expiryTime"),  // 保持為 String 類型
                        productId=jsonObject.getString("productId"),
                        productImage=jsonObject.getString("productImage"),
                        productName = jsonObject.getString("productName"),
                        quantity = jsonObject.getInt("quantity"),
                        timestamp = jsonObject.getString("timestamp"),  // 保持為 String 類型
                        status = jsonObject.getString("status"),
                        CouponId=jsonObject.getString("CouponId")
                    )
                    list.add(CouponInfo)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        return list
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Fragment_coupon_Not_used().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
