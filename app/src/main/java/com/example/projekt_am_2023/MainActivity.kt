package com.example.projekt_am_2023

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
/*
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        val ft = supportFragmentManager.beginTransaction()
        val calendar = Calendar.getInstance().apply { time = sdf.parse("2023-04-29 10:10")!! }
        Log.i("month", calendar.time.toString())
        Log.i("month", calendar.get(Calendar.MONTH).toString())
        calendar.set(Calendar.MONTH, 4)
        Log.i("month", calendar.time.toString())
        Log.i("month", calendar.get(Calendar.MONTH).toString())
        val mFragment = ListView.newInstance()
        ft.replace(R.id.fragment1, mFragment)
        ft.commit()
        //Thread.sleep(5000)
        //ListView().changeDate(Calendar.getInstance().apply { time = sdf.parse("2023-05-28 10:10")!! })
        findViewById<Button>(R.id.button).setOnClickListener {
            mFragment.changeDate(Calendar.getInstance().apply { time = sdf.parse("2023-04-29 10:10")!! })
        }
    }

}*/
