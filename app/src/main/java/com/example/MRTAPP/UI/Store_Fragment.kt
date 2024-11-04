package com.example.MRTAPP.UI

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.MRTAPP.UI.Mall.ProductList
import com.example.MRTAPP.UI.Mall.Product_RecyclerViewAdapter
import com.example.MRTAPP.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private var recyclerView:RecyclerView?=null
private var recyclerViewAdapter: Product_RecyclerViewAdapter?=null
private var productList = mutableListOf<ProductList>()
private lateinit var databaseReference: DatabaseReference

/**
 * A simple [Fragment] subclass.
 * Use the [Store_Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Store_Fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: Store_Fragment
    private lateinit var database: DatabaseReference
    private lateinit var dialog:BottomSheetDialog
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var rootView: View // 保存視圖引用

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }
    var btn: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_store_, container, false)

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            getUserData(rootView,userId)
        }

        return rootView

    }

    private fun getUserData(view: View, userId: String) {
        val db = FirebaseFirestore.getInstance()


        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val userCoin = document.getLong("usercoin")?.toInt()


                    view.findViewById<TextView>(R.id.mycoin).text=userCoin.toString()
                    productList=ArrayList()
                    recyclerView=view.findViewById<RecyclerView>(R.id.StoreRecyclerView)

                    recyclerViewAdapter = Product_RecyclerViewAdapter(userCoin!!.toInt(),this@Store_Fragment, productList)
                    val layoutManager:RecyclerView.LayoutManager=GridLayoutManager(context,2)
                    recyclerView!!.layoutManager=layoutManager
                    recyclerView!!.adapter= recyclerViewAdapter

                    prepareProductListData()


                    // 將資料傳回
                } else {
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "獲取用戶資料失敗: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun prepareProductListData() {
        // 獲取 Firestore 的實例
        val db = FirebaseFirestore.getInstance()

        // 獲取對應的集合
        val collectionRef = db.collection("Data").document("Product").collection("Items")

        // 讀取數據
        collectionRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("Store_Fragment", "Listen failed.", e)
                return@addSnapshotListener
            }

            productList.clear() // 清空列表，防重複

            if (snapshot != null) {
                // 建立暫時的列表，將每個 product 放入以便排序
                val tempProductList = mutableListOf<ProductList>()

                for (productSnapshot in snapshot.documents) {
                    val productId = productSnapshot.id
                    val productName = productSnapshot.getString("ProductName")
                    val productPrice = productSnapshot.getLong("Price")
                    val productQuantity = productSnapshot.getLong("quantity")
                    val productImageUrl = productSnapshot.getString("ImageUrl").toString()

                    Log.d("title", "Name: $productName")
                    Log.d("title", "Price: $productPrice")

                    if (productId.isNotEmpty() && productName != null && productPrice != null && productQuantity != null) {
                        val product = ProductList(
                            productId,
                            productName,
                            productImageUrl,
                            productPrice.toInt(),
                            productQuantity.toLong()
                        )
                        tempProductList.add(product)
                        Log.e("Store_Fragment", " data--> imageResource:${productImageUrl}: ID: $productId, Name: $productName, Price: $productPrice, Quantity: $productQuantity")
                    } else {
                        Log.e("Store_Fragment", "Invalid product data: ID: $productId, Name: $productName, Price: $productPrice, Quantity: $productQuantity")
                    }
                }

                // 根據 ProductId 中的數字進行排序
                tempProductList.sortBy { product ->
                    product.Id.replace("Product", "").toIntOrNull() ?: Int.MAX_VALUE
                }

                // 將排序好的產品加入到 productList
                productList.addAll(tempProductList)
                recyclerViewAdapter?.notifyDataSetChanged()
            } else {
                Log.d("Store_Fragment", "Current data: null")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // 當返回該頁面時重新獲取用戶硬幣數量
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            getUserData(rootView, userId) // 使用已經存在的視圖來更新數據
        }
    }



    private fun getImageResourceForProduct(productId: String?): Int {
        if (productId.isNullOrEmpty()) {
            return 0
        }
        val resourceName = "product" + productId?.replace("Product", "")
        return context?.let { ctx ->
            ctx.resources.getIdentifier(resourceName, "drawable", ctx.packageName)
        } ?: 0
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Store_Fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Store_Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        fun showBottomSheet() {
            showBottomSheet()
        }
    }
}