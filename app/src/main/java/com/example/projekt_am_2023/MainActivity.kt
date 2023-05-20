package com.example.projekt_am_2023

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Color
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var tasks: MutableList<Section>
    private lateinit var users: MutableList<User>
    private lateinit var tags: MutableList<Tag>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        users = mutableListOf(
            User("User 1", R.drawable.user1),
            User("User 2", R.drawable.user2),
        )

        tags = mutableListOf(
            Tag("First", Color.RED),
            Tag("First", Color.BLUE),
        )

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        tasks = mutableListOf(
            Section(
                "Section 1",
                mutableListOf(
                    Task(
                        "Task 1.1", false, users[0],
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-27 10:10")!! },
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-28 11:11")!! },
                        "Description of task 1.1", mutableListOf(), mutableListOf(tags[0])
                    ),
                    Task(
                        "Task 1.2", false, users[0],
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-26 09:05")!! },
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-29 12:20")!! },
                        "Description of task 1.2", mutableListOf(), mutableListOf(tags[0], tags[1])
                    )
                )
            ),
            Section(
                "Section 2",
                mutableListOf(
                    Task(
                        "Task 2.1", false, users[0],
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-29 02:09")!! },
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-29 02:19")!! },
                        "Description of task 1.1", mutableListOf(), mutableListOf(tags[0])
                    ),
                    Task(
                        "Task 2.2", false, users[1],
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-26 09:05")!! },
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-29 12:20")!! },
                        "Description of task 2.2", mutableListOf(), mutableListOf(tags[0], tags[1])
                    ),
                    Task(
                        "Task 2.3", true, users[1],
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-27 10:05")!! },
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-30 11:20")!! },
                        "Description of task 2.2", mutableListOf(), mutableListOf(tags[0], tags[1])
                    )
                )
            ),
            Section(
                "Section 3",
                mutableListOf(
                    Task(
                        "Task 3.1", false, users[0],
                        Calendar.getInstance().apply { time = sdf.parse("2023-03-03 05:05")!! },
                        Calendar.getInstance().apply { time = sdf.parse("2023-05-05 03:03")!! },
                        "Description of task 3.1", mutableListOf(), mutableListOf(tags[1])
                    ),
                    Task(
                        "Task 3.2", false, users[1],
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-30 11:00")!! },
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-30 15:15")!! },
                        "Description of task 3.2", mutableListOf(), mutableListOf(tags[1])
                    ),
                    Task(
                        "Task 3.3", false, users[1],
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-28 15:00")!! },
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-29 20:11")!! },
                        "Description of task 3.2", mutableListOf(), mutableListOf(tags[0])
                    )
                )
            ),
            Section(
                "Section 4",
                mutableListOf(
                    Task(
                        "Task 4.1", false, users[1],
                        Calendar.getInstance().apply { time = sdf.parse("2023-03-28 17:05")!! },
                        Calendar.getInstance().apply { time = sdf.parse("2023-05-28 19:03")!! },
                        "Description of task 4.1", mutableListOf(), mutableListOf()
                    ),
                    Task(
                        "Task 4.2", false, users[1],
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-29 12:00")!! },
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-30 13:00")!! },
                        null, mutableListOf(), mutableListOf(tags[1])
                    ),
                )
            ),
            Section(
                "Section 5",
                mutableListOf(
                    Task(
                        "Task 5.1", false, null,
                        Calendar.getInstance().apply { time = sdf.parse("2023-06-06 06:06")!! },
                        Calendar.getInstance().apply { time = sdf.parse("2023-06-07 06:07")!! },
                        "Description of task 5.1", mutableListOf(), mutableListOf(tags[1])
                    ),
                    Task(
                        "Task 5.2", false, users[1],
                        Calendar.getInstance().apply { time = sdf.parse("2023-05-07 12:12")!! },
                        Calendar.getInstance().apply { time = sdf.parse("2023-07-05 13:13")!! },
                        "Description of task 5.2", mutableListOf(), mutableListOf(tags[0])
                    ),
                    Task(
                        "Task 5.3", false, users[1],
                        Calendar.getInstance().apply { time = sdf.parse("2023-09-09 09:09")!! },
                        null,
                        "Description of task 5.3", mutableListOf(), mutableListOf(tags[0])
                    ),
                    Task(
                        "Task 5.4", false, users[0],
                        null,
                        Calendar.getInstance().apply { time = sdf.parse("2023-06-06 20:00")!! },
                        "Description of task 5.4", mutableListOf(), mutableListOf(tags[1])
                    ),
                )
            ),
        )

        tasks[0].tasks[0].subtasks.add(tasks[0].tasks[1])
        tasks[0].tasks[0].subtasks.add(tasks[1].tasks[1])
        tasks[4].tasks[0].subtasks.add(tasks[2].tasks[1])
        tasks[4].tasks[0].subtasks.add(tasks[3].tasks[1])
        tasks[4].tasks[0].subtasks.add(tasks[4].tasks[1])

//        user1.saveDatabase()
//        user2.saveDatabase()
//        tag1.saveDatabase()
//        tag2.saveDatabase()
//        tasks[0].tasks[1].saveDatabase()
//        tasks[1].tasks[1].saveDatabase()
//        tasks[2].tasks[1].saveDatabase()
//        tasks[3].tasks[1].saveDatabase()
//        tasks[4].tasks[1].saveDatabase()
//        for(s in tasks){
//            for(t in s.tasks){
//                t.saveDatabase()
//            }
//            s.saveDatabase()
//        }

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.i("Firebase", "Tasks to load $dataSnapshot")
                var tasks = DatabaseLoader.loadDatabase(dataSnapshot)
//                val sections = dataSnapshot.getValue<MutableList<Section>>()
                Log.i("Firebase", "Tasks loaded ${tasks[0].name}")
                Log.i("Firebase", "Tasks loaded ${tasks[0].tasks[0].title}")
//                if(tasks[0].tasks[0].title == "Task 4.2"){
//                    tasks[0].tasks[0].title = "Changed Title"
//                    tasks[0].tasks[0].saveDatabase()
//                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("Firebase", "loadPost:onCancelled", databaseError.toException())
            }
        }
        DatabaseLoader.dataref.addValueEventListener(postListener)

    }
}


