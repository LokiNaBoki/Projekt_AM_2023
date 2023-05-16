package com.example.projekt_am_2023

import java.io.Serializable

data class Tag(
    var name: String="",
    var color: Int=0,
    var databaseId: String?=null
) : Serializable
