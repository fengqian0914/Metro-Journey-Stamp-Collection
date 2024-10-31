package com.example.MRTAPP.UI.Setting.Coupons

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.MRTAPP.R
import com.example.MRTAPP.databinding.ActivityQrcodeCouponRecyclerviewBinding


class Coupon_Exchange_final: AppCompatActivity()  {
    private lateinit var binding:ActivityQrcodeCouponRecyclerviewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrcodeCouponRecyclerviewBinding.inflate(layoutInflater)
        setContentView(binding.root)





    }


}