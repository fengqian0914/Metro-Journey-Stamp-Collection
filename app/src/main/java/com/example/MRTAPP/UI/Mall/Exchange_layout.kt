package com.example.MRTAPP.UI.Mall

import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.MRTAPP.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class exchange_layout : AppCompatActivity() {
    private var count = 1
    private val minCount = 1
    private lateinit var dialog: BottomSheetDialog
    lateinit var databaseReference: DatabaseReference
    private var Productquantity = 0
    private lateinit var storage: FirebaseStorage
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    var usercoin: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exchange_layout)

        supportActionBar?.hide()

        // 初始化 Firebase
        storage = FirebaseStorage.getInstance()
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference

        val ProductName = intent.getStringExtra("ProductName").toString()
        val ProductImage = intent.getStringExtra("ProductImage").toString()
        val Productprice = intent.getIntExtra("Productprice", 0)
        val ProductId = intent.getStringExtra("ProductId").toString()
        Productquantity = getProductQuantity(ProductId)

        Log.d("ProductList_2", "Productprice: $Productprice, Productquantity: $Productquantity")

        findViewById<TextView>(R.id.ExchangeProductName).text = ProductName
        val imageView = findViewById<ImageView>(R.id.ExchangeProductImageView)
        Glide.with(imageView)
            .load(ProductImage) // 這裡是 Firebase Storage 的圖片 URL
            .placeholder(R.drawable.placeholder) // 請替換為你的佔位符圖片
            .error(R.drawable.loading_error) // 請替換為你的錯誤圖片
            .into(imageView)

        findViewById<TextView>(R.id.ExchangeProductPrice).text = Productprice.toString()
        findViewById<TextView>(R.id.ExchangeProductquantity).text = Productquantity.toString()
        Log.d("ProductList_3", "Productquantity: ${Productquantity}")

        findViewById<TextView>(R.id.TotalPrice).text = (Productprice * count).toString()

        val btnMinus: Button = findViewById(R.id.btn_minus)
        val btnPlus: Button = findViewById(R.id.btn_plus)
        val exchange_final: Button = findViewById(R.id.exchange_final)
        val tvCount: TextView = findViewById(R.id.tv_count)

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val db = FirebaseFirestore.getInstance()

            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        usercoin = document.getLong("usercoin")!!.toInt()
                        findViewById<TextView>(R.id.Exchange_MyCoin).text = usercoin.toString()
                    } else {
                        // 用户信息未找到
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "獲取用戶資料失敗: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }

        btnMinus.setOnClickListener {
            if (count > minCount) {
                count--
                tvCount.text = count.toString()
                findViewById<TextView>(R.id.TotalPrice).text = (Productprice * count).toString()
            }
        }

        btnPlus.setOnClickListener {
            if (count < Productquantity) {
                count++
                tvCount.text = count.toString()
                findViewById<TextView>(R.id.TotalPrice).text = (Productprice * count).toString()
            }
        }

        exchange_final.setOnClickListener {
            addCoupon(usercoin, Productprice, count, ProductId, ProductName, ProductImage)
        }
    }

    private fun getProductQuantity(productId: String): Int {
        val db = FirebaseFirestore.getInstance()
        val productRef = db.collection("Data").document("Product").collection("Items").document(productId)
        Log.d("ProductList_id",productId.toString())

        productRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // 獲取最新數量
                    val productQuantity = document.getLong("quantity") ?: 0
                    // 更新 UI
                    findViewById<TextView>(R.id.ExchangeProductquantity).text = productQuantity.toString()
                    Productquantity = productQuantity.toString().toInt()
                    Log.d("ProductList_S","aaa"+productQuantity.toString())
                } else {
                    Log.e("Firestore", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error getting document", exception)
            }
        return Productquantity
    }



    private fun addCoupon(usercoin: Int, Productprice: Int, count: Int, ProductId: String, ProductName: String, ProductImage: String) {
        // 更新值（加 1）
        findViewById<TextView>(R.id.Exchange_MyCoin).text = usercoin.toString()
        val spend_coins = Productprice * count
        Log.d("StoreData", "${Productquantity}.....${count}")
        if (spend_coins > usercoin || Productquantity < count) {
            Toast.makeText(this, "所需硬幣不夠", Toast.LENGTH_LONG).show()
        } else {
            // 生成隨機字母和數字的兌換碼（16碼）
            val QRrandom: String = (1..16)
                .map { ('A'..'Z') + ('0'..'9') }
                .flatten()
                .shuffled()
                .take(16)
                .joinToString("")
            showBottomSheet(ProductId, ProductName, usercoin.toLong(), spend_coins, QRrandom, count.toString(), ProductImage)
        }
    }

    private fun showBottomSheet(ProductId: String, ProductName: String, original_coin: Long, spend_coins: Int, QRrandom: String, quantity: String, ProductImage: String) {
        // 載入佈局
        val dialogView = layoutInflater.inflate(R.layout.qrcode_product, null)

        // 找到佈局中的 TextView 並設置文字
        val productNameTextView = dialogView.findViewById<TextView>(R.id.QR_ProductName)
        productNameTextView.text = ProductName

        val Product_QRcode = dialogView.findViewById<ImageView>(R.id.Product_QRcode)
        try {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.encodeBitmap(QRrandom, BarcodeFormat.QR_CODE, 400, 400)
            Product_QRcode.setImageBitmap(bitmap)
            adjustImageViewSize(Product_QRcode, bitmap)

        } catch (e: WriterException) {
            e.printStackTrace()
        }

        couponExchange(ProductId, ProductName, quantity, original_coin, spend_coins, dialogView, ProductImage, QRrandom)
        // 建立 BottomSheetDialog
        dialog = BottomSheetDialog(this, R.style.BottomsheetDialogTheme)
        dialog.setContentView(dialogView)
        dialog.show()
    }

    private fun couponExchange(ProductId: String, ProductName: String, quantity: String, original_coin: Long, spend_coins: Int, dialogView: View, ProductImage: String, QRrandom: String) {
        val userId = auth.currentUser?.uid ?: return
        val couponId= (1..16)
            .map { ('A'..'Z') + ('0'..'9') }
            .flatten()
            .shuffled()
            .take(16)
            .joinToString("")
        val couponRef = db.collection("users").document(userId).collection("coupons").document(couponId) // 使用 couponId 作为文档 ID
        // 獲取當前日期和時間
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        // 計算過期時間，設置為 redeemTime 加 30 天
        val calendar = Calendar.getInstance()
        calendar.time = dateFormat.parse(currentDate) // 使用 redeemTime 作為基準時間
        calendar.add(Calendar.DAY_OF_YEAR, 30)
        val expiryTime: String = dateFormat.format(calendar.time)

        // 儲存兌換資訊
        val couponData = hashMapOf(
            "productId" to ProductId,
            "productName" to ProductName,
            "productImage" to ProductImage,
            "quantity" to quantity,
            "QRcode" to QRrandom,
            "timestamp" to currentDate,
            "expiryTime" to expiryTime,
            "status" to "未兌換",
            "CouponId" to couponId
        )

        couponRef.set(couponData)
            .addOnSuccessListener {
                val remaining_coins = original_coin-spend_coins

                db.collection("users").document(userId)
                    .update("usercoin", remaining_coins)
                    .addOnSuccessListener {
                        dialogView.findViewById<TextView>(R.id.original_coin).text=original_coin.toString()
                        findViewById<TextView>(R.id.Exchange_MyCoin).text = remaining_coins.toString()
                        dialogView.findViewById<TextView>(R.id.remaining_coins).text=remaining_coins.toString()
                        dialogView.findViewById<TextView>(R.id.original_coin).visibility=View.VISIBLE
                        dialogView.findViewById<TextView>(R.id.remaining_coins).visibility=View.VISIBLE
                        usercoin=remaining_coins.toInt()
                        StoreDateChange(ProductId,quantity.toInt())
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "更新硬幣數量失敗", Toast.LENGTH_SHORT).show()
                    }

            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "儲存優惠券失敗: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun StoreDateChange(ProductId: String, Purchase_quantity: Int) {


        val db = FirebaseFirestore.getInstance()
        val productRef = db.collection("Data").document("Product").collection("Items").document(ProductId)
        Log.d("ProductList_id",ProductId.toString())
        val userRef = db.collection("Data").document("Product").collection("Items").document(ProductId)

        productRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // 獲取最新數量
                    val productQuantity = document.getLong("quantity") ?: 0
                    Log.d("StoreData", "原先數量: $productQuantity")
                    Log.d("StoreData", "購買數量: $Purchase_quantity")

                    // 計算更新後的庫存數量
                    val newquantity = productQuantity - Purchase_quantity
                    val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
                    Log.d("newquantity",newquantity.toString())


                    productRef.update("quantity", newquantity)
                        .addOnSuccessListener {

                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "更新硬幣數量失敗", Toast.LENGTH_SHORT).show()
                        }



                    // 更新 UI
                    findViewById<TextView>(R.id.ExchangeProductquantity).text = newquantity.toString()
                    Productquantity = productQuantity.toString().toInt()
                    Log.d("ProductList_S","aaa"+productQuantity.toString())
                } else {
                    Log.e("Firestore", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error getting document", exception)
            }





    }

    private fun adjustImageViewSize(imageView: ImageView, bitmap: Bitmap) {
        // 在這裡獲取 ImageView 的寬度
        val targetWidth = imageView.width

        // 如果 ImageView 的寬度為 0 (尚未佈局完成)，則在佈局完成後再調整大小
        if (targetWidth == 0) {
            imageView.post {
                val finalWidth = imageView.width
                val scaledBitmap = scaleBitmapToFitWidth(bitmap, finalWidth)
                imageView.setImageBitmap(scaledBitmap)

            }
        } else {
            // 設置 ImageView 的大小並顯示位圖
            val scaledBitmap = scaleBitmapToFitWidth(bitmap, targetWidth)
            imageView.setImageBitmap(scaledBitmap)
        }
    }

    private fun scaleBitmapToFitWidth(bitmap: Bitmap, targetWidth: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val scale = targetWidth.toFloat() / width
        val matrix = Matrix().apply {
            postScale(scale, scale)
        }
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
    }
}
