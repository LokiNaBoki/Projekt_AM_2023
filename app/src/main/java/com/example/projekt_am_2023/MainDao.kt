package com.example.projekt_am_2023

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

@Dao
interface MainDao {
    @Query("SELECT * FROM task")
    fun getAll(): Flow<List<Task>>

//    fun getDay(date : Calendar):List<Task>{
//        val d = date.clone() as Calendar
//        d.add(Calendar.DATE, 1)
//        return getBetween(date.timeInMillis,d.timeInMillis)
//    }

    @Query("SELECT * FROM task WHERE (start_calendar <= :dateE) and (end_calendar >= :dateB)")
    fun getBetween(dateB : Long, dateE:Long): Flow<List<Task>>


    @Query("SELECT * FROM task WHERE id IN (:taskIds)")
    fun loadAllByIds(taskIds: IntArray): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE id = :id")
    fun loadById(id: Int): Flow<Task>

    @Update
    fun update(vararg tasks: Task)

    @Insert
    fun insert(vararg tasks: Task)

    @Delete
    fun delete(tasks: Task)
}
