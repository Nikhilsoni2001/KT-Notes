package com.knowtech.kt_notes.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.knowtech.kt_notes.R
import com.knowtech.kt_notes.mvvm.db.Note
import com.knowtech.kt_notes.mvvm.viewmodels.NotesViewModel
import com.knowtech.kt_notes.screens.NotesActivity
import kotlinx.android.synthetic.main.fragment_notes.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.lang.StringBuilder


class NotesFragment : Fragment(R.layout.fragment_notes) {
    lateinit var viewModel: NotesViewModel



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as NotesActivity).viewModel

        CoroutineScope(Dispatchers.IO).launch {
            val result = viewModel.retrievePerson()
            withContext(Dispatchers.Main) {
            tvResult.text = result }
        }





        createNotesFabButton.setOnClickListener { findNavController().navigate(R.id.action_notesFragment_to_createNotesFragment) }
    }





}