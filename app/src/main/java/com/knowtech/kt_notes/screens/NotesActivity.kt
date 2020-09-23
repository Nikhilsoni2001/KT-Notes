package com.knowtech.kt_notes.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.knowtech.kt_notes.R
import com.knowtech.kt_notes.mvvm.db.NotesDatabase
import com.knowtech.kt_notes.mvvm.repositories.NotesRepository
import com.knowtech.kt_notes.mvvm.viewmodels.NotesViewModel
import com.knowtech.kt_notes.mvvm.viewmodels.NotesViewModelFactory

class NotesActivity : AppCompatActivity() {

    lateinit var viewModel: NotesViewModel


    companion object {
       //var collection_name = "Notes"
      val collection_name = Firebase.auth.currentUser?.email

    }



        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

            val database = NotesDatabase(this)
            val repository = NotesRepository(database)
            val factory = NotesViewModelFactory(this,repository)
            viewModel = ViewModelProvider(this,factory).get(NotesViewModel::class.java)



        //noteCollectionref = Firebase.firestore.collection(collection_name)


    }
}