package com.example.projekt_am_2023

import android.util.Log
import com.google.firebase.database.DataSnapshot
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class Task(
    var title: String="",
    var done: Boolean=false,
    var assignee: User?=null,
    var startCalendar: Calendar?=null,
    var endCalendar: Calendar?=null,
    var Description: String?=null,
    var subtasks: MutableList<Task> = mutableListOf(),
    var tags: MutableList<Tag> = mutableListOf(),
    var section: Section?=null,
    var databaseId: String?=null
) : Serializable {
    companion object {
        private var locale: Locale = Locale.getDefault()
        private val dateSDF = SimpleDateFormat("yyyy-MM-dd", locale)
        private val timeSDF = SimpleDateFormat("HH:mm", locale)

        @JvmStatic
        fun emptyTask() = Task("", false, null, null, null, null, mutableListOf(), mutableListOf())

        fun loadDatabaseArray(dataSnapshot: DataSnapshot) : MutableList<Task>{
            var tasks = mutableListOf<Task>()
            for (d in dataSnapshot.children) {
                tasks.add(loadDatabase(d))
            }
            return tasks
        }
        fun loadDatabase(dataSnapshot: DataSnapshot) : Task{
            Log.i("Firebase",""+dataSnapshot)
            var task : Task = Task()
            task.databaseId = dataSnapshot.key
            task.title = dataSnapshot.child("title").value as String
            task.done = dataSnapshot.child("done").value as Boolean
//            task.assignee = User.loadDatabase(dataSnapshot.child("title"))
            DataViewModel.users.child(dataSnapshot.child("assignee").value as String).get().addOnSuccessListener {
                task.assignee =  User.loadDatabase(it)
            }
            //task.assignee = User.loadDatabase(DataViewModel.users.child(dataSnapshot.child("assignee").value as String))
            task.startCalendar = Calendar.getInstance()
            task.startCalendar!!.time.time = dataSnapshot.child("startCalendar").value as Long
            task.endCalendar = Calendar.getInstance()
            task.endCalendar!!.time.time = dataSnapshot.child("endCalendar").value as Long
            task.Description = dataSnapshot.child("Description").value as String?
//            task.subtasks = Task.loadDatabaseArray(dataSnapshot.child("subtasks"))
            for(t in dataSnapshot.child("subtasks").children){
                DataViewModel.tasks.child(t.key!!).get().addOnSuccessListener {
                    task.subtasks.add(Task.loadDatabase(it))
                }
            }
//            task.tags = Tag.loadDatabaseArray(dataSnapshot.child("tags"))
//            for(t in dataSnapshot.child("tags") as List<DataSnapshot>){
//                task.tags.add(Tag.loadDatabase(DataViewModel.tasks.child(t.key!!).get()))
//            }
            for(t in dataSnapshot.child("tags").children){
                DataViewModel.tags.child(t.key!!).get().addOnSuccessListener {
                    task.tags.add(Tag.loadDatabase(it))
                }
            }
            return task
        }
    }

    fun saveDatabase(){
        var key : String?
        if(this.databaseId == null){
            key = DataViewModel.tasks.push().key
        }else{
            key = this.databaseId
        }

        if (key == null) {
            Log.w("Firebase", "Couldn't get push key for posts")
            return
        }

        var subtasks = hashMapOf<String, Boolean>()
        for(t in this.subtasks){
            if(t.databaseId == null){
                Log.w("Firebase","Subtask not in database.")
                return
            }
            subtasks[t.databaseId!!] = true
        }

        var tags = hashMapOf<String, Boolean>()
        for(t in this.tags){
            if(t.databaseId == null){
                Log.w("Firebase","Tag not in database.")
                return
            }
            tags[t.databaseId!!] = true
        }


        val postValues = hashMapOf<String, Any>(
            "title" to this.title,
            "done" to this.done,
            "assignee" to this.assignee?.databaseId!!,
            "startCalendar" to (this.startCalendar?.time?.time!!),
            "endCalendar" to (this.endCalendar?.time?.time!!),
            "description" to (if(this.Description==null)"" else this.Description!!),
            "subtasks" to subtasks,
            "tags" to tags
        )

        val childUpdates = hashMapOf<String, Any>(
            "/tasks/$key" to postValues,
//                "/sections/$sectionId/$key" to true,
        )
        this.databaseId = key
        DataViewModel.dataref.updateChildren(childUpdates)
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
