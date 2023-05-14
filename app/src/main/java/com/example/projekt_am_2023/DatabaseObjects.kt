package com.example.projekt_am_2023

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.Calendar

@Entity
data class DatabaseTag(
    @PrimaryKey(autoGenerate = true) var id: Int,
    var name: String,
    var color: Int
    )

@Entity
data class DatabaseTask(
    @PrimaryKey(autoGenerate = true) var id:Int = 0,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "done") var done: Boolean,
    @ColumnInfo(name = "user_id") var assignee: Int?,
    @ColumnInfo(name = "start_calendar") var startCalendar: Calendar?,
    @ColumnInfo(name = "end_calendar") var endCalendar: Calendar?,
    @ColumnInfo(name = "description") var Description: String?,
    @ColumnInfo(name = "parent_id") var parentId: Int?,
    @ColumnInfo(name = "section_id") var sectionId: Int?,
//    @Embedded var subtasks: MutableList<Task>,
//    @Embedded var tags: MutableList<Tag>,
)

@Entity
data class DatabaseUser(
    @PrimaryKey(autoGenerate = true) var id:Int = 0,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "avatar") var avatar: Int?
)

@Entity
data class DatabasSection(
    @PrimaryKey(autoGenerate = true) var id: Int=0,
        var name: String,
//        var tasks: MutableList<Task>
)

data class UserAndTask(
    @Embedded val user: DatabaseUser,
    @Relation(
        parentColumn = "id",
        entityColumn = "user_id"
    )
    val playlists: List<DatabaseTask>
)

data class TaskWithSubtasks(
    @Embedded val task: DatabaseTask,
    @Relation(
        parentColumn = "id",
        entityColumn = "parent_id"
    )
    val playlists: List<DatabaseTask>
)

data class SectionWithTasks(
    @Embedded val task: DatabasSection,
    @Relation(
        parentColumn = "id",
        entityColumn = "section_id"
    )
    val playlists: List<DatabaseTask>
)
