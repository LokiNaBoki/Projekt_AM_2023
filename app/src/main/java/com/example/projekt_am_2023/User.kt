package com.example.projekt_am_2023

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import java.io.Serializable

data class User(
    @PrimaryKey(autoGenerate = true) var id:Int = 0,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "avatar") var avatar: Int?
    ) : Serializable
