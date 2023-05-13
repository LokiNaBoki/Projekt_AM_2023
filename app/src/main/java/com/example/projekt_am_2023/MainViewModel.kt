package com.example.projekt_am_2023

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainViewModel(private val mainDao : MainDao) : ViewModel(){
    fun allTasks(): List<Task> = mainDao.getAll()

//    fun scheduleForStopName(name: String): List<Task> = mainDao.getByStopName(name)

}


class BusScheduleViewModelFactory(
    private val mainDao: MainDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(mainDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
