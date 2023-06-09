package com.example.projekt_am_2023.tag

import android.util.Log
import com.example.projekt_am_2023.database.DatabaseLoader
import com.google.firebase.database.DataSnapshot
import java.io.Serializable

data class Tag(
    var name: String = "",
    var color: Int = 0,
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
            "color" to this.color,
        )

        val childUpdates = hashMapOf<String, Any>(
            "/tags/$key" to postValues
        )

        this.databaseId = key
        DatabaseLoader.dataref.updateChildren(childUpdates)
    }

    companion object {
        fun loadDatabaseArray(dataSnapshot: DataSnapshot): MutableList<Tag> {
            val tags = mutableListOf<Tag>()
            for(d in dataSnapshot.children) {
                tags.add(loadDatabase(d))
            }
            return tags
        }

        fun loadDatabaseMap(dataSnapshot: DataSnapshot): HashMap<String, Tag> {
            val elements = HashMap<String, Tag>()
            for(d in dataSnapshot.children) {
                val element = loadDatabase(d)
                elements[element.databaseId!!] = element
            }
            return elements
        }

        private fun loadDatabase(dataSnapshot: DataSnapshot): Tag {
            val tag = Tag()
            tag.databaseId = dataSnapshot.key
            tag.name = dataSnapshot.child("name").value as String
            tag.color = (dataSnapshot.child("color").value as Long).toInt()
            return tag
        }
    }
}
