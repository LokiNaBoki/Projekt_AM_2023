package com.example.projekt_am_2023.database

import android.content.Context
import android.widget.ImageView
import com.example.projekt_am_2023.section.Section
import com.example.projekt_am_2023.tag.Tag
import com.example.projekt_am_2023.task.Task
import com.example.projekt_am_2023.user.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class DatabaseLoader {
    companion object {
        val database = FirebaseDatabase.getInstance(
            "https://tasks-b3e1f-default-rtdb.europe-west1.firebasedatabase.app"
        ).apply {
            setPersistenceEnabled(true)
        }

        val sections = database.getReference("sections")
        val tasks = database.getReference("tasks")
        val users = database.getReference("users")
        val tags = database.getReference("tags")
        val dataref = database.reference

        private val storage = Firebase.storage("gs://tasks-b3e1f.appspot.com/")
        val storref = storage.reference
        
        fun loadDatabase(dataSnapshot: DataSnapshot): MutableList<Section> {
            val tagsMap = Tag.loadDatabaseMap(dataSnapshot.child("tags"))
            val usersMap = User.loadDatabaseMap(dataSnapshot.child("users"))
            val sectionsMap = Section.loadDatabaseMap(dataSnapshot.child("sections"))
            val tasksMap = Task.loadDatabaseMap(dataSnapshot.child("tasks"), tagsMap, usersMap, sectionsMap)

            Task.assignSubtasks(tasksMap)
            Section.assignTasks(tasksMap)

            return sectionsMap.values.toMutableList()
        }

        fun loadImage(name:String, context:Context, imageView:ImageView) {
            val reference = storref.child(name)
            GlideApp.with(context).load(reference).into(imageView)
        }
    }
}