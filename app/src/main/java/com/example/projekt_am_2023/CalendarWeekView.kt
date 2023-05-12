package com.example.projekt_am_2023

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale


class CalendarWeekView : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //numeric consts
        val daysRange = 0..6
        val hoursRange = 6..20
        val minutesInHour = 60

        setContentView(R.layout.activity_calendar_week_view)

        //get task list
        var sections: MutableList<Section>
        sections = mutableListOf<Section>()
        sections = genTasks()

        var tasks = mutableListOf<Task>()
        for (s in sections) {
            tasks.addAll(s.tasks)
        }

        for (t in tasks) {
            Log.println(Log.ERROR, "loop_exec", "Task: ${t.startCalendar!!.time}")
            if (t.startCalendar == null) {
                return
            }
            if (t.endCalendar == null) {
                return
            }
        }
        //after this moment tasks should be only from specified week, sorted and shouldn't collide (should be for one user?)

        val currentDate = Calendar.getInstance() // should be date in browsed week

        //assigns hours to
        val hoursView: LinearLayout = findViewById(R.id.hours)
        for (h in hoursRange) {
            var text = TextView(this)
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
        findViewById<TextView>(R.id.monthYear).text =
            SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(currentDate.time)

        //creates columns for every day
        var daysView: LinearLayout = findViewById(R.id.days) //layout of day names
        var eventsView: LinearLayout = findViewById(R.id.events) //layout of layouts for each day
        var eventLists = mutableListOf<LinearLayout>() //layout of events in day
        for (day in daysRange) {
            var textView = TextView(this)
            var param = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1.0f
            )
            textView.layoutParams = param
            textView.text =
                DayOfWeek.values()[day].getDisplayName(TextStyle.NARROW, Locale.getDefault())
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            daysView.addView(textView)

            eventLists.add(LinearLayout(this))
            eventLists.last().orientation = LinearLayout.VERTICAL
            param = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1.0f
            )
            eventLists.last().layoutParams = param
            eventsView.addView(eventLists.last())
        }


//        var today = currentDate.clone() as Calendar
//        trimToDay(today)
//        today.set(Calendar.DAY_OF_WEEK, today.firstDayOfWeek)
//
//        var tomorrow = today.clone() as Calendar
//        tomorrow.add(Calendar.DAY_OF_YEAR, 1)
//
//        val firstMinute = minutesInHour * hoursRange.first
//        val lastMinute = minutesInHour * hoursRange.last
//
//        //adds events
//        var day = daysRange.first
//        var t = 0
////        var keepTask = false
//        var addDay = false
//        while (t < tasks.size && day <= daysRange.last) {
//            Log.println(Log.ERROR, "loop_exec", "Task: $t Day:$day")
//            Log.println(
//                Log.ERROR,
//                "loop_exec",
//                "Task: ${tasks[t].startCalendar!!.time} Tomorrow: ${tomorrow.time}"
//            )
//            var time: Int
//            var spaceTime = 0
//
//            if (tasks[t].startCalendar!! > today) {
//                if (tasks[t].startCalendar!! > tomorrow) {
//                    day++
//                    today = tomorrow.clone() as Calendar
//                    tomorrow.add(Calendar.DAY_OF_YEAR, 1)
//                    continue
//                }
//                time = minuteOfDay(tasks[t].startCalendar!!)
//                if (t > 0) {
//                    if (tasks[t - 1].endCalendar!! > today) {
//                        spaceTime = time - minuteOfDay(tasks[t - 1].endCalendar!!)
//                    } else {
//                        if (tasks[t - 1].endCalendar!! < today) {
//                            addSpace(
//                                eventLists[day - 1],
//                                (minuteOfDay(tasks[t - 1].endCalendar!!) - lastMinuteOfDay).toFloat()
//                            )
//                        }
//                        spaceTime = time
//                    }
//                } else {
//                    spaceTime = time
//                }
//            } else {
//                time = firstMinute
//            }
//
//            if (tasks[t].endCalendar!! > today) {
//                time = lastMinute - time
//                keepTask = true
//                addDay = true
//            } else {
//                time = minuteOfDay(tasks[t].endCalendar!!) - time
//            }
//
//            if (spaceTime > 0) {
//                addSpace(eventLists[day], spaceTime.toFloat())
//            }
//
//            var view = layoutInflater.inflate(R.layout.week_view_event, eventLists[day], false)
//            view.layoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                time.toFloat()
//            )
//            view.findViewById<TextView>(R.id.monthYear).text = tasks[t].title
//            eventLists[day].addView(view)
//
//
//
//            if (keepTask) {
//                keepTask = false
//            } else {
//                t++
//            }
//            if (addDay) {
//                addDay = false
//                day++
//                today = tomorrow.clone() as Calendar
//                tomorrow.add(Calendar.DAY_OF_YEAR, 1)
//            }
//        }
//
//        if (tasks.last().endCalendar!! < today) {
//            addSpace(
//                eventLists[day - 1],
//                (minuteOfDay(tasks.last().endCalendar!!) - lastMinuteOfDay).toFloat()
//            )
//        }
//
//        var spaceStart = startMinute
//        var spaceEnd
//
//
//        if(tasks[t].begin.get(Calendar.DAY_OF_WEEK) == day && minuteOfDay(tasks[t].begin)>=firstMinute){
//            spaceStart = minuteOfDay([t].begin)
//        }else{
//            spaceStart = firstMinute
//        }
//
//        if(tasks[t].end.get(Calendar.DAY_OF_WEEK) == day && minuteOfDay(tasks[t].end)<=lastMinute){
//            spaceEnd = minuteOfDay([t].end)
//        }else{
//            spaceEnd = lastMinute
//        }
//
//        createTaskView()
//
//
//        if(todayBegin > tasks[i].beg){
//            spaceStart = firstMinute
//        }else{
//
//        }

        var instanceType : Boolean //true - event, false - space
        var instanceStart : Calendar
        var instanceEnd : Calendar
        var spaceStart : Int
        var spaceEnd : Int
        var todayStart : Calendar
        var todayEnd : Calendar

        var firstDayOfWeek = currentDate.clone() as Calendar
        trimToDay(firstDayOfWeek)
        firstDayOfWeek.set(Calendar.DAY_OF_WEEK, firstDayOfWeek.firstDayOfWeek)

//        var tomorrow = today.clone() as Calendar
//        tomorrow.add(Calendar.DAY_OF_YEAR, 1)

        val firstMinute = minutesInHour * hoursRange.first
        val lastMinute = minutesInHour * hoursRange.last

        todayStart = firstDayOfWeek.clone() as Calendar
        todayEnd = firstDayOfWeek.clone() as Calendar
        todayStart.set(Calendar.HOUR_OF_DAY, hoursRange.first)
        todayEnd.set(Calendar.HOUR_OF_DAY, hoursRange.last)

        if(tasks[0].startCalendar!!<firstDayOfWeek){
            instanceType = true
            instanceStart = tasks[0].startCalendar!!
            instanceEnd = tasks[0].endCalendar!!
        }else{
            instanceType = false
            instanceStart = firstDayOfWeek
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
                if (instanceType == true) {
                    view = layoutInflater.inflate(R.layout.week_view_event, eventLists[day], false)
                    view.findViewById<TextView>(R.id.monthYear).text = tasks[t].title
                } else {
                    view = Space(this)
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
                if (instanceType == false) {
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
        var space = Space(this)
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

    fun genTasks(): MutableList<Section> {
        val user1 = User("User 1", R.drawable.user1)
        val user2 = User("User 2", R.drawable.user2)

        val tag1 = Tag("First", Color.RED)
        val tag2 = Tag("First", Color.BLUE)

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        return mutableListOf<Section>(
            Section(
                "Section 4",
                mutableListOf<Task>(
                    Task(
                        "Task 1.1", false, user1,
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-27 10:10")!! },
                        Calendar.getInstance().apply { time = sdf.parse("2023-05-08 11:11")!! },
                        "Description of task 1.1", mutableListOf(), mutableListOf(tag1)
                    ),
                    Task(
                        "Task 4.2", false, user2,
                        Calendar.getInstance().apply{ time = sdf.parse("2023-05-08 20:00")!! },
                        Calendar.getInstance().apply{ time = sdf.parse("2023-05-08 22:14")!! },
                        null, mutableListOf<Task>(), mutableListOf<Tag>(tag2)
                    ),
                )
            ),
            Section(
                "Section 5",
                mutableListOf<Task>(
                    Task(
                        "Task 5.1", false, null,
                        Calendar.getInstance().apply{ time = sdf.parse("2023-05-09 16:20")!! },
                        Calendar.getInstance().apply{ time = sdf.parse("2023-05-10 12:00")!! },
                        "Description of task 5.1", mutableListOf<Task>(), mutableListOf<Tag>(tag2)
                    ),
                    Task(
                        "Task 5.2", false, user2,
                        Calendar.getInstance().apply{ time = sdf.parse("2023-05-10 12:00")!! },
                        Calendar.getInstance().apply{ time = sdf.parse("2023-05-10 13:00")!! },
                        "Description of task 5.2", mutableListOf<Task>(), mutableListOf<Tag>(tag1)
                    ),
                )
            ),
        );
    }
}