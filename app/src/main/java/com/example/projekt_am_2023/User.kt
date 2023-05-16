package com.example.projekt_am_2023

import java.io.Serializable

data class User(
    var name: String="",
    var avatar: Int?=null,
    var databaseId: String?=null
) : Serializable
