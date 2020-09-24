package com.knowtech.kt_notes.mvvm.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val note_id: Int = 0,
    val note_title: String = "",
    val note_content: String = "",
    val note_favourite: Boolean = false,
    val note_sync: Boolean = false
)