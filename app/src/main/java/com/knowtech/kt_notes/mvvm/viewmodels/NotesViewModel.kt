package com.knowtech.kt_notes.mvvm.viewmodels

import android.content.ContentProvider
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.knowtech.kt_notes.mvvm.db.Note
import com.knowtech.kt_notes.mvvm.repositories.NotesRepository
import com.knowtech.kt_notes.screens.NotesActivity
import kotlinx.android.synthetic.main.fragment_notes.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.lang.StringBuilder

class NotesViewModel(private val context: Context,private val repository: NotesRepository): ViewModel() {

    fun upsert(note: Note) = viewModelScope.launch { repository.upsert(note) }
    fun delete(note: Note) = viewModelScope.launch { repository.delete(note) }
    fun getAllNotes() = repository.getAllNotes()
    fun deleteAllNotes() = repository.deleteAllNotes()


    var noteCollectionRef = Firebase.firestore.collection(NotesActivity.collection_name!!)

        fun saveNote(note: Note) = viewModelScope.launch {
        try {
            noteCollectionRef.add(note).await()
            withContext(Dispatchers.Main) {
                Toast.makeText(context,"Note saved successfully!", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context,e.message, Toast.LENGTH_LONG).show()
            }
        }
    }



    suspend fun retrieveNotes(): List<Note> {
        val noteList = mutableListOf<Note>()
        val job = viewModelScope.launch {

            try {
                val querySnapshot = noteCollectionRef.get().await()

                for(document in querySnapshot.documents) {
                    val notes = document.toObject(Note::class.java)
                    if(notes!=null) {
                         noteList.add(notes)
                    }
                }


            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context,e.message,Toast.LENGTH_LONG).show()
                }
            }

        }

        job.join()

        val result = noteList

        return result
    }

























   /*     fun retrievePerson(): String {




            val job = viewModelScope.launch {
                val sb = StringBuilder()
               try {
                   val querySnapshot = noteCollectionRef.get().await()

                   for(document in querySnapshot.documents) {
                       val notes = document.toObject(Note::class.java)
                       sb.append("$notes\n")
                       Log.e("ishant",sb.toString())
                   }


               } catch (e: Exception) {
                   withContext(Dispatchers.Main) {
                       Toast.makeText(context,e.message,Toast.LENGTH_LONG).show()
                       sb.append("Error")
                       Log.e("ishant","B")
                   }
               }
       }

            job.await()

           val result = sb

        return result
        } */




        /*fun retrievePerson() = viewModelScope.launch {
            try {
                val querySnapshot = noteCollectionRef.get().await()
                val sb = StringBuilder()

                for(document in querySnapshot.documents) {
                    val notes = document.toObject(Note::class.java)
                    sb.append("$notes\n")
                }



            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context,e.message,Toast.LENGTH_LONG).show()
                }
            }

        }*/



}