package com.example.projekt_am_2023

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale

class CalendarWeekView : Fragment() {

    //numeric consts
    private val daysRange = 0..6
    private val hoursRange = 6..20
    private val minutesInHour = 60
    private lateinit var sections : MutableList<Section>

    lateinit var hoursView : LinearLayout
    lateinit var monthYearView : TextView
    lateinit var daysView : LinearLayout
    lateinit var eventsView : LinearLayout

    lateinit var weekStart : Calendar
    lateinit var weekEnd  : Calendar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_calendar_week_view, container, false)

        hoursView = view.findViewById(R.id.hours)
        monthYearView = view.findViewById(R.id.monthYear)
        daysView = view.findViewById(R.id.days) //layout of day names
        eventsView = view.findViewById(R.id.events) //layout of layouts for each day
        setWeek(Calendar.getInstance())



        //get task list
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                sections = DatabaseLoader.loadDatabase(dataSnapshot)
                generateView(getWeekTasks())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("Firebase", "loadPost:onCancelled", databaseError.toException())
            }
        }
        DatabaseLoader.dataref.addValueEventListener(postListener)
        return view
    }

    private fun getWeekTasks(): List<Task> {
        var tasks = mutableListOf<Task>()
        for (s in sections) {
            for (t in s.tasks) {
                if (t.startCalendar != null && t.endCalendar != null) {
                    if (t.startCalendar!! < weekEnd && t.endCalendar!! > weekStart) {
                        tasks.add(t)
                    }
                }
            }
        }
        return tasks
    }


    fun setWeek(calendar : Calendar){
        weekStart = calendar.clone() as Calendar
        trimToDay(weekStart)
        weekStart.set(Calendar.DAY_OF_WEEK, weekStart.firstDayOfWeek)
        weekEnd  = weekStart.clone() as Calendar
        weekStart.add(Calendar.DAY_OF_YEAR, daysRange.first )
        weekEnd.add(Calendar.DAY_OF_YEAR, daysRange.last )
    }


    fun generateView(tasks : List<Task>){

        val currentDate = Calendar.getInstance() // should be date in browsed week

        //assigns hours to

        for (h in hoursRange) {
            var text = TextView(activity)
            val param = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1.0f
            )
            text.layoutParams = param
            text.text = "$h"//could be better?
            hoursView.addView(text)
        }

        //sets title date
        monthYearView.text = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(currentDate.time)

        //creates columns for every day

        var eventLists = mutableListOf<LinearLayout>() //layout of events in day
        for (day in daysRange) {
            var textView = TextView(activity)
            var param = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1.0f
            )
            textView.layoutParams = param
            textView.text = DayOfWeek.values()[day].getDisplayName(TextStyle.NARROW, Locale.getDefault())
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            daysView.addView(textView)

            eventLists.add(LinearLayout(activity))
            eventLists.last().orientation = LinearLayout.VERTICAL
            param = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1.0f
            )
            eventLists.last().layoutParams = param
            eventsView.addView(eventLists.last())
        }


        var instanceType : Boolean //true - event, false - space
        var instanceStart : Calendar
        var instanceEnd : Calendar
        var spaceStart : Int
        var spaceEnd : Int
        var todayStart : Calendar
        var todayEnd : Calendar



//        var tomorrow = today.clone() as Calendar
//        tomorrow.add(Calendar.DAY_OF_YEAR, 1)

        val firstMinute = minutesInHour * hoursRange.first
        val lastMinute = minutesInHour * hoursRange.last

        todayStart = weekStart.clone() as Calendar
        todayEnd = weekStart.clone() as Calendar
        todayStart.set(Calendar.HOUR_OF_DAY, hoursRange.first)
        todayEnd.set(Calendar.HOUR_OF_DAY, hoursRange.last)

        if(tasks[0].startCalendar!!<weekStart){
            instanceType = true
            instanceStart = tasks[0].startCalendar!!
            instanceEnd = tasks[0].endCalendar!!
        }else{
            instanceType = false
            instanceStart = weekStart
            instanceEnd = tasks[0].startCalendar!!
        }

        //adds events
        var day = daysRange.first
        var t = 0
        while(t<tasks.size && day<=daysRange.last) {
            if (instanceStart < todayStart) {
                spaceStart = firstMinute
            } else {
                spaceStart = minuteOfDay(instanceStart)
            }

            if (instanceEnd > todayEnd) {
                spaceEnd = lastMinute
            } else {
                spaceEnd = minuteOfDay(instanceStart)
            }
            var space = spaceEnd - spaceStart
            var view: View
            if (space > 0) {
                if (instanceType) {
                    view = layoutInflater.inflate(R.layout.week_view_event, eventLists[day], false)
                   monthYearView.text = tasks[t].title
                } else {
                    view = Space(activity)
                }
                view.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    space.toFloat()
                )
                eventLists[day].addView(view)
            }

            if (instanceEnd > todayEnd) {
                day++
                todayStart.add(Calendar.DAY_OF_YEAR, 1)
                todayEnd.add(Calendar.DAY_OF_YEAR, 1)
            } else {
                if (!instanceType) {
                    t++
                    if(t>=tasks.size){
                        continue
                    }
                    instanceType = true
                    instanceStart = tasks[t].startCalendar!!
                    instanceEnd = tasks[t].endCalendar!!
                } else {
                    instanceType = false
                    instanceStart = tasks[t].endCalendar!!
                    if(t+1<tasks.size){
                        instanceEnd = tasks[t + 1].startCalendar!!
                    }else{
                        instanceEnd = todayEnd
                    }

                }
            }
        }
    }

    fun addSpace(layout: LinearLayout, weight : Float){
        var space = Space(activity)
        space.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT,
            weight
        )
        layout.addView(space)
    }

    fun minuteOfDay(cal : Calendar) : Int{
        return cal.get(Calendar.HOUR_OF_DAY)*60 + cal.get(Calendar.MINUTE)
    }

    fun trimToDay(cal : Calendar){
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
    }

    companion object {
        fun newInstance(): CalendarWeekView {
            return CalendarWeekView()
        }
    }
}