package com.example.projekt_am_2023

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Color
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {
    lateinit var tasks: MutableList<Section>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val user1 = User("User 1", R.drawable.user1)
        val user2 = User("User 2", R.drawable.user2)

        val tag1 = Tag("First", Color.RED)
        val tag2 = Tag("First", Color.BLUE)

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        tasks = mutableListOf(
            Section(
                "Section 1",
                mutableListOf(
                    Task(
                        "Task 1.1", false, user1,
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-27 10:10")!! },
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-28 11:11")!! },
                        "Description of task 1.1", mutableListOf(), mutableListOf(tag1)
                    ),
                    Task(
                        "Task 1.2", false, user1,
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-26 09:05")!! },
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-29 12:20")!! },
                        "Description of task 1.2", mutableListOf(), mutableListOf(tag1, tag2)
                    )
                )
            ),
            Section(
                "Section 2",
                mutableListOf(
                    Task(
                        "Task 2.1", false, user1,
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-29 02:09")!! },
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-29 02:19")!! },
                        "Description of task 1.1", mutableListOf(), mutableListOf(tag1)
                    ),
                    Task(
                        "Task 2.2", false, user2,
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-26 09:05")!! },
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-29 12:20")!! },
                        "Description of task 2.2", mutableListOf(), mutableListOf(tag1, tag2)
                    ),
                    Task(
                        "Task 2.3", true, user2,
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-27 10:05")!! },
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-30 11:20")!! },
                        "Description of task 2.2", mutableListOf(), mutableListOf(tag1, tag2)
                    )
                )
            ),
            Section(
                "Section 3",
                mutableListOf(
                    Task(
                        "Task 3.1", false, user1,
                        Calendar.getInstance().apply { time = sdf.parse("2023-03-03 05:05")!! },
                        Calendar.getInstance().apply { time = sdf.parse("2023-05-05 03:03")!! },
                        "Description of task 3.1", mutableListOf(), mutableListOf(tag2)
                    ),
                    Task(
                        "Task 3.2", false, user2,
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-30 11:00")!! },
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-30 15:15")!! },
                        "Description of task 3.2", mutableListOf(), mutableListOf(tag2)
                    ),
                    Task(
                        "Task 3.3", false, user2,
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-28 15:00")!! },
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-29 20:11")!! },
                        "Description of task 3.2", mutableListOf(), mutableListOf(tag1)
                    )
                )
            ),
            Section(
                "Section 4",
                mutableListOf(
                    Task(
                        "Task 4.1", false, user2,
                        Calendar.getInstance().apply { time = sdf.parse("2023-03-28 17:05")!! },
                        Calendar.getInstance().apply { time = sdf.parse("2023-05-28 19:03")!! },
                        "Description of task 4.1", mutableListOf(), mutableListOf()
                    ),
                    Task(
                        "Task 4.2", false, user2,
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-29 12:00")!! },
                        Calendar.getInstance().apply { time = sdf.parse("2023-04-30 13:00")!! },
                        null, mutableListOf(), mutableListOf(tag2)
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
                        "Description of task 5.1", mutableListOf(), mutableListOf(tag2)
                    ),
                    Task(
                        "Task 5.2", false, user2,
                        Calendar.getInstance().apply { time = sdf.parse("2023-05-07 12:12")!! },
                        Calendar.getInstance().apply { time = sdf.parse("2023-07-05 13:13")!! },
                        "Description of task 5.2", mutableListOf(), mutableListOf(tag1)
                    ),
                    Task(
                        "Task 5.3", false, user2,
                        Calendar.getInstance().apply { time = sdf.parse("2023-09-09 09:09")!! },
                        null,
                        "Description of task 5.3", mutableListOf(), mutableListOf(tag1)
                    ),
                    Task(
                        "Task 5.4", false, user1,
                        null,
                        Calendar.getInstance().apply { time = sdf.parse("2023-06-06 20:00")!! },
                        "Description of task 5.4", mutableListOf(), mutableListOf(tag2)
                    ),
                )
            ),
        )

        tasks[0].tasks[0].subtasks.add(tasks[0].tasks[1])
        tasks[0].tasks[0].subtasks.add(tasks[1].tasks[1])

        tasks[4].tasks[0].subtasks.add(tasks[2].tasks[1])
        tasks[4].tasks[0].subtasks.add(tasks[3].tasks[1])
        tasks[4].tasks[0].subtasks.add(tasks[4].tasks[1])
    }
}