package com.example.projekt_am_2023

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private var tasks: MutableList<Section> = mutableListOf()
    private var users: MutableList<User> = mutableListOf()
    private var tags: MutableList<Tag> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DatabaseLoader.dataref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                tasks = DatabaseLoader.loadDatabase(dataSnapshot)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("Firebase", "dataref - loadPost:onCancelled", databaseError.toException())
            }
        })

        DatabaseLoader.tags.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                tags = Tag.loadDatabaseArray(dataSnapshot)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("Firebase", "tags - loadPost:onCancelled", databaseError.toException())
            }
        })

        DatabaseLoader.users.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                users = User.loadDatabaseArray(dataSnapshot)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("Firebase", "users - loadPost:onCancelled", databaseError.toException())
            }
        })
    }
}
