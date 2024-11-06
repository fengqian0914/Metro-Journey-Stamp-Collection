package com.example.MRTAPP.UI.Star

import org.json.JSONObject

data class Achievement_List(
    val id:String,
    val Name:String,
    val station:JSONObject,
    val level:Map<String,Any>,
    val Image:JSONObject,
    val existsquantity:Int)
