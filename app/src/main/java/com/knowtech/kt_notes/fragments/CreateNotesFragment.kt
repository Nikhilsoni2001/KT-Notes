package com.knowtech.kt_notes.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.knowtech.kt_notes.R
import com.knowtech.kt_notes.mvvm.db.Note
import com.knowtech.kt_notes.mvvm.viewmodels.NotesViewModel
import com.knowtech.kt_notes.screens.NotesActivity
import kotlinx.android.synthetic.main.fragment_create_notes.*

class CreateNotesFragment : Fragment(R.layout.fragment_create_notes) {
    lateinit var viewModel: NotesViewModel

    var noteCollectionRef = Firebase.firestore.collection(NotesActivity.collection_name!!)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as NotesActivity).viewModel

        btnCreateNote.setOnClickListener {

            val noteTitle = etNoteTitle.text.toString()
            val noteContent = etNoteContent.text.toString()

            if(noteTitle.isNotEmpty() && noteContent.isNotEmpty()) {
                val note = Note(0,noteTitle,noteContent, note_favourite = false, note_sync = false)
                viewModel.upsert(note)
                viewModel.saveNote(note)
                openHome()
            }
        }
    }

    private fun openHome() {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment, NotesFragment())
            ?.commit()
    }
}