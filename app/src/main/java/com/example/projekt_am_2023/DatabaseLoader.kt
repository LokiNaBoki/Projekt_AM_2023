package com.example.projekt_am_2023

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase

class DatabaseLoader {
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

        fun loadDatabase(dataSnapshot: DataSnapshot) : MutableList<Section> {
            var tagsMap = Tag.loadDatabaseMap(dataSnapshot.child("tags"))
            var usersMap = User.loadDatabaseMap(dataSnapshot.child("users"))
            var tasksMap = Task.loadDatabaseMap(dataSnapshot.child("tasks"),tagsMap,usersMap)

            Task.assignSubtasks(tasksMap)
            var sectionsMap = Section.loadDatabaseMap(dataSnapshot.child("sections"),tasksMap)

            return sectionsMap.values.toMutableList()
        }
    }
}