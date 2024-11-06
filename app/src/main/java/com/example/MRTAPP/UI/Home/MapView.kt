package com.example.MRTAPP.UI.Home
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.MRTAPP.Other.GetStationNameLanguage
import com.example.MRTAPP.R
import kotlin.math.sqrt


class MapView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    val start_station=findViewById<TextView>(R.id.start_station)
    val end_station=findViewById<TextView>(R.id.end_station)


    private val displayMetrics = resources.displayMetrics
    private val screenHeight = displayMetrics.heightPixels - 500

    private val blueLineStations = mutableListOf<Station>()
    private val redLineStations = mutableListOf<Station>()
    private val red_2_LineStations = mutableListOf<Station>()

    private val greenLineStations = mutableListOf<Station>()
    private val green_2_LineStations = mutableListOf<Station>()

    private val brownLineStations = mutableListOf<Station>()
    private val orangeLineStations = mutableListOf<Station>()
    private val orange_2_LineStations = mutableListOf<Station>()

    private val yellowLineStations = mutableListOf<Station>()

    private var startStationText: String? = null
    private var endStationText: String? = null
    private var listener: StationTextListener? = null
    private val Alltextsize=30f
    private val enlarge_size=1.2f
    private  var startStatiodId=""
    private  var EndStatiodId=""

    var click_count=0;
    private val linePaints: Map<String, Paint> = mapOf(
        "R22A" to Paint().apply { color = ContextCompat.getColor(context,R.color.mrt_route_R22A) },
        "G03A" to Paint().apply { color = ContextCompat.getColor(context,R.color.mrt_route_G03A)},
        "BL" to Paint().apply { color = ContextCompat.getColor(context,R.color.mrt_route_BL) },
        "R" to Paint().apply { color = ContextCompat.getColor(context,R.color.mrt_route_R)},
        "G" to Paint().apply { color = ContextCompat.getColor(context,R.color.mrt_route_G)},
        "BR" to Paint().apply { color =ContextCompat.getColor(context,R.color.mrt_route_BR) },
        "O" to Paint().apply { color = ContextCompat.getColor(context,R.color.mrt_route_O)},
        "Y" to Paint().apply { color = ContextCompat.getColor(context,R.color.mrt_route_Y)}
    )

    private val stationCodePaints: Map<String, Paint> = mapOf(
        "R22A" to Paint().apply { color = ContextCompat.getColor(context,R.color.mrt_route_R22A) },
        "G03A" to Paint().apply { color = ContextCompat.getColor(context,R.color.mrt_route_G03A)},
        "BL" to Paint().apply { color = ContextCompat.getColor(context,R.color.mrt_route_BL) },
        "R" to Paint().apply { color = ContextCompat.getColor(context,R.color.mrt_route_R)},
        "G" to Paint().apply { color = ContextCompat.getColor(context,R.color.mrt_route_G)},
        "BR" to Paint().apply { color =ContextCompat.getColor(context,R.color.mrt_route_BR) },
        "O" to Paint().apply { color = ContextCompat.getColor(context,R.color.mrt_route_O)},
        "Y" to Paint().apply { color = ContextCompat.getColor(context,R.color.mrt_route_Y)}
    )

    private var offsetX = 0.0f
    private var offsetY = 0.0f
    private var lastTouchX = 0f
    private var lastTouchY = 0f

    private val MIN_SCALE = 0.2f
    private val MAX_SCALE = 3.0f
    private var currentScale = 1f
    private var previousDistance = 1f
     var savelanauge:String?=null
    private var selectedStation: Station? = null


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.save()
//        canvas.scale(currentScale, currentScale)
        //中心點
        val centerX = (width / 2).toFloat()
        val centerY = (height / 2).toFloat()
        canvas.scale(currentScale, currentScale, centerX, centerY)
        canvas.scale(currentScale, currentScale)

        canvas.translate(offsetX / currentScale, offsetY / currentScale)

        // Draw blue line
        drawLine(canvas, blueLineStations, "BL")

        // Draw red line
        drawLine(canvas, redLineStations, "R")
        drawLine(canvas, red_2_LineStations, "R22A")

        // Draw green line
        drawLine(canvas, greenLineStations, "G")
        drawLine(canvas, green_2_LineStations, "G03A")

        // Draw brown line
        drawLine(canvas, brownLineStations, "BR")

        // Draw orange line
        drawLine(canvas, orangeLineStations, "O")
        drawLine(canvas, orange_2_LineStations, "O")

        // Draw yellow line
        drawLine(canvas, yellowLineStations, "Y")

        canvas.restore()

        selectedStation?.let {
            // Draw selected station name
            val x = it.position.x / currentScale + offsetX / currentScale
            val y = it.position.y / currentScale + offsetY / currentScale




            stationCodePaints[it.code]?.let { paint ->
                canvas.drawText(it.code, x, y + Alltextsize, paint)
            }
        }
    }

    private fun drawLine(canvas: Canvas, lineStations: List<Station>, lineCode: String) {
        val paint = linePaints[lineCode] ?: return
        val codePaint = stationCodePaints[lineCode] ?: return
        val path = Path()

        for ((index, station) in lineStations.withIndex()) {
            val x = station.position.x
            val y = station.position.y

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }

//            canvas.drawCircle(x, y, 1f, stationPaint)
            val GetLanguage = GetStationNameLanguage(context)
//            GetLanguage.getStationName(this,station)

//            val stationName_TW = "${GetLanguage.getStationName(context,station.chineseName)}"
//            val stationName_EN = "${station.englishName}"
            savelanauge=GetLanguage.getsaveLanguage(context)

            val LanguageText=when(savelanauge){
                "Zh_tw"->station.chineseName
                "En" -> station.englishName
                "Ja" -> station.JpName
                "Zh-Hans" -> station.Zh_HansName
                "Ko" -> station.KoName
                else->station.englishName
            }
            val stationName_TW = "${LanguageText}"
            val stationName_EN= "${LanguageText}"

            val stationCode = station.code
            updateTextSizeBasedOnLanguage(station.code)

            setLinePaint(lineCode)
            setStationPaint(lineCode)

            canvas.drawCircle(station.position.x, station.position.y, 15f, stationPaint)
            canvas.drawCircle(station.position.x, station.position.y, 15f, linePaint)
            when (stationCode) {

                in "R07"->{
                    chineseTextPaint.textAlign = Paint.Align.LEFT
                    codePaint.textAlign = Paint.Align.LEFT

                    canvas.drawText(stationName_TW, x , y+60f, chineseTextPaint)
                    canvas.drawText(stationCode, x , y + 100f, createCodeTextPaint(lineCode))
                }
                in "G08"->{
                    chineseTextPaint.textAlign = Paint.Align.LEFT
                    codePaint.textAlign=Paint.Align.LEFT

                    canvas.drawText(stationName_TW, x-80f , y+105f, chineseTextPaint)
                    canvas.drawText(stationCode, x-80f , y+145f, createCodeTextPaint(lineCode))
                }
                in "G09","G03A"->{
                    chineseTextPaint.textAlign = Paint.Align.RIGHT
                    codePaint.textAlign=Paint.Align.RIGHT

                    canvas.drawText(stationName_TW, x -70f, y, chineseTextPaint)
                    canvas.drawText(stationCode, x - 120f, y+35f, createCodeTextPaint(lineCode))
                }
                in "O12"->{
                    chineseTextPaint.textAlign = Paint.Align.RIGHT
                    codePaint.textAlign=Paint.Align.RIGHT

                    canvas.drawText(stationName_TW, x -120f, y-10f, chineseTextPaint)
                    canvas.drawText(stationCode, x - 150f, y+20f, createCodeTextPaint(lineCode))
                }
                in "O50".."O54"->{
                    chineseTextPaint.textAlign = Paint.Align.RIGHT
                    codePaint.textAlign=Paint.Align.RIGHT

                    canvas.drawText(stationName_TW, x-80f , y+60f, chineseTextPaint)
                    canvas.drawText(stationCode, x-120f , y+90f, createCodeTextPaint(lineCode))
                }
                in "BL09"->{
                    chineseTextPaint.textAlign = Paint.Align.LEFT
                    codePaint.textAlign=Paint.Align.LEFT

                    canvas.drawText(stationName_TW, x + 30f, y+20f, chineseTextPaint)
                    canvas.drawText(stationCode, x + 30f, y + 55f, createCodeTextPaint(lineCode))
                }
                in "R09"->{
                    chineseTextPaint.textAlign = Paint.Align.LEFT
                    codePaint.textAlign=Paint.Align.LEFT

                    canvas.drawText(stationName_TW, x + 30f, y-10f, chineseTextPaint)
                    canvas.drawText(stationCode, x + 30f, y +20f, createCodeTextPaint(lineCode))
                }
                in "R11","R13"->{ //交叉站 右下
                    chineseTextPaint.textAlign = Paint.Align.LEFT
                    codePaint.textAlign=Paint.Align.LEFT

                    canvas.drawText(stationName_TW, x +40f, y+60f, chineseTextPaint)

                    canvas.drawText(stationCode, x +40f, y+90f, createCodeTextPaint(lineCode))
                }
                in "O05","O06","Y07","O12_1","R22_1","G03_1","Y11_1","Y18_1"->{ //不顯示

                }in "O17"->{
                chineseTextPaint.textAlign = Paint.Align.CENTER
                codePaint.textAlign=Paint.Align.CENTER

                canvas.drawText(stationName_TW, x -80f, y-70f, chineseTextPaint)
                canvas.drawText(stationCode, x - 80f, y-30f, createCodeTextPaint(lineCode))
            }
                in "Y17","Y09"->{ //左下
                    chineseTextPaint.textAlign = Paint.Align.LEFT
                    codePaint.textAlign=Paint.Align.LEFT

                    canvas.drawText(stationName_TW, x-80f , y+100f, chineseTextPaint)
                    canvas.drawText(stationCode, x-80f , y+140f, createCodeTextPaint(lineCode))
                }
                in "R06",in "BR15".."BR20","BL17","G17"->{ //左上
                    chineseTextPaint.textAlign = Paint.Align.LEFT
                    codePaint.textAlign=Paint.Align.LEFT

                    canvas.drawText(stationName_TW, x-70f , y-80f, chineseTextPaint)
                    canvas.drawText(stationCode, x-70f , y-40f, createCodeTextPaint(lineCode))
                }
                in "BL12",in "G04".."G07" ,in "BR03","BR06","G13",in "Y13".."Y16","R08",in "BR08","R05","O10"-> { //右上
                    chineseTextPaint.textAlign = Paint.Align.LEFT
                    codePaint.textAlign=Paint.Align.LEFT
                    canvas.drawText(stationName_TW, x + 30f, y-60f, chineseTextPaint)
                    canvas.drawText(stationCode, x + 30f, y-20f, createCodeTextPaint(lineCode))
                }
                in "G12".."G13"->{ //左方
                    chineseTextPaint.textAlign = Paint.Align.RIGHT
                    codePaint.textAlign=Paint.Align.RIGHT

                    canvas.drawText(stationName_TW, x -40f, y+40f, chineseTextPaint)
                    canvas.drawText(stationCode, x - 90f, y+80f, createCodeTextPaint(lineCode))
                }
                in "G11"->{
                    chineseTextPaint.textAlign = Paint.Align.RIGHT
                    codePaint.textAlign=Paint.Align.RIGHT

                    canvas.drawText(stationName_TW, x -40f, y-20f, chineseTextPaint)
                    canvas.drawText(stationCode, x - 90f, y+20f, createCodeTextPaint(lineCode))
                }
                in "BL10".."BL16",in "BL18".."BL20" ,in "R02".."R04",in "R08" ,in "R20".."R24",in "G15","G16","G18","G19","BR02",in "Y10".."Y12","Y08",in "BR07","O02"-> { // 下方
                    chineseTextPaint.textAlign = Paint.Align.LEFT
                    codePaint.textAlign = Paint.Align.LEFT

                    canvas.drawText(stationName_TW, x+20f , y+60f, chineseTextPaint)
                    canvas.drawText(stationCode, x+20f , y + 100f, createCodeTextPaint(lineCode))

                }

                else->{ //右方
                    chineseTextPaint.textAlign = Paint.Align.LEFT
                    codePaint.textAlign=Paint.Align.LEFT

                    canvas.drawText(stationName_TW, x + 30f, y+35f, chineseTextPaint)
                    canvas.drawText(stationCode, x + 30f, y + 70f, createCodeTextPaint(lineCode))
                }

            }

        }
        for (i in 0 until lineStations.size - 1) {
            val start = lineStations[i].position
            val end = lineStations[i + 1].position
            canvas.drawLine(start.x, start.y, end.x, end.y, linePaint(lineCode)?:return)
        }

    }

    private fun linePaint(lineCode: String): Paint=Paint().apply {


        when(lineCode) {

            "R22A" -> color = ContextCompat.getColor(context,R.color.mrt_route_R22A)
            "G03A"-> color = ContextCompat.getColor(context,R.color.mrt_route_G03A)
            "BL"->  color = ContextCompat.getColor(context,R.color.mrt_route_BL)
            "R" ->color = ContextCompat.getColor(context,R.color.mrt_route_R)
            "G"-> color = ContextCompat.getColor(context,R.color.mrt_route_G)
            "BR"-> color =ContextCompat.getColor(context,R.color.mrt_route_BR)
            "O"-> color = ContextCompat.getColor(context,R.color.mrt_route_O)
            "Y" ->color = ContextCompat.getColor(context,R.color.mrt_route_Y)

        }
        strokeWidth = 15f
        style = Paint.Style.STROKE
    }



    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = event.x
                lastTouchY = event.y
                selectedStation = findStation(event.x, event.y)



            }
            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount == 1) {
                    val dx = event.x - lastTouchX
                    val dy = event.y - lastTouchY
                    offsetX += dx
                    offsetY += dy
                    lastTouchX = event.x
                    lastTouchY = event.y
                    invalidate()
                } else if (event.pointerCount == 2) {
                    val currentDistance = getDistance(event)
                    if (previousDistance != 0f) {
                        val scale = currentDistance / previousDistance
                        currentScale = Math.max(MIN_SCALE, Math.min(currentScale * scale, MAX_SCALE))
                        invalidate()
                    }
                    previousDistance = currentDistance
                }
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                previousDistance = getDistance(event)
            }
            MotionEvent.ACTION_POINTER_UP -> {
                previousDistance = 0f
            }
        }
        return true
    }

    private fun getDistance(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt(x * x + y * y).toFloat()
    }

    private fun findStation(x: Float, y: Float): Station? {
        val adjustedX = (x - offsetX) / currentScale
        val adjustedY = (y - offsetY) / currentScale
        val touchRadius = 50f / currentScale // 設置觸摸半徑，根據縮放比例調整

        // 遍歷所有站點，查找是否有站點被觸摸到
        for (lineStations in listOf(blueLineStations, redLineStations,red_2_LineStations, greenLineStations, brownLineStations, orangeLineStations, yellowLineStations,green_2_LineStations,orange_2_LineStations)) {
            for (station in lineStations) {
                if (adjustedX >= station.position.x - touchRadius && adjustedX <= station.position.x + touchRadius &&
                    adjustedY >= station.position.y - touchRadius && adjustedY <= station.position.y + touchRadius
                ) {
                    try{
                        Log.d("asd","click_count${click_count}")
                        val GetLanguage = GetStationNameLanguage(context)
                        if(click_count==0){
                            startStatiodId=station.code
                            listener?.onStationTextChanged(GetLanguage.getStationName(context,station.chineseName),"start",station.code)
                            Log.d("title","0$station.chineseName")
                            click_count++

                        }else if(click_count==1){
                            EndStatiodId=station.code

                            if(startStatiodId==EndStatiodId){
                                Log.d("title","重複.chineseName")
                            }else {

                                listener?.onStationTextChanged(GetLanguage.getStationName(context,station.chineseName), "end", station.code)
                                click_count++
                                Log.d("title","1$station.chineseName")
                                Log.d("title","1${station.code}.chineseName")
                            }


                        }else{
                            Toast.makeText(context,"請清除後再使用",Toast.LENGTH_LONG).show()
                        }



                    }catch (e:Exception){
                        Log.d("title",e.message.toString())
                    }

                    return station
                }
            }
        }
        return null
    }



    data class Station(
        val chineseName: String,
        val englishName: String,
        val JpName: String,
        val KoName: String,
        val Zh_HansName: String,
        val code: String,
        val position: PointF
    )

    private val stationPaint: Paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL
    }
    private fun setStationPaint(lineCode: String) {
        when (lineCode) {
            "R22A" -> stationPaint.color = ContextCompat.getColor(context,R.color.mrt_route_R22A)
            "G03A"-> stationPaint.color = ContextCompat.getColor(context,R.color.mrt_route_G03A)
            "BL"->  stationPaint.color = ContextCompat.getColor(context,R.color.mrt_route_BL)
            "R" ->stationPaint.color = ContextCompat.getColor(context,R.color.mrt_route_R)
            "G"-> stationPaint.color = ContextCompat.getColor(context,R.color.mrt_route_G)
            "BR"-> stationPaint.color =ContextCompat.getColor(context,R.color.mrt_route_BR)
            "O"-> stationPaint.color = ContextCompat.getColor(context,R.color.mrt_route_O)
            "Y" ->stationPaint.color = ContextCompat.getColor(context,R.color.mrt_route_Y)
            else -> stationPaint.color = Color.BLACK
        }
    }
    private val chineseTextPaint: Paint = Paint().apply {
        color = Color.WHITE
        isFakeBoldText = true
        isAntiAlias = true
    }


    private fun updateTextSizeBasedOnLanguage(code: String) {
        chineseTextPaint.textSize = if (
            savelanauge == "En" ||
            ((savelanauge == "Zh_tw" || savelanauge == "Zh-Hans" || savelanauge == "Ko") && code == "R06")
        ) {
            25f
        } else {
            35f
        }

    }
    private fun createCodeTextPaint(lineCode: String): Paint {
        return Paint().apply {
            textSize = Alltextsize
            isAntiAlias = true
            color = Color.WHITE
//            when (lineCode) {
//                "BL" ->color = Color.parseColor("#0070bd")
//                "R" -> color = Color.parseColor("#e3002c")
//                "G" -> color = Color.parseColor("#008659")
//                "BR" -> color = Color.parseColor("#c48c31")
//                "O" -> color = Color.parseColor("#f8b61c")
//                "Y" -> color = Color.parseColor("#EBD23E")
//                else -> color = Color.BLACK
//            }
        }
    }

    private var linePaint: Paint = Paint().apply {
        color = Color.RED
        strokeWidth = 10f
        style = Paint.Style.STROKE
    }


    fun setLinePaint(lineCode: String) {
        when (lineCode) {



            "R22A" -> linePaint.color = ContextCompat.getColor(context,R.color.mrt_route_R22A)
            "G03A"-> linePaint.color = ContextCompat.getColor(context,R.color.mrt_route_G03A)
            "BL"->  linePaint.color = ContextCompat.getColor(context,R.color.mrt_route_BL)
            "R" ->linePaint.color = ContextCompat.getColor(context,R.color.mrt_route_R)
            "G"-> linePaint.color = ContextCompat.getColor(context,R.color.mrt_route_G)
            "BR"-> linePaint.color =ContextCompat.getColor(context,R.color.mrt_route_BR)
            "O"-> linePaint.color = ContextCompat.getColor(context,R.color.mrt_route_O)
            "Y" ->linePaint.color = ContextCompat.getColor(context,R.color.mrt_route_Y)
            else -> linePaint.color = Color.BLACK
        }
    }
    fun setStationTexts(startStation: String, endStation: String) {
        this.startStationText = startStation
        this.endStationText = endStation
    }
    fun setStationTextListener(listener: StationTextListener) {
        this.listener = listener
    }
    interface StationTextListener {
        fun onStationTextChanged(Station: String, location: String,code:String)

    }
    fun returnbtn(){
        click_count=0
    }


    init {
        // Populate station lists based on JSON data
        // You need to parse the JSON data and populate these lists accordingly
        // For now, I'm just populating some dummy data for demonstration purposes

        blueLineStations.addAll(
            listOf(
                Station("頂埔", "Dingpu", "頂埔", "딩푸", "顶埔", "BL01", PointF(100f * enlarge_size, screenHeight.toFloat())),
                Station("永寧", "Yongning", "永寧", "용닝", "永宁", "BL02", PointF(100f * enlarge_size, screenHeight.toFloat() - 200f * enlarge_size)),
                Station("土城", "Tucheng", "土城", "투청", "土城", "BL03", PointF(100f * enlarge_size, screenHeight.toFloat() - 350f * enlarge_size)),
                Station("海山", "Haishan", "海山", "하이산", "海山", "BL04", PointF(100f * enlarge_size, screenHeight.toFloat() - 500f * enlarge_size)),
                Station("亞東醫院", "Far Eastern Hospital", "亜東病院", "야둥 병원", "亚东医院", "BL05", PointF(100f * enlarge_size, screenHeight.toFloat() - 650f * enlarge_size)),
                Station("府中", "Fuzhong", "府中", "푸중", "府中", "BL06", PointF(100f * enlarge_size, screenHeight.toFloat() - 800f * enlarge_size)),
                Station("板橋", "Banqiao", "板橋", "반차오", "板桥", "BL07", PointF(100f * enlarge_size, screenHeight.toFloat() - 950f * enlarge_size)),
                Station("新埔", "Xinpu", "新埔", "신푸", "新埔", "BL08", PointF(100f * enlarge_size, screenHeight.toFloat() - 1100f * enlarge_size)),
                Station("江子翠", "Jiangzicui", "江子翠", "장쯔추이", "江子翠", "BL09", PointF(100f * enlarge_size, screenHeight.toFloat() - 1250f * enlarge_size)),
                Station("龍山寺", "Longshan Temple", "龍山寺", "용산사", "龙山寺", "BL10", PointF(300f * enlarge_size, screenHeight.toFloat() - 1400f * enlarge_size)),
                Station("西門", "Ximen", "西門", "시먼", "西门", "BL11", PointF(500f * enlarge_size, screenHeight.toFloat() - 1400f * enlarge_size)),
                Station("台北車站", "Taipei Main Station", "台北駅", "타이베이 역", "台北车站", "BL12", PointF(700f * enlarge_size, screenHeight.toFloat() - 1400f * enlarge_size)),
                Station("善導寺", "Shandao Temple", "善導寺", "산다오사", "善导寺", "BL13", PointF(900f * enlarge_size, screenHeight.toFloat() - 1400f * enlarge_size)),
                Station("忠孝新生", "Zhongxiao Xinsheng", "忠孝新生", "종샤오 신셩", "忠孝新生", "BL14", PointF(1100f * enlarge_size, screenHeight.toFloat() - 1400f * enlarge_size)),
                Station("忠孝復興", "Zhongxiao Fuxing", "忠孝復興", "종샤오 푸싱", "忠孝复兴", "BL15", PointF(1300f * enlarge_size, screenHeight.toFloat() - 1400f * enlarge_size)),
                Station("忠孝敦化", "Zhongxiao Dunhua", "忠孝敦化", "종샤오 둔화", "忠孝敦化", "BL16", PointF(1500f * enlarge_size, screenHeight.toFloat() - 1400f * enlarge_size)),
                Station("國父紀念館", "Sun Yat-Sen Memorial Hall", "国父紀念館", "국부기념관", "国父纪念馆", "BL17", PointF(1700f * enlarge_size, screenHeight.toFloat() - 1400f * enlarge_size)),
                Station("市政府", "Taipei City Hall", "台北市政府", "타이베이 시청", "台北市政府", "BL18", PointF(1900f * enlarge_size, screenHeight.toFloat() - 1400f * enlarge_size)),
                Station("永春", "Yongchun", "永春", "융춘", "永春", "BL19", PointF(2100f * enlarge_size, screenHeight.toFloat() - 1400f * enlarge_size)),
                Station("後山埤", "Houshanpi", "後山埤", "허우산피", "后山埤", "BL20", PointF(2300f * enlarge_size, screenHeight.toFloat() - 1400f * enlarge_size)),
                Station("昆陽", "Kunyang", "昆陽", "쿤양", "昆阳", "BL21", PointF(2300f * enlarge_size, screenHeight.toFloat() - 1550f * enlarge_size)),
                Station("南港", "Nangang", "南港", "난강", "南港", "BL22", PointF(2300f * enlarge_size, screenHeight.toFloat() - 1700f * enlarge_size)),
                Station("南港展覽館", "Taipei Nangang Exhibition Center", "南港展覧館", "난강 전람관", "南港展览馆", "BL23", PointF(2300f * enlarge_size, screenHeight.toFloat() - 1850f * enlarge_size))
            )

        )
        redLineStations.addAll(
            listOf(
                Station("象山", "Xiangshan", "象山", "샹산", "象山", "R02", PointF(1900f * enlarge_size, screenHeight.toFloat() - 1100f * enlarge_size)),
                Station("台北101/世貿", "Taipei 101/World Trade Center", "台北101/世界貿易センター", "타이베이101/ 세계무역센터", "台北101/世贸", "R03", PointF(1700f * enlarge_size, screenHeight.toFloat() - 1100f * enlarge_size)),
                Station("信義安和", "Xinyi Anhe", "信義安和", "신이 안허", "信义安和", "R04", PointF(1500f * enlarge_size, screenHeight.toFloat() - 1100f * enlarge_size)),
                Station("大安", "Daan", "大安", "다안", "大安", "R05", PointF(1300f * enlarge_size, screenHeight.toFloat() - 1100f * enlarge_size)),
                Station("大安森林公園", "Daan Park", "大安森林公園", "다안 삼림 공원", "大安森林公园", "R06", PointF(1200f * enlarge_size, screenHeight.toFloat() - 1100f * enlarge_size)),
                Station("東門", "Dongmen", "東門", "둥먼", "东门", "R07", PointF(1100f * enlarge_size, screenHeight.toFloat() - 1100f * enlarge_size)),
                Station("中正紀念堂", "Chiang Kai-Shek Memorial Hall", "中正紀念堂", "중정 기념당", "中正纪念堂", "R08", PointF(700f * enlarge_size, screenHeight.toFloat() - 1100f * enlarge_size)),
                Station("台大醫院", "NTU Hospital", "台湾大学病院", "대만 대학 병원", "台大医院", "R09", PointF(700f * enlarge_size, screenHeight.toFloat() - 1250f * enlarge_size)),
                Station("中山", "Zhongshan", "中山", "중산", "中山", "R11", PointF(700f * enlarge_size, screenHeight.toFloat() - 1650f * enlarge_size)),
                Station("雙連", "Shuanglian", "双連", "솽롄", "双连", "R12", PointF(700f * enlarge_size, screenHeight.toFloat() - 1800f * enlarge_size)),
                Station("民權西路", "Minquan W. Rd.", "民権西路", "민취안시루", "民权西路", "R13", PointF(700f * enlarge_size, screenHeight.toFloat() - 1950f * enlarge_size)),
                Station("圓山", "Yuanshan", "圓山", "위안샨", "圆山", "R14", PointF(700f * enlarge_size, screenHeight.toFloat() - 2100f * enlarge_size)),
                Station("劍潭", "Jiantan", "剣潭", "젠탄", "剑潭", "R15", PointF(700f * enlarge_size, screenHeight.toFloat() - 2250f * enlarge_size)),
                Station("士林", "Shilin", "士林", "스린", "士林", "R16", PointF(700f * enlarge_size, screenHeight.toFloat() - 2400f * enlarge_size)),
                Station("芝山", "Zhishan", "芝山", "즈산", "芝山", "R17", PointF(700f * enlarge_size, screenHeight.toFloat() - 2550f * enlarge_size)),
                Station("明德", "Mingde", "明徳", "밍더", "明德", "R18", PointF(700f * enlarge_size, screenHeight.toFloat() - 2700f * enlarge_size)),
                Station("石牌", "Shipai", "石牌", "스파이", "石牌", "R19", PointF(700f * enlarge_size, screenHeight.toFloat() - 2850f * enlarge_size)),
                Station("唭哩岸", "Qilian", "唭哩岸", "치리안", "唭哩岸", "R20", PointF(550f * enlarge_size, screenHeight.toFloat() - 2850f * enlarge_size)),
                Station("奇岩", "Qiyan", "奇岩", "치옌", "奇岩", "R21", PointF(400f * enlarge_size, screenHeight.toFloat() - 2850f * enlarge_size)),
                Station("北投", "Beitou", "北投", "베이터우", "北投", "R22", PointF(250f * enlarge_size, screenHeight.toFloat() - 2850f * enlarge_size)),
                Station("復興崗", "Fuxinggang", "復興崗", "푸싱강", "复兴岗", "R23", PointF(100f * enlarge_size, screenHeight.toFloat() - 2850f * enlarge_size)),
                Station("忠義", "Zhongyi", "忠義", "종이", "忠义", "R24", PointF(-50f * enlarge_size, screenHeight.toFloat() - 2850f * enlarge_size)),
                Station("關渡", "Guandu", "関渡", "관두", "关渡", "R25", PointF(-50f * enlarge_size, screenHeight.toFloat() - 3000f * enlarge_size)),
                Station("竹圍", "Zhuwei", "竹囲", "주웨이", "竹围", "R26", PointF(-50f * enlarge_size, screenHeight.toFloat() - 3150f * enlarge_size)),
                Station("紅樹林", "Hongshulin", "紅樹林", "홍수림", "红树林", "R27", PointF(-50f * enlarge_size, screenHeight.toFloat() - 3300f * enlarge_size)),
                Station("淡水", "Tamsui", "淡水", "단수이", "淡水", "R28", PointF(-50f * enlarge_size, screenHeight.toFloat() - 3450f * enlarge_size))
            )
        )
        red_2_LineStations.addAll(
            listOf(
                Station("北投", "Beitou", "北投", "베이터우", "北投", "R22_1", PointF(250f * enlarge_size, screenHeight.toFloat() - 2850f * enlarge_size)),
                Station("新北投", "Xinbeitou", "新北投", "신베이터우", "新北投", "R22A", PointF(250f * enlarge_size, screenHeight.toFloat() - 3100f * enlarge_size))
            )

        )
        greenLineStations.addAll(
            listOf(
                Station("新店", "Xindian", "新店", "신덴", "新店", "G01", PointF(1600f * enlarge_size, screenHeight.toFloat() - 50f * enlarge_size)),
                Station("新店區公所", "Xindian District Office", "新店区役所", "신뎬구청", "新店区公所", "G02", PointF(1600f * enlarge_size, screenHeight.toFloat() - 200f * enlarge_size)),
                Station("七張", "Qizhang", "七張", "치장", "七张", "G03", PointF(1600f * enlarge_size, screenHeight.toFloat() - 350f * enlarge_size)),
//                Station("小碧潭", "Xiaobitan", "小碧潭", "샤오비탄", "小碧潭", "G03A", PointF(1450f * enlarge_size, screenHeight.toFloat() - 350f * enlarge_size)),
                Station("大坪林", "Dapinglin", "大坪林", "다핑린", "大坪林", "G04", PointF(1600f * enlarge_size, screenHeight.toFloat() - 500f * enlarge_size)),
                Station("景美", "Jingmei", "景美", "징메이", "景美", "G05", PointF(1450f * enlarge_size, screenHeight.toFloat() - 600f * enlarge_size)),
                Station("萬隆", "Wanlong", "万隆", "완롱", "万隆", "G06", PointF(1300f * enlarge_size, screenHeight.toFloat() - 700f * enlarge_size)),
                Station("公館", "Gongguan", "公館", "공관", "公馆", "G07", PointF(1150f * enlarge_size, screenHeight.toFloat() - 800f * enlarge_size)),
                Station("台電大樓", "Taipower Building", "台湾電力ビル", "대만 전력공사 빌딩", "台电大楼", "R08", PointF(1000f * enlarge_size, screenHeight.toFloat() - 900f * enlarge_size)),
                Station("古亭", "Guting", "古亭", "구팅", "古亭", "G09", PointF(850f * enlarge_size, screenHeight.toFloat() - 1000f * enlarge_size)),
                Station("中正紀念堂", "Chiang Kai-Shek Memorial Hall", "中正紀念堂", "중정 기념당", "中正纪念堂", "R08", PointF(700f * enlarge_size, screenHeight.toFloat() - 1100f * enlarge_size)),
                Station("小南門", "Xiaonanmen", "小南門", "샤오난먼", "小南门", "G11", PointF(500f * enlarge_size, screenHeight.toFloat() - 1100f * enlarge_size)),
                Station("北門", "Beimen", "北門", "베이먼", "北门", "G13", PointF(500f * enlarge_size, screenHeight.toFloat() - 1650f * enlarge_size)),
                Station("松江南京", "Songjiang Nanjing", "松江南京", "송장 난징", "松江南京", "G15", PointF(1100f * enlarge_size, screenHeight.toFloat() - 1650f * enlarge_size)),
                Station("南京復興", "Nanjing Fuxing", "南京復興", "난징 푸싱", "南京复兴", "G16", PointF(1300f * enlarge_size, screenHeight.toFloat() - 1650f * enlarge_size)),
                Station("台北小巨蛋", "Taipei Arena", "台北アリーナ", "타이베이 아레나", "台北小巨蛋", "G17", PointF(1500f * enlarge_size, screenHeight.toFloat() - 1650f * enlarge_size)),
                Station("南京三民", "Nanjing Sanmin", "南京三民", "난징 싼민", "南京三民", "G18", PointF(1700f * enlarge_size, screenHeight.toFloat() - 1650f * enlarge_size)),
                Station("松山", "Songshan", "松山", "송산", "松山", "G19", PointF(1900f * enlarge_size, screenHeight.toFloat() - 1650f * enlarge_size))
            )

        )
        green_2_LineStations.addAll(
            listOf(
                Station("小碧潭", "Xiaobitan", "小碧潭", "샤오비탄", "小碧潭", "G03A", PointF(1450f * enlarge_size, screenHeight.toFloat() - 350f * enlarge_size)),
                Station("七張", "Qizhang", "七張", "치장", "七张", "G03_1", PointF(1600f * enlarge_size, screenHeight.toFloat() - 350f * enlarge_size))
            )

        )
        brownLineStations.addAll(
            listOf(
                Station("動物園", "Taipei Zoo", "動物園", "동물원", "动物园", "BR01", PointF(2050f * enlarge_size, screenHeight.toFloat() - 300f * enlarge_size)),
                Station("木柵", "Muzha", "木柵", "무자", "木栅", "BR02", PointF(1900f * enlarge_size, screenHeight.toFloat() - 300f * enlarge_size)),
                Station("萬芳社區", "Wanfang Community", "万芳コミュニティ", "완팡 단지", "万芳社区", "BR03", PointF(1750f * enlarge_size, screenHeight.toFloat() - 400f * enlarge_size)),
                Station("萬芳醫院", "Wanfang Hospital", "万芳病院", "완팡 병원", "万芳医院", "BR04", PointF(1750f * enlarge_size, screenHeight.toFloat() - 650f * enlarge_size)),
                Station("辛亥", "Xinhai", "辛亥", "신하이", "辛亥", "BR05", PointF(1750f * enlarge_size, screenHeight.toFloat() - 800f * enlarge_size)),
                Station("麟光", "Linguang", "麟光", "링광", "麟光", "BR06", PointF(1700f * enlarge_size, screenHeight.toFloat() - 900f * enlarge_size)),
                Station("六張犁", "Liuzhangli", "六張犁", "류장리", "六张犁", "BR07", PointF(1500f * enlarge_size, screenHeight.toFloat() - 900f * enlarge_size)),
                Station("科技大樓", "Technology Building", "テクノロジービル", "테크놀로지 빌딩", "科技大楼", "BR08", PointF(1300f * enlarge_size, screenHeight.toFloat() - 900f * enlarge_size)),
                Station("中山國中", "Zhongshan Junior High School", "中山中学校", "중산 중학교", "中山国中", "BR12", PointF(1300f * enlarge_size, screenHeight.toFloat() - 1800f * enlarge_size)),
                Station("松山機場", "Songshan Airport", "松山空港", "송산 공항", "松山机场", "BR13", PointF(1300f * enlarge_size, screenHeight.toFloat() - 1950f * enlarge_size)),
                Station("大直", "Dazhi", "大直", "다즈", "大直", "BR14", PointF(1300f * enlarge_size, screenHeight.toFloat() - 2100f * enlarge_size)),
                Station("劍南路", "Jiannan Rd.", "剣南路", "젠난루", "剑南路", "BR15", PointF(1300f * enlarge_size, screenHeight.toFloat() - 2250f * enlarge_size)),
                Station("西湖", "Xihu", "西湖", "시후", "西湖", "BR16", PointF(1500f * enlarge_size, screenHeight.toFloat() - 2250f * enlarge_size)),
                Station("港墘", "Gangqian", "港墘", "강첸", "港墘", "BR17", PointF(1650f * enlarge_size, screenHeight.toFloat() - 2250f * enlarge_size)),
                Station("文德", "Wende", "文徳", "원더", "文德", "BR18", PointF(1850f * enlarge_size, screenHeight.toFloat() - 2250f * enlarge_size)),
                Station("內湖", "Neihu", "内湖", "네이후", "内湖", "BR19", PointF(2000f * enlarge_size, screenHeight.toFloat() - 2250f * enlarge_size)),
                Station("大湖公園", "Dahu Park", "大湖公園", "다후 공원", "大湖公园", "BR20", PointF(2200f * enlarge_size, screenHeight.toFloat() - 2250f * enlarge_size)),
                Station("葫洲", "Huzhou", "葫洲", "후저우", "葫洲", "BR21", PointF(2300f * enlarge_size, screenHeight.toFloat() - 2250f * enlarge_size)),
                Station("東湖", "Donghu", "東湖", "둥후", "东湖", "BR22", PointF(2300f * enlarge_size, screenHeight.toFloat() - 2100f * enlarge_size)),
                Station("南港軟體園區", "Nangang Software Park", "南港ソフトウェアパーク", "난강 소프트웨어 단지", "南港软体园区", "BR23", PointF(2300f * enlarge_size, screenHeight.toFloat() - 1950f * enlarge_size)),
                Station("南港展覽館", "Taipei Nangang Exhibition Center", "南港展覧館", "난강 전람관", "南港展览馆", "BL23", PointF(2300f * enlarge_size, screenHeight.toFloat() - 1850f * enlarge_size))
            )
        )
        orangeLineStations.addAll(
            listOf(
                Station("南勢角", "Nanshijiao", "南勢角", "난시자오", "南势角", "O01", PointF(800f * enlarge_size, screenHeight.toFloat() - 350f * enlarge_size)),
                Station("景安", "Jingan", "景安", "징안", "景安", "O02", PointF(800f * enlarge_size, screenHeight.toFloat() - 600f * enlarge_size)),
                Station("永安市場", "Yongan Market", "永安市場", "융안 시장", "永安市场", "O03", PointF(800f * enlarge_size, screenHeight.toFloat() - 750f * enlarge_size)),
                Station("頂溪", "Dingxi", "頂溪", "딩시", "顶溪", "O04", PointF(800f * enlarge_size, screenHeight.toFloat() - 900f * enlarge_size)),
                Station("古亭", "Guting", "古亭", "구팅", "古亭", "O05", PointF(850f * enlarge_size, screenHeight.toFloat() - 1000f * enlarge_size)),
                Station("東門", "Dongmen", "東門", "둥먼", "东门", "O06", PointF(1100f * enlarge_size, screenHeight.toFloat() - 1100f * enlarge_size)),
                Station("行天宮", "Xingtian Temple", "行天宮", "싱톈궁", "行天宫", "O09", PointF(1100f * enlarge_size, screenHeight.toFloat() - 1800f * enlarge_size)),
                Station("中山國小", "Zhongshan Elementary School", "中山小学校", "중산 초등학교", "中山国小", "O10", PointF(950f * enlarge_size, screenHeight.toFloat() - 1950f * enlarge_size)),
                Station("大橋頭", "Daqiaotou", "大橋頭", "다차오터우", "大桥头", "O12", PointF(550f * enlarge_size, screenHeight.toFloat() - 1950f * enlarge_size)),
                Station("台北橋", "Taipei Bridge", "台北橋", "타이페이 대교", "台北桥", "O13", PointF(400f * enlarge_size, screenHeight.toFloat() - 1800f * enlarge_size)),
                Station("菜寮", "Cailiao", "菜寮", "차이랴오", "菜寮", "O14", PointF(250f * enlarge_size, screenHeight.toFloat() - 1650f * enlarge_size)),
                Station("三重", "Sanchong", "三重", "싼충", "三重", "O15", PointF(100f * enlarge_size, screenHeight.toFloat() - 1500f * enlarge_size)),
                Station("先嗇宮", "Xianse Temple", "先嗇宮", "셴써궁", "先啬宫", "O16", PointF(-50f * enlarge_size, screenHeight.toFloat() - 1350f * enlarge_size)),
                Station("頭前庄", "Touqianzhuang", "頭前庄", "터우첸좡", "头前庄", "O17", PointF(-200f * enlarge_size, screenHeight.toFloat() - 1200f * enlarge_size)),
                Station("新莊", "Xinzhuang", "新莊", "신좡", "新庄", "O18", PointF(-350f * enlarge_size, screenHeight.toFloat() - 1050f * enlarge_size)),
                Station("輔大", "Fu Jen University", "輔仁大学", "푸런 대학교", "辅大", "O19", PointF(-350f * enlarge_size, screenHeight.toFloat() - 900f * enlarge_size)),
                Station("丹鳳", "Danfeng", "丹鳳", "단펑", "丹凤", "O20", PointF(-350f * enlarge_size, screenHeight.toFloat() - 750f * enlarge_size)),
                Station("迴龍", "Huilong", "迴龍", "후이룽", "回龙", "O21", PointF(-350f * enlarge_size, screenHeight.toFloat() - 600f * enlarge_size))
            )

        )

        orange_2_LineStations.addAll(
            listOf(
                Station("大橋頭", "Daqiaotou", "大橋頭", "다차오터우", "大桥头", "O12_1", PointF(550f * enlarge_size, screenHeight.toFloat() - 1950f * enlarge_size)),
                Station("三重國小", "Sanchong Elementary School", "三重小学校", "싼충 초등학교", "三重国小", "O50", PointF(450f * enlarge_size, screenHeight.toFloat() - 2100f * enlarge_size)),
                Station("三和國中", "Sanhe Junior High School", "三和中学校", "싼허 중학교", "三和国中", "O51", PointF(350f * enlarge_size, screenHeight.toFloat() - 2200f * enlarge_size)),
                Station("徐匯中學", "St. Ignatius High School", "徐匯高校", "쉬후이 고등학교", "徐汇中学", "O52", PointF(250f * enlarge_size, screenHeight.toFloat() - 2300f * enlarge_size)),
                Station("三民高中", "Sanmin Senior High School", "三民高校", "산민 고등학교", "三民高中", "O53", PointF(150f * enlarge_size, screenHeight.toFloat() - 2400f * enlarge_size)),
                Station("蘆洲", "Luzhou", "蘆洲", "루저우", "芦洲", "O54", PointF(50f * enlarge_size, screenHeight.toFloat() - 2500f * enlarge_size))
            )

        )

        yellowLineStations.addAll(
            listOf(
                Station("大坪林", "Dapinglin", "大坪林", "다핑린", "大坪林", "Y07", PointF(1600f * enlarge_size, screenHeight.toFloat() - 500f * enlarge_size)),
                Station("十四張", "Shisijhang", "十四張", "스쓰진", "十四张", "Y08", PointF(1400f * enlarge_size, screenHeight.toFloat() - 500f * enlarge_size)),
                Station("秀朗橋", "Xiulang Bridge", "秀朗橋", "시우랑챠오", "秀朗桥", "Y09", PointF(1200f * enlarge_size, screenHeight.toFloat() - 600f * enlarge_size)),
                Station("景平", "Jingping", "景平", "징핑", "景平", "Y10", PointF(1000f * enlarge_size, screenHeight.toFloat() - 600f * enlarge_size)),
                Station("景安", "Jingan", "景安", "징안", "景安", "Y11_1", PointF(800f * enlarge_size, screenHeight.toFloat() - 600f * enlarge_size)),
                Station("中和", "Zhonghe", "中和", "중허", "中和", "Y12", PointF(650f * enlarge_size, screenHeight.toFloat() - 600f * enlarge_size)),
                Station("橋和", "Qiaohe", "橋和", "챠오허", "桥和", "Y13", PointF(570f * enlarge_size, screenHeight.toFloat() - 700f * enlarge_size)),
                Station("中原", "Zhongyuan", "中原", "종위안", "中原", "Y14", PointF(450f * enlarge_size, screenHeight.toFloat() - 750f * enlarge_size)),
                Station("板新", "Banxin", "板新", "반신", "板新", "Y15", PointF(350f * enlarge_size, screenHeight.toFloat() - 800f * enlarge_size)),
                Station("板橋", "Banqiao", "板橋", "반차오", "板桥", "Y16", PointF(280f * enlarge_size, screenHeight.toFloat() - 950f * enlarge_size)),
                Station("新埔民生", "Xinpu Minsheng", "新埔民生", "신푸민성", "新埔民生", "Y17", PointF(-50f * enlarge_size, screenHeight.toFloat() - 1100f * enlarge_size)),
                Station("頭前庄", "Touqianzhuang", "頭前庄", "터우첸좡", "头前庄", "Y18_1", PointF(-200f * enlarge_size, screenHeight.toFloat() - 1200f * enlarge_size)),
                Station("幸福", "Xingfu", "幸福", "씽푸", "幸福", "Y19", PointF(-200f * enlarge_size, screenHeight.toFloat() - 1450f * enlarge_size)),
                Station("新北產業園區", "Xinbei Industrial Park", "新北産業団地", "신베이 산업원 단지", "新北产业园区", "Y20", PointF(-200f * enlarge_size, screenHeight.toFloat() - 1600f * enlarge_size))
            )

        )
        // Add other line stations similarly



    }

}


