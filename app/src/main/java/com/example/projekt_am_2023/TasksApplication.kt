package com.example.projekt_am_2023

import android.app.Application

class TasksApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}
