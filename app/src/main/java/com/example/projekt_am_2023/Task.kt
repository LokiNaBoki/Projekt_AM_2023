package com.example.projekt_am_2023

import java.util.Calendar

data class Task(
    var title: String,
    var done: Boolean,
    var assignee: User?,
    var startCalendar: Calendar?,
    var endCalendar: Calendar?,
    var Description: String?,
    var subtasks: MutableList<Task>,
    var tags: MutableList<Tag>,
)