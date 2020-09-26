package com.knowtech.kt_notes.mvvm.viewmodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.knowtech.kt_notes.mvvm.db.Note
import com.knowtech.kt_notes.mvvm.repositories.NotesRepository
import com.knowtech.kt_notes.screens.NotesActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class NotesViewModel(private val context: Context, private val repository: NotesRepository) :
    ViewModel() {

    lateinit var one: DocumentReference

    fun upsert(note: Note) = viewModelScope.launch { repository.upsert(note) }
    fun delete(note: Note) = viewModelScope.launch { repository.delete(note) }
    fun getAllNotes() = repository.getAllNotes()
    fun deleteAllNotes() = repository.deleteAllNotes()


    private var noteCollectionRef = Firebase.firestore.collection(NotesActivity.collection_name!!)

    fun saveNote(note: Note) = viewModelScope.launch {
        try {
            one = noteCollectionRef.add(note).await()

            Log.d("NotesViewModel", "saveNote: ${one.id}")


            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Note saved successfully!", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }


    suspend fun retrieveNotes(): List<Note> {
        val noteList = mutableListOf<Note>()
        val job = viewModelScope.launch {

            try {
                val querySnapshot = noteCollectionRef.get().await()

                for (document in querySnapshot.documents) {
                    val notes = document.toObject(Note::class.java)
                    if (notes != null) {
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

    suspend fun updateData(note: Note) = viewModelScope.launch {

            try {
                noteCollectionRef.document(note.document_id.toString()).set(
                    note,
                    SetOptions.merge()
                ).await()
            } catch (e: java.lang.Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }