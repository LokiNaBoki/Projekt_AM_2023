package com.example.projekt_am_2023

import android.util.Log
import com.google.firebase.database.DataSnapshot

data class Section(
    var name: String = "",
    var tasks: MutableList<Task> = mutableListOf(),
    var databaseId: String? = null
    ){

    fun saveDatabase(){
        var key : String?
        if(this.databaseId == null){
            key = DatabaseLoader.sections.push().key
        }else{
            key = this.databaseId
        }

        if (key == null) {
            Log.w("Firebase", "Couldn't get push key for posts")
            return
        }

        var tasks = hashMapOf<String, Boolean>()
        for(t in this.tasks){
            if(t.databaseId == null){
                Log.w("Firebase","Subtask not in database.")
                return
            }
            tasks[t.databaseId!!] = true
        }

        val postValues = hashMapOf<String, Any>(
            "name" to this.name,
            "tasks" to tasks
        )

        val childUpdates = hashMapOf<String, Any>(
            "/sections/$key" to postValues
        )
        this.databaseId = key
        DatabaseLoader.dataref.updateChildren(childUpdates)
    }

    companion object{
//        fun loadDatabaseArray(dataSnapshot: DataSnapshot) : MutableList<Section> {
//            var sections = mutableListOf<Section>()
//            for (d in dataSnapshot.children){
//                sections.add(loadDatabase(d))
//            }
//            return sections
//        }

        fun loadDatabaseMap(dataSnapshot: DataSnapshot, tasks:HashMap<String,Task>) : HashMap<String,Section> {
            var elements = HashMap<String,Section>()
            for (d in dataSnapshot.children){
                val element = loadDatabase(d, tasks)
                elements[element.databaseId!!] = element
            }
            return elements
        }

        fun loadDatabase(dataSnapshot: DataSnapshot, tasks:HashMap<String,Task>) : Section{
//            Log.i("Firebase",""+dataSnapshot)
            var section : Section =  Section()
            section.databaseId = dataSnapshot.key
            section.name = dataSnapshot.child("name").value as String
            for(t in dataSnapshot.child("tasks").children){
                if(tasks.containsKey(t.key)){
                    section.tasks.add(tasks[t.key]!!)
                }
            }
            return section
        }
    }
}