package com.example.MRTAPP.UI.Mall

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.MRTAPP.R
import com.example.MRTAPP.UI.Store_Fragment
import java.text.NumberFormat

class Product_RecyclerViewAdapter constructor(

    private  val getActivity:Store_Fragment,
    private val ProductList:List<ProductList>, private  val coin:Int?=0):
    RecyclerView.Adapter<Product_RecyclerViewAdapter.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_storeitem,parent,false)
        return  MyViewHolder(view)
    }



    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val sharedPreferences = holder.itemView.context.getSharedPreferences("Login", Context.MODE_PRIVATE)
        val Guest = sharedPreferences.getBoolean("Guest",false)
        if(Guest==true){
            holder.tProducteExchange.setOnClickListener {
                val viewContext=holder.itemView.context
                Toast.makeText(viewContext,viewContext.getString(R.string.Guest_toast_msg),Toast.LENGTH_LONG).show()
            }
        }else{
            holder.tProducteExchange.setOnClickListener {
                val intent = Intent(holder.itemView.context, exchange_layout::class.java)
                intent.putExtra("ProductName", ProductList[position].title)
                intent.putExtra("ProductImage", ProductList[position].Image)
                intent.putExtra("Productprice", ProductList[position].price)
                intent.putExtra("Productquantity", ProductList[position].quantity)
                intent.putExtra("ProductId", ProductList[position].Id)

                Log.d("ProductList", "ProductList${ProductList[position]}")
                Log.d("logs", "id${ProductList[position].Id}")

                intent.putExtra("Mycoin", coin)

                holder.itemView.context.startActivity(intent)
            }
        }
        holder.tProductTitle.text = ProductList[position].title

        Log.d("ProductImage",ProductList[position].Image)
        // 使用 Glide 加載圖片
        Glide.with(holder.itemView.context)
            .load(ProductList[position].Image) // 這裡是 Firebase Storage 的圖片 URL
            .placeholder(R.drawable.placeholder) // 請替換為你的佔位符圖片
            .error(R.drawable.loading_error) // 請替換為你的錯誤圖片
            .into(holder.tProductImage)

        // 格式化價格和數量
        val priceFormatted = NumberFormat.getInstance().format(ProductList[position].price)
        holder.tProductPrice.text = priceFormatted
        val quantityFormatted = NumberFormat.getInstance().format(ProductList[position].quantity)
        holder.tProductQuantity.text = quantityFormatted

        // 打印日誌以便調試
        Log.d("logs", ProductList[position].Image.toString())


    }

    override fun getItemCount(): Int {
        return ProductList.size
    }
    class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val tProductTitle:TextView=itemView.findViewById(R.id.ProductName)
        val tProductImage:ImageView=itemView.findViewById(R.id.ProductImage)
        val tProductPrice:TextView=itemView.findViewById(R.id.ProductPrice)
        val tProductQuantity:TextView=itemView.findViewById(R.id.Productquantity)
        val tProducteExchange: Button =itemView.findViewById(R.id.ProductExchange)
        val cardview:CardView=itemView.findViewById(R.id.ProductCardView)

    }

}