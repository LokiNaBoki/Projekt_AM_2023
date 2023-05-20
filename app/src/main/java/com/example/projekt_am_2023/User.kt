package com.example.projekt_am_2023

import android.util.Log
import com.google.firebase.database.DataSnapshot
import java.io.Serializable

data class User(
    var name: String="",
    var avatar: Int?=null,
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
            "avatar" to this.avatar!!,
        )

        val childUpdates = hashMapOf<String, Any>(
            "/users/$key" to postValues
        )

        this.databaseId = key
        DataViewModel.dataref.updateChildren(childUpdates)
    }

    companion object{
        fun loadDatabaseArray(dataSnapshot: DataSnapshot) : MutableList<User>{
            var users = mutableListOf<User>()
            for (d in dataSnapshot.children) {
                users.add(loadDatabase(d))
            }
            return users
        }
        fun loadDatabase(dataSnapshot: DataSnapshot) : User{
            Log.i("Firebase",""+dataSnapshot)
            var user : User =  User()
            user.databaseId = dataSnapshot.key
            user.name = dataSnapshot.child("name").value as String
            user.avatar = (dataSnapshot.child("avatar").value as Long?)?.toInt()
            return user
        }
    }
}
