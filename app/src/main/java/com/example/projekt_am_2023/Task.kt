package com.example.projekt_am_2023

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true) var id:Int = 0,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "done") var done: Boolean,
    @ColumnInfo(name = "assignee") var assignee: User?,
    @ColumnInfo(name = "start_calendar") var startCalendar: Calendar?,
    @ColumnInfo(name = "end_calendar") var endCalendar: Calendar?,
    @ColumnInfo(name = "description") var Description: String?,
    @Embedded var subtasks: MutableList<Task>,
    @Embedded var tags: MutableList<Tag>,
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
