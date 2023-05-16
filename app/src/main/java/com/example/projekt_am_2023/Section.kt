package com.example.projekt_am_2023

import com.google.firebase.database.IgnoreExtraProperties

data class Section(
    var name: String = "",
    var tasks: MutableList<Task> = mutableListOf(),
    var databaseId: String? = null
    )