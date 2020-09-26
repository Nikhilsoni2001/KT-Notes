package com.knowtech.kt_notes.mvvm.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val note_id: Int = 0,
    val document_id: String? = null,
    val note_title: String = "",
    val note_description: String = "",
    val note_favourite: Boolean = false,
    val note_sync: Boolean = false
) : Serializable