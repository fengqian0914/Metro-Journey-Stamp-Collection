package com.example.MRTAPP.UI

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import cn.iwgang.countdownview.CountdownView
import com.example.MRTAPP.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var countdownText: TextView

private val handler = Handler(Looper.getMainLooper())

/**
 * A simple [Fragment] subclass.
 * Use the [Star_Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Star_Fragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_star_, container, false)
        val task_btn = view.findViewById<Button>(R.id.Task_btn)
        val Achievement_btn = view.findViewById<Button>(R.id.Achievement_btn)
        val gobacklayout_1=view.findViewById<LinearLayout>(R.id.goback_layout)
        val gobacklayout_2=view.findViewById<LinearLayout>(R.id.goback_layout_2)

        val Task_layout=view.findViewById<ScrollView>(R.id.scrollview_task)
        val Achievement_layout=view.findViewById<ScrollView>(R.id.scrollview_Achievement)

        val menu_layout=view.findViewById<LinearLayout>(R.id.star_menu)



        val c: Calendar = Calendar.getInstance()
//        c[Calendar.MONTH] = Calendar.FEBRUARY

        val mcounddown_today=view.findViewById<CountdownView>(R.id.timers_today)

        val mcoundown_toweek=view.findViewById<CountdownView>(R.id.timers_toweek)
        val mcoundown_toweek2=view.findViewById<CountdownView>(R.id.timers_toweek2)
        val mcoundown_toweek3=view.findViewById<CountdownView>(R.id.timers_toweek3)

        val mcoundown_tomonth=view.findViewById<CountdownView>(R.id.timers_tomonth)
        val mcoundown_tomonth2=view.findViewById<CountdownView>(R.id.timers_tomonth2)
        val mcoundown_tomonth3=view.findViewById<CountdownView>(R.id.timers_tomonth3)
        val mcoundown_tomonth4=view.findViewById<CountdownView>(R.id.timers_tomonth4)
        val mcoundown_tomonth5=view.findViewById<CountdownView>(R.id.timers_tomonth5)


        val aDayInMilliSecond = (60 * 60 * 24 * 1000).toLong() //一天的毫秒數

        val today_time=Today(c,aDayInMilliSecond)//呼叫函式 計算本日剩餘時間
        val toweek_time=ToWeek(c,aDayInMilliSecond,today_time) //呼叫函式 計算本週剩餘天數
        val tomonth_time=ToMonth(c,aDayInMilliSecond,today_time)//呼叫函式 計算本月剩餘天數

        mcounddown_today.start(today_time.toLong())
        mcoundown_toweek.start(toweek_time.toLong())
        mcoundown_toweek2.start(toweek_time.toLong())
        mcoundown_toweek3.start(toweek_time.toLong())

        mcoundown_tomonth.start(tomonth_time.toLong())
        mcoundown_tomonth2.start(tomonth_time.toLong())
        mcoundown_tomonth3.start(tomonth_time.toLong())
        mcoundown_tomonth4.start(tomonth_time.toLong())
        mcoundown_tomonth5.start(tomonth_time.toLong())






        task_btn.setOnClickListener{
            Task_layout.setVisibility(View.VISIBLE)
            menu_layout.setVisibility(View.GONE)
            Achievement_layout.setVisibility(View.GONE)
        }
        Achievement_btn.setOnClickListener{
            Task_layout.setVisibility(View.GONE)
            menu_layout.setVisibility(View.GONE)
            Achievement_layout.setVisibility(View.VISIBLE)
        }
        gobacklayout_1.setOnClickListener{
            Task_layout.setVisibility(View.GONE)
            menu_layout.setVisibility(View.VISIBLE)
            Achievement_layout.setVisibility(View.GONE)
        }
        gobacklayout_2.setOnClickListener{
            Task_layout.setVisibility(View.GONE)
            menu_layout.setVisibility(View.VISIBLE)
            Achievement_layout.setVisibility(View.GONE)
        }
        return view  //回傳view
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Star_Fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Star_Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    private fun Today(c:Calendar,aDayInMilliSecond: Long):Long{
        val sdf_hour = SimpleDateFormat("HH")
        val sdf_minute = SimpleDateFormat("m")
        val sdf_Second = SimpleDateFormat("s")
        val timeStamp = System.currentTimeMillis()




        sdf_hour.setTimeZone(TimeZone.getTimeZone("GMT+8") );
        val Snum_hour: String = sdf_hour.format(Date(java.lang.String.valueOf(timeStamp).toLong()))
        val Snum_Minute: String = sdf_minute.format(Date(java.lang.String.valueOf(timeStamp).toLong()))
        val Snum_Second: String = sdf_Second.format(Date(java.lang.String.valueOf(timeStamp).toLong()))
        val num_hour:Int=Snum_hour.toInt()*60*60*1000
        val num_minute:Int=Snum_Minute.toInt()*60*1000
        val num_second:Int=Snum_Second.toInt()*1000
        val today_time =aDayInMilliSecond-(num_hour+num_minute+num_second)
        return today_time
    }
    fun ToWeek(c:Calendar,aDayInMilliSecond:Long,today_time:Long):Long{
        var weekday: Int = c.get(Calendar.DAY_OF_WEEK)
        if(weekday==1){
            weekday=8
        }
        Log.d("debug_time","星期："+(weekday-1).toString())
        val toweek_time=today_time+aDayInMilliSecond*(7-weekday+1)
        return toweek_time
    }
    private fun ToMonth(c:Calendar,aDayInMilliSecond:Long,today_time:Long):Long{
        val year = c[Calendar.YEAR] //取出年
        val c1 = Calendar.getInstance()
        val c2 = Calendar.getInstance()

        val day = c1.get(Calendar.DAY_OF_MONTH)
        //day=30;
        val month = c1.get(Calendar.MONTH) ; //取出月，月份的編號是由0~11 故+1
        val date_one=1;
        c1.set(year, month, day);
        c2.set(year, month+1, date_one);

        Log.d("debug_time","month:"+month.toString())
        Log.d("debug_time","c1:"+year.toString()+"年"+month.toString()+"月"+day.toString()+"日");
        Log.d("debug_time","c2:"+year.toString()+"年"+(month+1).toString()+"月"+date_one.toString()+"日")
        Log.d("debug_time","c1:"+c1.timeInMillis.toString()+"   c2:   "+c2.timeInMillis.toString());

        val dayDiff = (c2.timeInMillis - c1.timeInMillis) / aDayInMilliSecond

        val tomonth_time=today_time+aDayInMilliSecond*(dayDiff)

        return tomonth_time
    }

}