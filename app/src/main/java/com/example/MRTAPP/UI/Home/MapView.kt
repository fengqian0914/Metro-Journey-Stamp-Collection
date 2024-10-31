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

            val stationName_TW = "${station.chineseName}"
            val stationName_EN = "(${station.englishName})"

            val stationCode = station.code

            setLinePaint(lineCode)
            setStationPaint(lineCode)

            canvas.drawCircle(station.position.x, station.position.y, 15f, stationPaint)
            canvas.drawCircle(station.position.x, station.position.y, 15f, linePaint)
            when (stationCode) {
         
                in "R07"->{
                    chineseTextPaint.textAlign = Paint.Align.LEFT
                    englishTextPaint.textAlign = Paint.Align.LEFT
                    codePaint.textAlign = Paint.Align.LEFT

                    canvas.drawText(stationName_TW, x , y+60f, chineseTextPaint)
                    canvas.drawText(stationName_EN, x , y+100f, englishTextPaint)
                    canvas.drawText(stationCode, x , y + 140f, createCodeTextPaint(lineCode))
                }
                in "G08"->{
                    chineseTextPaint.textAlign = Paint.Align.LEFT
                    englishTextPaint.textAlign = Paint.Align.LEFT
                    codePaint.textAlign=Paint.Align.LEFT

                    canvas.drawText(stationName_TW, x-80f , y+65f, chineseTextPaint)
                    canvas.drawText(stationName_EN, x-80f , y+105f, englishTextPaint)
                    canvas.drawText(stationCode, x-80f , y+145f, createCodeTextPaint(lineCode))
                }
                in "G09","G03A"->{
                    chineseTextPaint.textAlign = Paint.Align.RIGHT
                    englishTextPaint.textAlign = Paint.Align.RIGHT
                    codePaint.textAlign=Paint.Align.RIGHT

                    canvas.drawText(stationName_TW, x -70f, y, chineseTextPaint)
                    canvas.drawText(stationName_EN, x - 70f, y+35f, englishTextPaint)
                    canvas.drawText(stationCode, x - 120f, y+70f, createCodeTextPaint(lineCode))
                }
                in "O12"->{
                    chineseTextPaint.textAlign = Paint.Align.RIGHT
                    englishTextPaint.textAlign = Paint.Align.RIGHT
                    codePaint.textAlign=Paint.Align.RIGHT

                    canvas.drawText(stationName_TW, x -120f, y-10f, chineseTextPaint)
                    canvas.drawText(stationName_EN, x -120f, y+20f, englishTextPaint)
                    canvas.drawText(stationCode, x - 170f, y+50f, createCodeTextPaint(lineCode))
                }
                in "O50".."O54"->{
                    chineseTextPaint.textAlign = Paint.Align.RIGHT
                    englishTextPaint.textAlign = Paint.Align.RIGHT
                    codePaint.textAlign=Paint.Align.RIGHT

                    canvas.drawText(stationName_TW, x-80f , y+60f, chineseTextPaint)
                    canvas.drawText(stationName_EN, x-80f , y+90f, englishTextPaint)
                    canvas.drawText(stationCode, x-130f , y+120f, createCodeTextPaint(lineCode))
                }
                in "BL09"->{
                    chineseTextPaint.textAlign = Paint.Align.LEFT
                    englishTextPaint.textAlign = Paint.Align.LEFT
                    codePaint.textAlign=Paint.Align.LEFT

                    canvas.drawText(stationName_TW, x + 30f, y+20f, chineseTextPaint)
                    canvas.drawText(stationName_EN, x + 30f, y+55f, englishTextPaint)
                    canvas.drawText(stationCode, x + 30f, y + 90f, createCodeTextPaint(lineCode))
                }
                in "R09"->{
                    chineseTextPaint.textAlign = Paint.Align.LEFT
                    englishTextPaint.textAlign = Paint.Align.LEFT
                    codePaint.textAlign=Paint.Align.LEFT

                    canvas.drawText(stationName_TW, x + 30f, y-40f, chineseTextPaint)
                    canvas.drawText(stationName_EN, x + 30f, y-10f, englishTextPaint)
                    canvas.drawText(stationCode, x + 30f, y +20f, createCodeTextPaint(lineCode))
                }
                in "R11","R13"->{ //交叉站 右下
                    chineseTextPaint.textAlign = Paint.Align.LEFT
                    englishTextPaint.textAlign = Paint.Align.LEFT
                    codePaint.textAlign=Paint.Align.LEFT

                    canvas.drawText(stationName_TW, x +40f, y+50f, chineseTextPaint)
                    canvas.drawText(stationName_EN, x +40f, y+80f, englishTextPaint)

                    canvas.drawText(stationCode, x +40f, y+110f, createCodeTextPaint(lineCode))
                }
                in "O05","O06","Y07","O12_1","R22_1","G03_1","Y11_1","Y18_1"->{ //不顯示

                }in "O17"->{
                chineseTextPaint.textAlign = Paint.Align.CENTER
                englishTextPaint.textAlign = Paint.Align.CENTER
                codePaint.textAlign=Paint.Align.CENTER

                canvas.drawText(stationName_TW, x -80f, y-60f, chineseTextPaint)
                canvas.drawText(stationName_EN, x - 80f, y-30f, englishTextPaint)

                canvas.drawText(stationCode, x - 80f, y, createCodeTextPaint(lineCode))
                }
                in "Y17","Y09"->{ //左下
                    chineseTextPaint.textAlign = Paint.Align.LEFT
                    englishTextPaint.textAlign = Paint.Align.LEFT
                    codePaint.textAlign=Paint.Align.LEFT

                    canvas.drawText(stationName_TW, x-80f , y+60f, chineseTextPaint)
                    canvas.drawText(stationName_EN, x-80f , y+100f, englishTextPaint)
                    canvas.drawText(stationCode, x-80f , y+140f, createCodeTextPaint(lineCode))
                }
                in "R06",in "BR15".."BR20","BL17"->{ //左上
                    chineseTextPaint.textAlign = Paint.Align.LEFT
                    englishTextPaint.textAlign = Paint.Align.LEFT
                    codePaint.textAlign=Paint.Align.LEFT

                    canvas.drawText(stationName_TW, x-70f , y-120f, chineseTextPaint)
                    canvas.drawText(stationName_EN, x-70f , y-80f, englishTextPaint)
                    canvas.drawText(stationCode, x-70f , y-40f, createCodeTextPaint(lineCode))
                }
                in "BL12",in "G04".."G07" ,in "BR03","BR06","G13",in "Y13".."Y16","R08",in "BR08","R05","O10"-> { //右上
                    chineseTextPaint.textAlign = Paint.Align.LEFT
                    englishTextPaint.textAlign = Paint.Align.LEFT
                    codePaint.textAlign=Paint.Align.LEFT
                    canvas.drawText(stationName_TW, x + 30f, y-100f, chineseTextPaint)
                    canvas.drawText(stationName_EN, x + 30f, y-60f, englishTextPaint)
                    canvas.drawText(stationCode, x + 30f, y-20f, createCodeTextPaint(lineCode))
                }
                in "G12".."G13"->{ //左方
                    chineseTextPaint.textAlign = Paint.Align.RIGHT
                    englishTextPaint.textAlign = Paint.Align.RIGHT
                    codePaint.textAlign=Paint.Align.RIGHT

                    canvas.drawText(stationName_TW, x -40f, y, chineseTextPaint)
                    canvas.drawText(stationName_EN, x - 40f, y+40f, englishTextPaint)
                    canvas.drawText(stationCode, x - 90f, y+80f, createCodeTextPaint(lineCode))
                }
                in "G11"->{
                    chineseTextPaint.textAlign = Paint.Align.RIGHT
                    englishTextPaint.textAlign = Paint.Align.RIGHT
                    codePaint.textAlign=Paint.Align.RIGHT

                    canvas.drawText(stationName_TW, x -40f, y-60f, chineseTextPaint)
                    canvas.drawText(stationName_EN, x - 40f, y-20f, englishTextPaint)
                    canvas.drawText(stationCode, x - 90f, y+20f, createCodeTextPaint(lineCode))
                }
                in "BL10".."BL16",in "BL18".."BL20" ,in "R02".."R04",in "R08" ,in "R20".."R24",in "G15".."G19","BR02",in "Y10".."Y12","Y08",in "BR07","O02"-> { // 下方
                    chineseTextPaint.textAlign = Paint.Align.LEFT
                    englishTextPaint.textAlign = Paint.Align.LEFT
                    codePaint.textAlign = Paint.Align.LEFT

                    canvas.drawText(stationName_TW, x+20f , y+60f, chineseTextPaint)
                    canvas.drawText(stationName_EN, x+20f , y+100f, englishTextPaint)
                    canvas.drawText(stationCode, x+20f , y + 140f, createCodeTextPaint(lineCode))

                }

                else->{ //右方
                    chineseTextPaint.textAlign = Paint.Align.LEFT
                    englishTextPaint.textAlign = Paint.Align.LEFT
                    codePaint.textAlign=Paint.Align.LEFT

                    canvas.drawText(stationName_TW, x + 30f, y, chineseTextPaint)
                    canvas.drawText(stationName_EN, x + 30f, y+35f, englishTextPaint)
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
                        if(click_count==0){
                            startStatiodId=station.code
                            listener?.onStationTextChanged(station.chineseName,"start",station.code)
                            Log.d("title","0$station.chineseName")
                            click_count++

                        }else if(click_count==1){
                            EndStatiodId=station.code

                            if(startStatiodId==EndStatiodId){
                                Log.d("title","重複.chineseName")
                            }else {

                                listener?.onStationTextChanged(station.chineseName, "end", station.code)
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
        textSize = Alltextsize
        isFakeBoldText = true
        isAntiAlias = true
    }

    private val englishTextPaint: Paint = Paint().apply {
        color = Color.WHITE
        textSize = Alltextsize*0.7f
        isAntiAlias = true
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
                Station("頂埔", "Dingpu", "BL01", PointF(100f*enlarge_size, screenHeight.toFloat())),
                Station("永寧", "Yongning", "BL02", PointF(100f*enlarge_size, screenHeight.toFloat() - 200f*enlarge_size)),
                Station("土城", "Tucheng", "BL03", PointF(100f*enlarge_size, screenHeight.toFloat() - 350f*enlarge_size)),
                Station("海山", "Haishan", "BL04", PointF(100f*enlarge_size, screenHeight.toFloat() - 500f*enlarge_size)),
                Station("亞東醫院", "Far Eastern Hospital", "BL05", PointF(100f*enlarge_size, screenHeight.toFloat() - 650f*enlarge_size)),
                Station("府中", "Fuzhong", "BL06", PointF(100f*enlarge_size, screenHeight.toFloat() - 800f*enlarge_size)),
                Station("板橋", "Banqiao", "BL07", PointF(100f*enlarge_size, screenHeight.toFloat() - 950f*enlarge_size)),
                Station("新埔", "Xinpu", "BL08", PointF(100f*enlarge_size, screenHeight.toFloat() - 1100f*enlarge_size)),
                Station("江子翠", "Jiangzicui", "BL09", PointF(100f*enlarge_size, screenHeight.toFloat() - 1250f*enlarge_size)),
                Station("龍山寺", "Longshan\nTemple", "BL10", PointF(300f*enlarge_size, screenHeight.toFloat() - 1400f*enlarge_size)),
                Station("西門", "Ximen", "BL11", PointF(500f*enlarge_size, screenHeight.toFloat() - 1400f*enlarge_size)),
                Station("台北車站", "Taipei Main \nStation", "BL12", PointF(700f*enlarge_size, screenHeight.toFloat() - 1400f*enlarge_size)),
                Station("善導寺", "Shandao Temple", "BL13", PointF(900f*enlarge_size, screenHeight.toFloat() - 1400f*enlarge_size)),
                Station("忠孝新生", "Zhongxiao \nXinsheng", "BL14", PointF(1100f*enlarge_size, screenHeight.toFloat() - 1400f*enlarge_size)),
                Station("忠孝復興", "Zhongxiao\n Fuxing", "BL15", PointF(1300f*enlarge_size, screenHeight.toFloat() - 1400f*enlarge_size)),
                Station("忠孝敦化", "Zhongxiao \nDunhua", "BL16", PointF(1500f*enlarge_size, screenHeight.toFloat() - 1400f*enlarge_size)),
                Station("國父紀念館", "Sun Yat-Sen \nMemorial Hall", "BL17", PointF(1700f*enlarge_size, screenHeight.toFloat() - 1400f*enlarge_size)),
                Station("市政府", "Taipei City Hall", "BL18", PointF(1900f*enlarge_size, screenHeight.toFloat() - 1400f*enlarge_size)),
                Station("永春", "Yongchun", "BL19", PointF(2100f*enlarge_size, screenHeight.toFloat() - 1400f*enlarge_size)),
                Station("後山埤", "Houshanpi", "BL20", PointF(2300f*enlarge_size, screenHeight.toFloat() - 1400f*enlarge_size)),
                Station("昆陽", "Kunyang", "BL21", PointF(2300f*enlarge_size, screenHeight.toFloat() - 1550f*enlarge_size)),
                Station("南港", "Nangang", "BL22", PointF(2300f*enlarge_size, screenHeight.toFloat() - 1700f*enlarge_size)),
                Station("南港展覽館", "Taipei Nangang Exhibition Center", "BL23", PointF(2300f*enlarge_size, screenHeight.toFloat() - 1850f*enlarge_size))
            )
        )
        redLineStations.addAll(
            listOf(
                Station("象山","Xiangshan","R02",PointF(1900f*enlarge_size, screenHeight.toFloat() - 1100f*enlarge_size)),
                Station("台北101/世貿","Taipei 101 / World Trade Center","R03",PointF(1700f*enlarge_size, screenHeight.toFloat() - 1100f*enlarge_size)),
                Station("信義安和","Xinyi Anhe","R04",PointF(1500f*enlarge_size, screenHeight.toFloat() - 1100f*enlarge_size)),


                Station("大安","Daan","R05",PointF(1300f*enlarge_size, screenHeight.toFloat() - 1100f*enlarge_size)),
                Station("大安森林公園","Daan Park","R06",PointF(1200f*enlarge_size, screenHeight.toFloat() - 1100f*enlarge_size)),

                Station("東門","Dongmen","R07",PointF(1100f*enlarge_size, screenHeight.toFloat() - 1100f*enlarge_size)),

                Station("中正紀念堂","Chiang Kai-Shek Memorial Hall","R08",PointF(700f*enlarge_size, screenHeight.toFloat() - 1100f*enlarge_size)),
                Station("台大醫院","National Taiwan University Hospital","R09",PointF(700f*enlarge_size, screenHeight.toFloat() - 1250f*enlarge_size)),
                Station("中山","Zhongshan","R11",PointF(700f*enlarge_size, screenHeight.toFloat() - 1650f*enlarge_size)),
                Station("雙連","Shuanglian ","R12",PointF(700f*enlarge_size, screenHeight.toFloat() - 1800f*enlarge_size)),
                Station("民權西路","Minquan W. Rd.","R13",PointF(700f*enlarge_size, screenHeight.toFloat() - 1950f*enlarge_size)),
                Station("圓山","Yuanshan","R14",PointF(700f*enlarge_size, screenHeight.toFloat() - 2100f*enlarge_size)),
                Station("劍潭","Jiantan ","R15",PointF(700f*enlarge_size, screenHeight.toFloat() - 2250f*enlarge_size)),
                Station("士林","Shilin","R16",PointF(700f*enlarge_size, screenHeight.toFloat() - 2400f*enlarge_size)),
                Station("芝山","Zhishan","R17",PointF(700f*enlarge_size, screenHeight.toFloat() - 2550f*enlarge_size)),
                Station("明德","Mingde","R18",PointF(700f*enlarge_size, screenHeight.toFloat() - 2700f*enlarge_size)),
                Station("石牌","Shipai ","R19",PointF(700f*enlarge_size, screenHeight.toFloat() - 2850f*enlarge_size)),
                Station("唭哩岸","Qilian","R20",PointF(550f*enlarge_size, screenHeight.toFloat() - 2850f*enlarge_size)),
                Station("奇岩","Qiyan","R21",PointF(400f*enlarge_size, screenHeight.toFloat() - 2850f*enlarge_size)),
                Station("北投","Beitou","R22",PointF(250f*enlarge_size, screenHeight.toFloat() - 2850f*enlarge_size)),
                Station("復興崗","Fuxinggang","R23",PointF(100f*enlarge_size, screenHeight.toFloat() - 2850f*enlarge_size)),
                Station("忠義","Zhongyi","R24",PointF(-50f*enlarge_size, screenHeight.toFloat() - 2850f*enlarge_size)),

                Station("關渡","Guandu","R25",PointF(-50f*enlarge_size, screenHeight.toFloat() - 3000f*enlarge_size)),
                Station("竹圍","Zhuwei","R26",PointF(-50f*enlarge_size, screenHeight.toFloat() - 3150f*enlarge_size)),
                Station("紅樹林","Hongshulin","R27",PointF(-50f*enlarge_size, screenHeight.toFloat() - 3300f*enlarge_size)),
                Station("淡水","Tamsui","R28",PointF(-50f*enlarge_size, screenHeight.toFloat() - 3450f*enlarge_size)),
            )
        )
        red_2_LineStations.addAll(
            listOf(

                Station("北投","Beitou","R22_1",PointF(250f*enlarge_size, screenHeight.toFloat() - 2850f*enlarge_size)),
                Station("新北投","Xinbeitou","R22A",PointF(250f*enlarge_size, screenHeight.toFloat() - 3100f*enlarge_size)),
            )
        )
        greenLineStations.addAll(
            listOf(
                Station("新店(碧潭)","Xindian","G01",PointF(1600f*enlarge_size, screenHeight.toFloat() -50f*enlarge_size)),
                Station("新店區公所","Xindian District Office","G02",PointF(1600f*enlarge_size, screenHeight.toFloat() -200f*enlarge_size)),
//                Station("小碧潭(新店高中)","Xiaobitan (Xindian Senior High School)","G03A",PointF(1450f*enlarge_size, screenHeight.toFloat() - 350f*enlarge_size)),
                Station("七張","Qizhang","G03",PointF(1600f*enlarge_size, screenHeight.toFloat() -350f*enlarge_size)),
                Station("大坪林","Dapinglin","G04",PointF(1600f*enlarge_size, screenHeight.toFloat() - 500f*enlarge_size)),
                Station("景美","Jingmei","G05",PointF(1450f*enlarge_size, screenHeight.toFloat() - 600f*enlarge_size)),
                Station("萬隆","Wanlong","G06",PointF(1300f*enlarge_size, screenHeight.toFloat() - 700f*enlarge_size)),
                Station("公館","Gongguan","G07",PointF(1150f*enlarge_size, screenHeight.toFloat() - 800f*enlarge_size)),
                Station("台電大樓","Taipower Building","G08",PointF(1000f*enlarge_size, screenHeight.toFloat() -900f*enlarge_size)),
                Station("古亭","Guting","G09",PointF(850f*enlarge_size, screenHeight.toFloat() - 1000f*enlarge_size)),
                Station("中正紀念堂","Chiang Kai-Shek Memorial Hall","R08",PointF(700f*enlarge_size, screenHeight.toFloat() - 1100f*enlarge_size)),
                Station("小南門","Xiaonanmen","G11",PointF(500f*enlarge_size, screenHeight.toFloat() - 1100f*enlarge_size)),
//                Station("西門", "Ximen", "G12", PointF(500f*enlarge_size, screenHeight.toFloat() - 1400f*enlarge_size)),
                Station("北門","Beimen ","G13",PointF(500f*enlarge_size, screenHeight.toFloat() - 1650f*enlarge_size)),
//                Station("中山","Zhongshan","G14",PointF(700f*enlarge_size, screenHeight.toFloat() - 1650f*enlarge_size)),
                Station("松江南京","Songjiang Nanjing","G15",PointF(1100f*enlarge_size, screenHeight.toFloat() - 1650f*enlarge_size)),
                Station("南京復興","Nanjing Fuxing","G16",PointF(1300f*enlarge_size, screenHeight.toFloat() - 1650f*enlarge_size)),
                Station("台北小巨蛋","Taipei Arena","G17",PointF(1500f*enlarge_size, screenHeight.toFloat() - 1650f*enlarge_size)),
                Station("南京三民","Nanjing Sanmin","G18",PointF(1700f*enlarge_size, screenHeight.toFloat() - 1650f*enlarge_size)),
                Station("松山","Songshan","G19",PointF(1900f*enlarge_size, screenHeight.toFloat() - 1650f*enlarge_size)),


                )
        )
        green_2_LineStations.addAll(
            listOf(
                Station("小碧潭","Xiaobitan","G03A",PointF(1450f*enlarge_size, screenHeight.toFloat() - 350f*enlarge_size)),
                Station("七張","Qizhang","G03_1",PointF(1600f*enlarge_size, screenHeight.toFloat() -350f*enlarge_size)),
            )
        )
        brownLineStations.addAll(
            listOf(
                Station("動物園","Taipei Zoo","BR01",PointF(2050f*enlarge_size, screenHeight.toFloat() - 300f*enlarge_size)),
                Station("木柵","Muzha","BR02",PointF(1900f*enlarge_size, screenHeight.toFloat() - 300f*enlarge_size)),
                Station("萬芳社區","Wanfang Community","BR03",PointF(1750f*enlarge_size, screenHeight.toFloat() - 400f*enlarge_size)),
                Station("萬芳醫院","Wanfang Hospital","BR04",PointF(1750f*enlarge_size, screenHeight.toFloat() - 650f*enlarge_size)),
                Station("辛亥","Xinhai","BR05",PointF(1750f*enlarge_size, screenHeight.toFloat() - 800f*enlarge_size)),
                Station("麟光","Linguang","BR06",PointF(1700f*enlarge_size, screenHeight.toFloat() - 900f*enlarge_size)),
                Station("六張犁","Liuzhangli","BR07",PointF(1500f*enlarge_size, screenHeight.toFloat() - 900f*enlarge_size)),
                Station("科技大樓","Technology Building","BR08",PointF(1300f*enlarge_size, screenHeight.toFloat() - 900f*enlarge_size)),

                Station("中山國中","Zhongshan Junior High School","BR12",PointF(1300f*enlarge_size, screenHeight.toFloat() - 1800f*enlarge_size)),
                Station("松山機場","Songshan Airport","BR13",PointF(1300f*enlarge_size, screenHeight.toFloat() - 1950f*enlarge_size)),
                Station("大直","Dazhi","BR14",PointF(1300f*enlarge_size, screenHeight.toFloat() - 2100f*enlarge_size)),
                Station("劍南路","Jiannan Rd.","BR15",PointF(1300f*enlarge_size, screenHeight.toFloat() - 2250f*enlarge_size)),
                Station("西湖","Xihu","BR16",PointF(1500f*enlarge_size, screenHeight.toFloat() - 2250f*enlarge_size)),
                Station("港墘","Gangqian","BR17",PointF(1650f*enlarge_size, screenHeight.toFloat() - 2250f*enlarge_size)),
                Station("文德","Wende","BR18",PointF(1850f*enlarge_size, screenHeight.toFloat() - 2250f*enlarge_size)),
                Station("內湖","Neihu","BR19",PointF(2000f*enlarge_size, screenHeight.toFloat() - 2250f*enlarge_size)),
                Station("大湖公園","Dahu Park","BR20",PointF(2200f*enlarge_size, screenHeight.toFloat() - 2250f*enlarge_size)),
                Station("葫洲","Huzhou","BR21",PointF(2300f*enlarge_size, screenHeight.toFloat() - 2250f*enlarge_size)),
                Station("東湖","Donghu","BR22",PointF(2300f*enlarge_size, screenHeight.toFloat() - 2100f*enlarge_size)),
                Station("南港軟體園區","Nangang Software Park","BR23",PointF(2300f*enlarge_size, screenHeight.toFloat() - 1950f*enlarge_size)),
                Station("南港展覽館","Taipei Nangang Exhibition Center","BR24",PointF(2300f*enlarge_size, screenHeight.toFloat() - 1850f*enlarge_size)),
//                Station("南港展覽館", "Taipei Nangang Exhibition Center", "BL23", PointF(2300f*enlarge_size, screenHeight.toFloat() - 1850f*enlarge_size))
            )
        )
        orangeLineStations.addAll(
            listOf(
                Station("南勢角","Nanshijiao","O01",PointF(800f*enlarge_size, screenHeight.toFloat() - 350f*enlarge_size)),
                Station("景安","Jingan","O02",PointF(800f*enlarge_size, screenHeight.toFloat() - 600f*enlarge_size)),
                Station("永安市場","Yongan Market","O03",PointF(800f*enlarge_size, screenHeight.toFloat() - 750f*enlarge_size)),
                Station("頂溪","Dingxi","O04",PointF(800f*enlarge_size, screenHeight.toFloat() - 900f*enlarge_size)),

                Station("古亭","Guting","O05",PointF(850f*enlarge_size, screenHeight.toFloat() - 1000f*enlarge_size)),

                Station("東門","Dongmen","O06",PointF(1100f*enlarge_size, screenHeight.toFloat() - 1100f*enlarge_size)),




                Station("行天宮","Xingtian Temple","O09",PointF(1100f*enlarge_size, screenHeight.toFloat() - 1800f*enlarge_size)),
                Station("中山國小","Zhongshan Elementary School","O10",PointF(950f*enlarge_size, screenHeight.toFloat() - 1950f*enlarge_size)),


                Station("大橋頭","Daqiaotou","O12",PointF(550f*enlarge_size, screenHeight.toFloat() - 1950f*enlarge_size)),
                Station("台北橋","Taipei Bridge","O13",PointF(400f*enlarge_size, screenHeight.toFloat() - 1800f*enlarge_size)),
                Station("菜寮","Cailiao ","O14",PointF(250f*enlarge_size, screenHeight.toFloat() - 1650f*enlarge_size)),
                Station("三重","Sanchong","O15",PointF(100f*enlarge_size, screenHeight.toFloat() - 1500f*enlarge_size)),
                Station("先嗇宮","Xianse Temple","O16",PointF(-50f*enlarge_size, screenHeight.toFloat() - 1350f*enlarge_size)),
                Station("頭前庄","Touqianzhuang ","O17",PointF(-200f*enlarge_size, screenHeight.toFloat() - 1200f*enlarge_size)),
                Station("新莊","Xinzhuang","O18",PointF(-350f*enlarge_size, screenHeight.toFloat() - 1050f*enlarge_size)),

                Station("輔大","Fu Jen University","O19",PointF(-350f*enlarge_size, screenHeight.toFloat() - 900f*enlarge_size)),
                Station("丹鳳","Danfeng","O20",PointF(-350f*enlarge_size, screenHeight.toFloat() - 750f*enlarge_size)),
                Station("迴龍","Huilong ","O21",PointF(-350f*enlarge_size, screenHeight.toFloat() - 600f*enlarge_size)),


                )

        )
        orange_2_LineStations.addAll(
            listOf(
                Station("大橋頭","Daqiaotou","O12_1",PointF(550f*enlarge_size, screenHeight.toFloat() - 1950f*enlarge_size)),
                Station("三重國小","Sanchong Elementary School","O50",PointF(450f*enlarge_size, screenHeight.toFloat() - 2100f*enlarge_size)),
                Station("三和國中","Sanhe Junior High School","O51",PointF(350f*enlarge_size, screenHeight.toFloat() - 2200f*enlarge_size)),
                Station("徐匯中學","St. Ignatius High School","O52",PointF(250f*enlarge_size, screenHeight.toFloat() - 2300f*enlarge_size)),
                Station("三民高中","Sanmin Senior High School","O53",PointF(150f*enlarge_size, screenHeight.toFloat() - 2400f*enlarge_size)),
                Station("蘆洲","Luzhou","O54",PointF(50f*enlarge_size, screenHeight.toFloat() - 2500f*enlarge_size)),

                )
        )
        yellowLineStations.addAll(
            listOf(
                Station("大坪林","Dapinglin","Y07",PointF(1600f*enlarge_size, screenHeight.toFloat() - 500f*enlarge_size)),


                Station("十四張","Shisizhang","Y08",PointF(1400f*enlarge_size, screenHeight.toFloat() - 500f*enlarge_size)),
                Station("秀朗橋","Xiulang Bridge","Y09",PointF(1200f*enlarge_size, screenHeight.toFloat() - 600f*enlarge_size)),
                Station("景平","Jingping","Y10",PointF(1000f*enlarge_size, screenHeight.toFloat() - 600f*enlarge_size)),

                Station("景安","Jingan","Y11_1",PointF(800f*enlarge_size, screenHeight.toFloat() - 600f*enlarge_size)),

                Station("中和","Zhonghe","Y12",PointF(650f*enlarge_size, screenHeight.toFloat() - 600f*enlarge_size)),
                Station("橋和","Qiaohe","Y13",PointF(570f*enlarge_size, screenHeight.toFloat() - 700f*enlarge_size)),
                Station("中原","Zhongyuan","Y14",PointF(450f*enlarge_size, screenHeight.toFloat() - 750f*enlarge_size)),
                Station("板新","Banxin","Y15",PointF(350f*enlarge_size, screenHeight.toFloat() - 800f*enlarge_size)),

                Station("板橋(環狀)", "Banqiao", "Y16", PointF(280f*enlarge_size, screenHeight.toFloat() - 950f*enlarge_size)),

                Station("新埔民生","Xinpu Minsheng","Y17",PointF(-50f*enlarge_size, screenHeight.toFloat() - 1100f*enlarge_size)),

                Station("頭前庄","Touqianzhuang","Y18_1",PointF(-200f*enlarge_size, screenHeight.toFloat() - 1200f*enlarge_size)),

                Station("幸福","Xingfu","Y19",PointF(-200f*enlarge_size, screenHeight.toFloat() - 1450f*enlarge_size)),
                Station("新北產業園區","New Taipei Industrial Park","Y20",PointF(-200f*enlarge_size, screenHeight.toFloat() - 1600f*enlarge_size)),

                )
        )
        // Add other line stations similarly
    }

}

