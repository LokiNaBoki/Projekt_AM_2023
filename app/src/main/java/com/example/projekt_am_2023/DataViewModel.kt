package com.example.projekt_am_2023

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Calendar

class DataViewModel {
    companion object{
        val database = run{
            val database = FirebaseDatabase.getInstance("https://tasks-b3e1f-default-rtdb.europe-west1.firebasedatabase.app")
            database.setPersistenceEnabled(true)
            database
        }
        val sections = database.getReference("sections")
        val tasks = database.getReference("tasks")
        val users = database.getReference("users")
        val tags = database.getReference("tags")
        val dataref = database.reference
    }
}