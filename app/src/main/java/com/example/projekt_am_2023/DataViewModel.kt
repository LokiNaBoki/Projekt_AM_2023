package com.example.projekt_am_2023

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Calendar

class DataViewModel {
    companion object{
        val database = FirebaseDatabase.getInstance("https://tasks-b3e1f-default-rtdb.europe-west1.firebasedatabase.app")
        val sections = database.getReference("sections")
        val tasks = database.getReference("tasks")
        val users = database.getReference("users")
        val tags = database.getReference("tags")
        val dataref = database.reference

        fun save(task:Task){
            var key : String?
            if(task.databaseId == null){
                key = tasks.push().key
            }else{
                key = task.databaseId
            }

            if (key == null) {
                Log.w("Firebase", "Couldn't get push key for posts")
                return
            }

            var subtasks = hashMapOf<String, Boolean>()
            for(t in task.subtasks){
                if(t.databaseId == null){
                    Log.w("Firebase","Subtask not in database.")
                    return
                }
                subtasks[t.databaseId!!] = true
            }

            var tags = hashMapOf<String, Boolean>()
            for(t in task.tags){
                if(t.databaseId == null){
                    Log.w("Firebase","Tag not in database.")
                    return
                }
                subtasks[t.databaseId!!] = true
            }


            val postValues = hashMapOf<String, Any>(
                "title" to task.title,
                "done" to task.done,
                "assignee" to task.assignee?.databaseId!!,
                "startCalendar" to (task.startCalendar?.time?.time!!),
                "endCalendar" to (task.endCalendar?.time?.time!!),
                "description" to (if(task.Description==null)"" else task.Description!!),
                "subtasks" to subtasks,
                "tags" to tags
            )

            val childUpdates = hashMapOf<String, Any>(
                "/tasks/$key" to postValues,
//                "/sections/$sectionId/$key" to true,
            )
            task.databaseId = key
            dataref.updateChildren(childUpdates)
        }

        fun save(section:Section){
            var key : String?
            if(section.databaseId == null){
                key = sections.push().key
            }else{
                key = section.databaseId
            }

            if (key == null) {
                Log.w("Firebase", "Couldn't get push key for posts")
                return
            }

            var tasks = hashMapOf<String, Boolean>()
            for(t in section.tasks){
                if(t.databaseId == null){
                    Log.w("Firebase","Subtask not in database.")
                    return
                }
                tasks[t.databaseId!!] = true
            }

            val postValues = hashMapOf<String, Any>(
                "name" to section.name,
                "tasks" to tasks
            )

            val childUpdates = hashMapOf<String, Any>(
                "/sections/$key" to postValues
            )
            section.databaseId = key
            dataref.updateChildren(childUpdates)
        }

        fun save(tag:Tag){
            var key : String?
            if(tag.databaseId == null){
                key = sections.push().key
            }else{
                key = tag.databaseId
            }

            if (key == null) {
                Log.w("Firebase", "Couldn't get push key for posts")
                return
            }


            val postValues = hashMapOf<String, Any>(
                "name" to tag.name,
                "color" to tag.color,

            )

            val childUpdates = hashMapOf<String, Any>(
                "/tags/$key" to postValues
            )
            tag.databaseId = key
            dataref.updateChildren(childUpdates)
        }

        fun save(user: User){
            var key : String?
            if(user.databaseId == null){
                key = sections.push().key
            }else{
                key = user.databaseId
            }

            if (key == null) {
                Log.w("Firebase", "Couldn't get push key for posts")
                return
            }


            val postValues = hashMapOf<String, Any>(
                "name" to user.name,
                "avatar" to user.avatar!!,
                )

            val childUpdates = hashMapOf<String, Any>(
                "/users/$key" to postValues
            )
            user.databaseId = key
            dataref.updateChildren(childUpdates)
        }

        fun load(dataSnapshot: DataSnapshot){
            var key : String?
            if(task.databaseId == null){
                key = tasks.push().key
            }else{
                key = task.databaseId
            }

            if (key == null) {
                Log.w("Firebase", "Couldn't get push key for posts")
                return
            }

            var subtasks = hashMapOf<String, Boolean>()
            for(t in task.subtasks){
                if(t.databaseId == null){
                    Log.w("Firebase","Subtask not in database.")
                    return
                }
                subtasks[t.databaseId!!] = true
            }

            var tags = hashMapOf<String, Boolean>()
            for(t in task.tags){
                if(t.databaseId == null){
                    Log.w("Firebase","Tag not in database.")
                    return
                }
                subtasks[t.databaseId!!] = true
            }


            val postValues = hashMapOf<String, Any>(
                "title" to task.title,
                "done" to task.done,
                "assignee" to task.assignee?.databaseId!!,
                "startCalendar" to (task.startCalendar?.time?.time!!),
                "endCalendar" to (task.endCalendar?.time?.time!!),
                "description" to (if(task.Description==null)"" else task.Description!!),
                "subtasks" to subtasks,
                "tags" to tags
            )

            val childUpdates = hashMapOf<String, Any>(
                "/tasks/$key" to postValues,
//                "/sections/$sectionId/$key" to true,
            )
            task.databaseId = key
            dataref.updateChildren(childUpdates)
        }
    }
}