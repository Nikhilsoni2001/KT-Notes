package com.knowtech.kt_notes.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val note_id: Int = 0,
    var document_id: String? ="",
    val note_title: String = "",
    val note_description: String = "",
    val note_favourite: Int = 0,
    val note_sync: Int = 0
) : Serializable