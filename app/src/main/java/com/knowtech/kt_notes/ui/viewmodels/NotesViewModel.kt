package com.knowtech.kt_notes.ui.viewmodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.knowtech.kt_notes.db.Note
import com.knowtech.kt_notes.repositories.NotesRepository
import com.knowtech.kt_notes.ui.NotesActivity
import com.knowtech.kt_notes.util.Preferences
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class NotesViewModel(private val context: Context, private val repository: NotesRepository) :
    ViewModel() {


    private val preferences = Preferences(context)

    fun setDark(mode: Int) = preferences.setDark(mode)
    fun getDark(): Int = preferences.getDark()


    fun upsert(note: Note) = viewModelScope.launch { repository.upsert(note) }
    fun delete(note: Note) = viewModelScope.launch { repository.delete(note) }
    fun getAllNotes() = repository.getAllNotes()
    fun deleteAllNotes() = repository.deleteAllNotes()

    private var noteCollectionRef = Firebase.firestore.collection(NotesActivity.collection_name!!)

    suspend fun saveNote(note: Note): String {
        var id: String? = null

        val job = viewModelScope.launch {
            try {
                id = noteCollectionRef.add(note).await().id

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Note saved successfully!", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        job.join()

        return id.toString()

    }


    suspend fun retrieveNotes(): List<Note> {
        val noteList = mutableListOf<Note>()
        val job = viewModelScope.launch {

            try {
                val querySnapshot = noteCollectionRef.get().await()

                for (document in querySnapshot.documents) {
                    Log.d("TAG", "retrieveNotes: $document")
                    val notes = document.toObject(Note::class.java)
                    if (notes != null) {
                        notes.document_id = document.id
                        noteList.add(notes)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        job.join()
        return noteList
    }

    suspend fun updateData(note: Note, newNote: Map<String, Any>) = viewModelScope.launch {
        val noteQuery =
            noteCollectionRef.whereEqualTo("document_id", note.document_id.toString()).get().await()
        if (noteQuery.documents.isNotEmpty()) {
            for (document in noteQuery) {
                noteCollectionRef.document(document.id).set(newNote, SetOptions.merge()).await()
            }
        }
    }
}