package com.example.MRTAPP.Data

data class User(
    var id: String? = null,
    var account: String? = null,
    var password: String? = null,
    var name: String? = null,
    var photoId: String? = null,
    var coin: Int? = null,
    var station: Map<String, Map<String, Boolean>>? = null,
    var stationValue: Map<String, Int>? = null,
    val coupons: Map<String, Any>? = null // 使用 Any 來兼容 JSON 格式
)

