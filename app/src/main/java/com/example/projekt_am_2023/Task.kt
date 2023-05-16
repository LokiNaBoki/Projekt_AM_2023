package com.example.projekt_am_2023

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@IgnoreExtraProperties
data class Task(
    var title: String="",
    var done: Boolean=false,
    var assignee: User?=null,
    var startCalendar: Calendar?=null,
    var endCalendar: Calendar?=null,
    var Description: String?=null,
    var subtasks: MutableList<Task> = mutableListOf(),
    var tags: MutableList<Tag> = mutableListOf(),
    var databaseId: String?=null
) : Serializable {
    companion object {
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
