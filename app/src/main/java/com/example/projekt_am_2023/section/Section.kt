package com.example.projekt_am_2023.section

import android.util.Log
import com.example.projekt_am_2023.database.DatabaseLoader
import com.example.projekt_am_2023.task.Task
import com.google.firebase.database.DataSnapshot
import java.io.Serializable

data class Section(
    var name: String = "",
    var tasks: MutableList<Task> = mutableListOf(), //is not saved to the database
    var databaseId: String? = null
): Serializable {
    fun saveDatabase() {
        val key: String? = if(this.databaseId == null) {
            DatabaseLoader.sections.push().key
        } else {
            this.databaseId
        }

        if(key == null) {
            Log.w("Firebase", "Couldn't get push key for posts")
            return
        }

        val postValues = hashMapOf<String, Any>(
            "name" to this.name,
        )

        val childUpdates = hashMapOf<String, Any>(
            "/sections/$key" to postValues
        )
        this.databaseId = key
        DatabaseLoader.dataref.updateChildren(childUpdates)
    }

    companion object {
        fun loadDatabaseArray(dataSnapshot: DataSnapshot): MutableList<Section> {
            val sections = mutableListOf<Section>()
            for(d in dataSnapshot.children) {
                sections.add(loadDatabase(d))
            }
            return sections
        }

        fun loadDatabaseMap(dataSnapshot: DataSnapshot): HashMap<String, Section> {
            val elements = HashMap<String, Section>()
            for(d in dataSnapshot.children) {
                val element = loadDatabase(d)
                elements[element.databaseId!!] = element
            }
            return elements
        }

        private fun loadDatabase(dataSnapshot: DataSnapshot): Section {
            val section = Section()
            section.databaseId = dataSnapshot.key
            section.name = dataSnapshot.child("name").value as String
            return section
        }

        fun assignTasks(tasksMap: HashMap<String, Task>) {
            for(t in tasksMap.values) {
                t.section?.tasks?.add(t)
            }
        }
    }
}