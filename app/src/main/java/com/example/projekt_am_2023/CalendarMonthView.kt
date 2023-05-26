package com.example.projekt_am_2023

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.fragment.app.Fragment
import java.util.Calendar

class CalendarMonthView: Fragment() {
    private lateinit var fragment: ListView

    inner class ChooseDateListener: CalendarView.OnDateChangeListener {
        override fun onSelectedDayChange(view: CalendarView, year: Int, month: Int, day: Int) {
            val calendar: Calendar = Calendar.getInstance()
            calendar.set(year, month, day)
            fragment.changeDate(calendar)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_calendar_month_view, container, false)
        val chooseDateListener = ChooseDateListener()
        view.findViewById<CalendarView>(R.id.calendarView).setOnDateChangeListener(chooseDateListener)
        fragment = ListView.newInstance(Calendar.getInstance())
        childFragmentManager.beginTransaction().replace(R.id.childFragmentContainer, fragment).commit()
        return view
    }

    companion object {
        fun newInstance(): CalendarMonthView {
            return CalendarMonthView()
        }
    }
}