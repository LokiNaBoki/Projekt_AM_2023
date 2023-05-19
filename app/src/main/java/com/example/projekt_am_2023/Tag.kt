package com.example.projekt_am_2023

import android.util.Log
import com.google.firebase.database.DataSnapshot
import java.io.Serializable
import java.util.Calendar

data class Tag(
    var name: String="",
    var color: Int=0,
    var tasks: MutableList<Task> = mutableListOf(),
    var databaseId: String?=null
) : Serializable{
    fun saveDatabase(){
        var key : String?
        if(this.databaseId == null){
            key = DataViewModel.sections.push().key
        }else{
            key = this.databaseId
        }

        if (key == null) {
            Log.w("Firebase", "Couldn't get push key for posts")
            return
        }


        val postValues = hashMapOf<String, Any>(
            "name" to this.name,
            "color" to this.color,

            )

        val childUpdates = hashMapOf<String, Any>(
            "/tags/$key" to postValues
        )
        this.databaseId = key
        DataViewModel.dataref.updateChildren(childUpdates)
    }

    companion object{
        fun loadDatabaseArray(dataSnapshot: DataSnapshot) : MutableList<Tag>{
            var tags = mutableListOf<Tag>()
            tags.add(loadDatabase(dataSnapshot))
            return tags
        }
        fun loadDatabase(dataSnapshot: DataSnapshot) : Tag{
            var tag : Tag = Tag()
            tag.databaseId = dataSnapshot.key
            tag.name = dataSnapshot.child("name").value as String
            tag.color = dataSnapshot.child("name").value as Int
            return tag
        }
    }
}
