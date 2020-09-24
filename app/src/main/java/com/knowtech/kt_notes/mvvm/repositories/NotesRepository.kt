package com.knowtech.kt_notes.mvvm.repositories

import com.knowtech.kt_notes.mvvm.db.Note
import com.knowtech.kt_notes.mvvm.db.NotesDatabase

class NotesRepository(private val db: NotesDatabase) {

    suspend fun upsert(note: Note) = db.getNotesDao().upsert(note)
    suspend fun delete(note: Note) = db.getNotesDao().delete(note)
    fun getAllNotes() = db.getNotesDao().getAllNotes()
    fun deleteAllNotes() = db.getNotesDao().deleteAllNotes()

}