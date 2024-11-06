package com.example.MRTAPP.Other.Widget

import org.json.JSONObject

data class Widget_Trainlist(
    val TrainNumber: String,
    val StationName: String,
    val DestinationName: String,
    val countDown: String,
    val NowDateTime: String
) {
    fun toJsonString(): String {
        return JSONObject().apply {
            put("TrainNumber", TrainNumber)
            put("StationName", StationName)
            put("DestinationName", DestinationName)
            put("countDown", countDown)
            put("NowDateTime", NowDateTime)
        }.toString()
    }
}