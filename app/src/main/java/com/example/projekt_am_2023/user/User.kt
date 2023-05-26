package com.example.projekt_am_2023.user

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import com.example.projekt_am_2023.database.DatabaseLoader
import com.google.firebase.database.DataSnapshot
import com.google.firebase.storage.UploadTask
import java.io.Serializable
import java.util.UUID

data class User(
    var name: String = "",
    var avatar: String? = null,
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
            "avatar" to this.avatar!!,
        )

        val childUpdates = hashMapOf<String, Any>(
            "/users/$key" to postValues
        )

        this.databaseId = key
        DatabaseLoader.dataref.updateChildren(childUpdates)
    }

    fun loadAvatar(context: Context, imageView:ImageView) {
        if(avatar != null) {
            DatabaseLoader.loadImage("avatars/" + avatar!!, context, imageView)
        } else {
            Log.w("Data","User doesn't have avatar.")
        }
    }

    fun updateAvatar(file: Uri): UploadTask {
        val fileStr = file.toString()
        val name = UUID.randomUUID().toString()+fileStr.substring(fileStr.lastIndexOf("."))
        val avRef = DatabaseLoader.storref.child("avatars/$name")
        val uploadTask = avRef.putFile(file)

        uploadTask.addOnFailureListener {
            Log.w("Firebase","Avatar upload failed.")
        }.addOnSuccessListener {
            Log.i("Firebase","Avatar upload success.")
            avatar = name
        }
        return uploadTask
    }

    companion object {
        fun loadDatabaseArray(dataSnapshot: DataSnapshot): MutableList<User> {
            val users = mutableListOf<User>()
            for(d in dataSnapshot.children) {
                users.add(loadDatabase(d))
            }
            return users
        }

        fun loadDatabaseMap(dataSnapshot: DataSnapshot): HashMap<String, User> {
            val elements = HashMap<String, User>()
            for(d in dataSnapshot.children) {
                val element = loadDatabase(d)
                elements[element.databaseId!!] = element
            }
            return elements
        }

        private fun loadDatabase(dataSnapshot: DataSnapshot): User {
            val user = User()
            user.databaseId = dataSnapshot.key
            user.name = dataSnapshot.child("name").value as String
            user.avatar = dataSnapshot.child("avatar").value as String?
            return user
        }
    }
}
