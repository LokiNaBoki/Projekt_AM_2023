package com.example.projekt_am_2023

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class Task(
    var title: String,
    var done: Boolean,
    var assignee: User?,
    var startCalendar: Calendar?,
    var endCalendar: Calendar?,
    var Description: String?,
    var subtasks: MutableList<Task>,
    var tags: MutableList<Tag>,
) : Serializable {
    private companion object {
        private var locale: Locale = Locale.getDefault()
        private val dateSDF = SimpleDateFormat("yyyy-MM-dd", locale)
        private val timeSDF = SimpleDateFormat("HH:mm", locale)

        @JvmStatic
        fun emptyTask() = Task("", false, null, null, null, null, mutableListOf(), mutableListOf())
    }

    private fun getWeekDay(cal: Calendar?): String {
        return if(cal != null) {
            cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, locale)!!
        } else {
            ""
        }
    }

    fun getStartWeekDay(): String {
        return getWeekDay(startCalendar)
    }

    fun getEndWeekDay(): String {
        return getWeekDay(endCalendar)
    }

    private fun getDate(cal: Calendar?): String {
        return if(cal != null) {
            dateSDF.format(cal.time)
        } else {
            ""
        }
    }

    fun getStartDate(): String {
        return getDate(startCalendar)
    }

    fun getEndDate(): String {
        return getDate(endCalendar)
    }

    private fun getTime(cal: Calendar?): String {
        return if(cal != null) {
            timeSDF.format(cal.time)
        } else {
            ""
        }
    }

    fun getStartTime(): String {
        return getTime(startCalendar)
    }

    fun getEndTime(): String {
        return getTime(endCalendar)
    }
}
