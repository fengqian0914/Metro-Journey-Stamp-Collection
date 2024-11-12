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
        val tRouteLineName:TextView=itemView.findViewById(R.id.RouteLineName)
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
            "大坪林", "西門", "中山", "東門", "中正紀念堂","板橋",
            "古亭", "南港展覽館", "台北車站", "頭前庄", "民權西路", "忠孝新生"
        )


        transfetView(holder,position,dualStationNames)
        transfetView2(holder,position,dualStationNames)



        // 設置 ListView 的適配器來顯示 Transition_station 列表
        val innerAdapter = ArrayAdapter(
            context,
            android.R.layout.simple_list_item_1,
            currentItem.Transition_station.map { it.stationName }
        )
        holder.listView.adapter = innerAdapter

        // 動態設置 ListView 的高度

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
            holder.tTransfetView.visibility=View.GONE
            holder.tTransfetView2.visibility=View.GONE
            holder.tRouteLineName.visibility=View.GONE

        }
        if(holder.transferStationID_3.text=="BL11" && holder.transferStationID_1.text=="BL11"){
            holder.tTransfetView.visibility=View.GONE
            holder.tTransfetView2.visibility=View.GONE
            holder.tRouteLineName.visibility=View.GONE
        }

        RouteId(holder, position,currentItem.Transition_station.map { it.stationName }.size)
        determineRouteLine(holder,position)
    }

    fun determineRouteLine(holder: MyViewHolder,position: Int): String {
        val routeLineName: String = when {
            // 檢查新北投支線
            listOf(
                holder.transferStationID_1.text,
                holder.transferStationID_2.text,
                holder.transferStationID_3.text,
                holder.transferStationID_4.text
            ).count { it.contains("R22A") } >= 1 -> "R22A ${context.getString(R.string.route_R22A) }"
            // 檢查小碧潭支線
            listOf(
                holder.transferStationID_1.text,
                holder.transferStationID_2.text,
                holder.transferStationID_3.text,
                holder.transferStationID_4.text
            ).count { it.contains("G03A") } >= 1 -> "G03A ${context.getString(R.string.route_G03A) }"
            // 檢查板南線（至少两个 "BL"）
            listOf(
                holder.transferStationID_1.text,
                holder.transferStationID_2.text,
                holder.transferStationID_3.text,
                holder.transferStationID_4.text
            ).count { it.contains("BL") } >= 2 -> "BL ${context.getString(R.string.route_BL) }"
            // 檢查文湖線
            listOf(
                holder.transferStationID_1.text,
                holder.transferStationID_2.text,
                holder.transferStationID_3.text,
                holder.transferStationID_4.text
            ).count { it.contains("BR") } >= 2 -> "BR ${context.getString(R.string.route_BR) }"

            // 檢查淡水信義線
            listOf(
                holder.transferStationID_1.text,
                holder.transferStationID_2.text,
                holder.transferStationID_3.text,
                holder.transferStationID_4.text
            ).count { it.contains("R") } >= 2 -> "R ${context.getString(R.string.route_R) }"

            // 檢查新店線
            listOf(
                holder.transferStationID_1.text,
                holder.transferStationID_2.text,
                holder.transferStationID_3.text,
                holder.transferStationID_4.text
            ).count { it.contains("G") } >= 2 -> "G ${context.getString(R.string.route_G) }"

            // 檢查中和蘆洲線
            listOf(
                holder.transferStationID_1.text,
                holder.transferStationID_2.text,
                holder.transferStationID_3.text,
                holder.transferStationID_4.text
            ).count { it.contains("O") } >= 2 -> "O ${context.getString(R.string.route_O) }"

            // 檢查環狀線
            listOf(
                holder.transferStationID_1.text,
                holder.transferStationID_2.text,
                holder.transferStationID_3.text,
                holder.transferStationID_4.text
            ).count { it.contains("Y") } >= 2 ->"Y ${context.getString(R.string.route_Y) }"

            else -> "未知線路"
        }
        holder.tRouteLineName.text=routeLineName

        setStationBgcolor(holder.tRouteLineName,getColor(holder.tRouteLineName.text.toString()))


        return routeLineName
    }


    private fun transfetView(holder:MyViewHolder, position: Int, dualStationNames: Set<String>) {
        val currentItem = mlist[position]
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


        } else if (pathArray2!=null && pathArray2.size>1&& pathArray2[1] == "板橋"||
            pathArray2!=null && pathArray2.size>1&& pathArray2[0] == "板橋") {
            transfer2_Banqiao(holder, position)


        } else {
            val transferStationID = if (pathArray2 != null && pathArray2.size > 1) {
                findParentKeyByZhTwValue(context, pathArray2[1]) //
            } else {
                null
            }

            holder.transferStationID_3.text = transferStationID ?: ""

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

    private fun transfer2_Banqiao(holder: MyViewHolder, position: Int) {
        var colorStateList: ColorStateList? = null

        if(position - 1>=0) {
            val currentItem_Circular_Line = mlist[position - 1]
            val pathArray_Circular_Line =
                currentItem_Circular_Line.TransferStation.split("-").filter { it.isNotEmpty() }

            var BanqiaoID: String
            var temp = findParentKeyByZhTwValue(context, pathArray_Circular_Line[1]).toString().contains("BL")
            if (temp && Circular_Line_Banqiao == false) {
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
                setStationBgcolor(holder.transferStationID_3, getColor(holder.transferStationID_3.text.toString()))
            }
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
        // 讀取mrt_language.json
        val inputStream: InputStream = context.assets.open("mrt_language.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }

        val json = JSONObject(jsonString)

        // 找目標站
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
            "板橋" -> {
                stationIdArray.put("BL11")
                stationIdArray.put("Y16")
            }
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

            station.contains("BL")   -> R.color.mrt_route_BL
            station.contains("BR")   -> R.color.mrt_route_BR
            station.contains("R")    -> R.color.mrt_route_R
            station.contains("G")    -> R.color.mrt_route_G
            station.contains("O")    -> R.color.mrt_route_O
            station.contains("Y")    -> R.color.mrt_route_Y
            else -> R.color.mrt_route_BL
        }
    }



}
