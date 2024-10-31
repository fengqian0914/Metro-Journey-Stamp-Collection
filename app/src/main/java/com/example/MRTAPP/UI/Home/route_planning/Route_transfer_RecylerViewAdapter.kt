package com.example.MRTAPP.UI.Home.route_planning

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.MRTAPP.R
import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream

class Route_transfer_RecylerViewAdapter(
    private val context: Context,
    private val mlist: List<Route_transfer_list>
) : RecyclerView.Adapter<Route_transfer_RecylerViewAdapter.MyViewHolder>() {
    private var Circular_Line_Banqiao = false

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val listView: ListView = itemView.findViewById(R.id.stationListView)
        val tPath: TextView = itemView.findViewById(R.id.TransferPath)
        val tStationView: LinearLayout = itemView.findViewById(R.id.stationView)
        val tTransfetView: LinearLayout = itemView.findViewById(R.id.transfetView)
        val troute_arrow_down: ImageView = itemView.findViewById(R.id.route_arrow_down)
        val transferStationID_1: TextView = itemView.findViewById(R.id.transferStationID_1)
        val transferStationID_2: TextView = itemView.findViewById(R.id.transferStationID_2)

        val transferStationID_3: TextView = itemView.findViewById(R.id.transferStationID_3)
        val transferStationID_4: TextView = itemView.findViewById(R.id.transferStationID_4)
        val tPath2: TextView = itemView.findViewById(R.id.TransferPath2)
        val tTransfetView2: LinearLayout = itemView.findViewById(R.id.transfetView2)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_route_transfer_recyler_view_adapter, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = mlist.size

    private fun setStationBgcolor(Textview:TextView,Color:Int) {
        val colorStateList = ContextCompat.getColorStateList(context, Color)
        if (colorStateList != null) {
                ViewCompat.setBackgroundTintList(Textview, colorStateList)
        } else {
                Textview.setBackgroundColor(ContextCompat.getColor(context, Color))
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = mlist[position]
        val dualStationNames = setOf(
            "景安", "松江南京", "忠孝復興", "大安", "南京復興",
            "大坪林", "西門", "中山", "東門", "中正紀念堂",
            "古亭", "南港展覽館", "台北車站", "頭前庄", "民權西路", "忠孝新生"
        )



        transfetView2(holder,position,dualStationNames)
        val pathArray = currentItem.TransferStation.split("-").filter { it.isNotEmpty() }
        holder.tPath.text = pathArray[0]


        holder.transferStationID_2.visibility = View.GONE

        if (pathArray[1] in dualStationNames) {
            val stationIds = twostations(pathArray[1])
            holder.transferStationID_1.text = stationIds[0].toString()
            holder.transferStationID_2.text = stationIds[1].toString()
            holder.transferStationID_2.visibility = View.VISIBLE

            setStationBgcolor(holder.transferStationID_1,getColor(holder.transferStationID_1.text.toString()))
            setStationBgcolor(holder.transferStationID_2,getColor(holder.transferStationID_2.text.toString()))




        } else if (pathArray[1] == "板橋") {
            var colorStateList: ColorStateList? = null


            val currentItem_Circular_Line = mlist[position - 1]
            val pathArray_Circular_Line =
                currentItem_Circular_Line.TransferStation.split("-").filter { it.isNotEmpty() }

            Log.d(
                "currentItem_Circular_Line",
                "currentItem_Circular_Line:${pathArray_Circular_Line[1]}"
            )
            var BanqiaoID: String
            if (findParentKeyByZhTwValue(context, pathArray_Circular_Line[1]).toString()
                    .contains("BL") && Circular_Line_Banqiao == false
            ) {
                BanqiaoID = "BL07"
                colorStateList = ContextCompat.getColorStateList(context, R.color.mrt_route_BL)
                Circular_Line_Banqiao = true
            } else {
                BanqiaoID = "Y16"
                colorStateList = ContextCompat.getColorStateList(context, R.color.mrt_route_Y)
            }

            holder.transferStationID_1.text = BanqiaoID

            if (colorStateList != null) {
                ViewCompat.setBackgroundTintList(holder.transferStationID_1, colorStateList)
            } else {
                holder.transferStationID_1.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.mrt_route_BL
                    )
                )
            }

        } else {
            holder.transferStationID_1.text = findParentKeyByZhTwValue(context, pathArray[1])
        }


        val backgroundTint = getColor(holder.transferStationID_1.text.toString())

        // 設置背景顏色
        val colorStateList = ContextCompat.getColorStateList(context, backgroundTint)
        if (colorStateList != null) {
            ViewCompat.setBackgroundTintList(holder.transferStationID_1, colorStateList)
        } else {
            holder.transferStationID_1.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    backgroundTint
                )
            )
        }


        // 設置 ListView 的適配器來顯示 Transition_station 列表
        val innerAdapter = ArrayAdapter(
            context,
            android.R.layout.simple_list_item_1,
            currentItem.Transition_station.map { it.stationName }
        )
        holder.listView.adapter = innerAdapter

        // 動態設置 ListView 的高度
//        setListViewHeightBasedOnChildren(holder.listView)
        holder.tStationView.visibility = View.GONE
        holder.tTransfetView.setOnClickListener {
            if (holder.tStationView.visibility == View.GONE) {
                holder.tStationView.visibility = View.VISIBLE
            } else {
                holder.tStationView.visibility = View.GONE
            }
        }
        setListViewHeightBasedOnChildren(holder.listView)

        if (currentItem.Transition_station.map { it.stationName }.size == 0) {
            holder.troute_arrow_down.visibility = View.GONE

        } else {
            holder.troute_arrow_down.visibility = View.VISIBLE

        }

        if(position==mlist.size-1){
            holder.tTransfetView2.visibility=View.GONE
        }
        RouteId(holder, position,currentItem.Transition_station.map { it.stationName }.size)


    }

    private fun transfetView2(holder: MyViewHolder, position: Int,dualStationNames:Set<String>) {
        val currentItem2 = if (position + 1 < mlist.size) {
            mlist[position + 1]
        } else {
            null
        }

        val pathArray2 = currentItem2?.TransferStation?.split("-")?.filter { it.isNotEmpty() }
        holder.tPath2.text = pathArray2?.getOrNull(0) ?: ""
        holder.transferStationID_4.visibility = View.GONE


        if (pathArray2 != null && pathArray2.size > 1 && pathArray2[1] in dualStationNames) {
            val stationIds = twostations(pathArray2[1])
            holder.transferStationID_3.text = stationIds[0].toString()
            holder.transferStationID_4.text = stationIds[1].toString()
            holder.transferStationID_4.visibility = View.VISIBLE
            setStationBgcolor(holder.transferStationID_3,getColor(holder.transferStationID_3.text.toString()))
            setStationBgcolor(holder.transferStationID_4,getColor(holder.transferStationID_4.text.toString()))


        } else if (pathArray2!=null && pathArray2.size>1&& pathArray2[1] == "板橋") {
            var colorStateList: ColorStateList? = null


            val currentItem_Circular_Line = mlist[position - 1]
            val pathArray_Circular_Line =
                currentItem_Circular_Line.TransferStation.split("-").filter { it.isNotEmpty() }

            Log.d(
                "currentItem_Circular_Line",
                "currentItem_Circular_Line:${pathArray_Circular_Line[1]}"
            )
            var BanqiaoID: String
            if (findParentKeyByZhTwValue(context, pathArray_Circular_Line[1]).toString()
                    .contains("BL") && Circular_Line_Banqiao == false
            ) {
                BanqiaoID = "BL07"
                colorStateList = ContextCompat.getColorStateList(context, R.color.mrt_route_BL)
                Circular_Line_Banqiao = true
            } else {
                BanqiaoID = "Y16"
                colorStateList = ContextCompat.getColorStateList(context, R.color.mrt_route_Y)
            }

            holder.transferStationID_3.text = BanqiaoID

            if (colorStateList != null) {
                ViewCompat.setBackgroundTintList(holder.transferStationID_3, colorStateList)
            } else {

                setStationBgcolor(holder.transferStationID_3,getColor(holder.transferStationID_3.text.toString()))

            }

        } else {
            val transferStationID = if (pathArray2 != null && pathArray2.size > 1) {
                findParentKeyByZhTwValue(context, pathArray2[1]) // 安全访问 pathArray2[1]
            } else {
                null // 如果条件不满足，返回 null 或默认值
            }

            holder.transferStationID_3.text = transferStationID ?: "" // 设置默认值为空字符串

        }


        val backgroundTint = getColor(holder.transferStationID_3.text.toString())

        // 設置背景顏色
        val colorStateList = ContextCompat.getColorStateList(context, backgroundTint)
        if (colorStateList != null) {
            ViewCompat.setBackgroundTintList(holder.transferStationID_3, colorStateList)
        } else {
            holder.transferStationID_3.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    backgroundTint
                )
            )
        }

    }

    fun RouteId(holder: MyViewHolder, position: Int,itemsize:Int) {
        try {
            val currentItem_Circular_Line = mlist[position ]
            val pathArray_Circular_Line =
                currentItem_Circular_Line.TransferStation.split("-").filter { it.isNotEmpty() }
            val routeId = if(itemsize>0) {
                findParentKeyByZhTwValue(
                    context,
                    currentItem_Circular_Line.Transition_station[0].stationName
                ).toString()
            }else{
                findParentKeyByZhTwValue(context, pathArray_Circular_Line[1]).toString()
            }






        }
        catch (e:Exception){
            Log.d("RouteId", "e${e}")

        }
    }

    private fun setListViewHeightBasedOnChildren(listView: ListView) {
        val listAdapter = listView.adapter ?: return
        var totalHeight = 0
        for (i in 0 until listAdapter.count) {
            val listItem = listAdapter.getView(i, null, listView)
            listItem.measure(0, 0)
            totalHeight += listItem.measuredHeight
        }

        val params = listView.layoutParams
        params.height = totalHeight + (listView.dividerHeight * (listAdapter.count - 1))
        listView.layoutParams = params
        listView.requestLayout()
    }
    fun findParentKeyByZhTwValue(context: Context, targetZhTw: String): String? {
        // 从 assets 文件夹中读取 mrt_language.json 文件
        val inputStream: InputStream = context.assets.open("mrt_language.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }

        // 将字符串内容转换为 JSONObject
        val json = JSONObject(jsonString)

        // 遍历 JSON 对象查找目标站名
        for (lineKey in json.keys()) {
            val line = json.getJSONObject(lineKey)
            for (stationKey in line.keys()) {
                val station = line.getJSONObject(stationKey)
                if (station.has("Zh_tw") && station.getString("Zh_tw") == targetZhTw) {
                    return stationKey
                }
            }
        }
        return null
    }

    fun twostations(station: String): JSONArray {
        val stationIdArray = JSONArray() // 初始化 JSONArray
        when (station) {
            "南港展覽館" -> {
                stationIdArray.put("BL23")
                stationIdArray.put("BR24")
            }
            "景安" -> {
                stationIdArray.put("O02")
                stationIdArray.put("Y11")
            }
            "松江南京" -> {
                stationIdArray.put("G15")
                stationIdArray.put("O08")
            }
            "忠孝復興" -> {
                stationIdArray.put("BR10")
                stationIdArray.put("BL15")
            }
            "大安" -> {
                stationIdArray.put("BR09")
                stationIdArray.put("R05")
            }
            "南京復興" -> {
                stationIdArray.put("BR11")
                stationIdArray.put("G16")
            }
            "大坪林" -> {
                stationIdArray.put("G04")
                stationIdArray.put("Y07")
            }
            "西門" -> {
                stationIdArray.put("BL11")
                stationIdArray.put("G12")
            }
            "中山" -> {
                stationIdArray.put("G14")
                stationIdArray.put("R11")
            }
            "東門" -> {
                stationIdArray.put("R07")
                stationIdArray.put("O06")
            }
            "中正紀念堂" -> {
                stationIdArray.put("G10")
                stationIdArray.put("R08")
            }
            "古亭" -> {
                stationIdArray.put("G09")
                stationIdArray.put("O05")
            }
            "台北車站" -> {
                stationIdArray.put("BL12")
                stationIdArray.put("R10")
            }
            "頭前庄" -> {
                stationIdArray.put("O17")
                stationIdArray.put("Y18")
            }
            "民權西路" -> {
                stationIdArray.put("R13")
                stationIdArray.put("O11")
            }
            "忠孝新生" -> {
                stationIdArray.put("BL14")
                stationIdArray.put("O07")
            }
        }
        return stationIdArray
    }
    fun getColor(station: String):Int{
        return when {
            station.contains("R22A") -> R.color.mrt_route_R22A
            station.contains("G03A") -> R.color.mrt_route_G03A

            station.contains("BL") -> R.color.mrt_route_BL
            station.contains("BR") -> R.color.mrt_route_BR
            station.contains("R") -> R.color.mrt_route_R
            station.contains("G") -> R.color.mrt_route_G
            station.contains("O") -> R.color.mrt_route_O
            station.contains("Y") -> R.color.mrt_route_Y
            else -> R.color.mrt_route_BL
        }
    }



}
