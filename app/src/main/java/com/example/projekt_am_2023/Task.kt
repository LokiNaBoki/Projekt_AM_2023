package com.example.projekt_am_2023

import android.util.Log
import com.google.firebase.database.DataSnapshot
import android.os.Bundle
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
    var section: Section? = null,
    var subtasksHashes: MutableList<String> = mutableListOf(),
    var databaseId: String?=null
) : Serializable {
    companion object {
        private var locale: Locale = Locale.getDefault()
        private val dateSDF = SimpleDateFormat("yyyy-MM-dd", locale)
        private val timeSDF = SimpleDateFormat("HH:mm", locale)

        @JvmStatic
        fun emptyTask() = Task("", false, null, null, null, null, mutableListOf(), mutableListOf())

//        fun loadDatabaseArray(dataSnapshot: DataSnapshot) : MutableList<Task>{
//            var tasks = mutableListOf<Task>()
//            for (d in dataSnapshot.children) {
//                tasks.add(loadDatabase(d))
//            }
//            return tasks
//        }

        fun loadDatabaseMap(dataSnapshot: DataSnapshot, tags:HashMap<String,Tag>, users:HashMap<String,User>, sections:HashMap<String,Section>): HashMap<String,Task> {
            var elements = HashMap<String,Task>()
            for (d in dataSnapshot.children){
                val element = loadDatabase(d,tags,users,sections)
                elements[element.databaseId!!] = element
            }
            return elements
        }

        fun loadDatabase(dataSnapshot: DataSnapshot, tags:HashMap<String,Tag> , users:HashMap<String,User>, sections:HashMap<String,Section>) : Task{
//            Log.i("Firebase",""+dataSnapshot)
            var task : Task = Task()
            task.databaseId = dataSnapshot.key
            task.title = dataSnapshot.child("title").value as String
            task.done = dataSnapshot.child("done").value as Boolean
            task.assignee = users[dataSnapshot.child("assignee").value]
            task.startCalendar = run{
                var time = dataSnapshot.child("startCalendar").value as Long?
                var cal : Calendar? = null
                if(time != null){
                    cal = Calendar.getInstance()
                    cal.time.time = time
                }
                cal
            }

            task.endCalendar = run{
                var time = dataSnapshot.child("endCalendar").value as Long?
                var cal : Calendar? = null
                if(time != null){
                    cal = Calendar.getInstance()
                    cal!!.time.time = time
                }
                cal
            }

            task.Description = dataSnapshot.child("Description").value as String?
            task.section = sections[dataSnapshot.child("section").value]

            for(t in dataSnapshot.child("subtasks").children){
                task.subtasksHashes.add(t.key!!)
            }

            for(t in dataSnapshot.child("tags").children){
                if(tags.containsKey(t.key)){
                    task.tags.add(tags[t.key]!!)
                }
            }
            return task
        }

        fun assignSubtasks(tasksMap: HashMap<String, Task>) {
            for(t in tasksMap){
                for(sub in t.value.subtasksHashes){
                    if(tasksMap.containsKey(sub)){
                        t.value.subtasks.add(tasksMap[sub]!!)
                    }
                }
            }
        }
    }

    fun saveDatabase(){
        var key : String?
        if(this.databaseId == null){
            key = DatabaseLoader.tasks.push().key
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

        if(this.assignee != null && this.assignee!!.databaseId == null){
            Log.w("Firebase","User not in database.")
            return
        }

        if(this.section != null && this.section!!.databaseId == null){
            Log.w("Firebase","Section not in database.")
            return
        }


        val postValues = hashMapOf<String, Any?>(
            "title" to this.title,
            "done" to this.done,
            "assignee" to this.assignee?.databaseId,
            "startCalendar" to (this.startCalendar?.time?.time),
            "endCalendar" to (this.endCalendar?.time?.time),
            "description" to this.Description,
            "section" to this.section?.databaseId,
            "subtasks" to subtasks,
            "tags" to tags
        )

        val childUpdates = hashMapOf<String, Any>(
            "/tasks/$key" to postValues,
//                "/sections/$sectionId/$key" to true,
        )
        this.databaseId = key
        DatabaseLoader.dataref.updateChildren(childUpdates)
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
