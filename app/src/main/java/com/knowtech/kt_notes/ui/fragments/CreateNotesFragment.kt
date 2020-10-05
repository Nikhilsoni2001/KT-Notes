package com.knowtech.kt_notes.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.knowtech.kt_notes.R
import com.knowtech.kt_notes.db.Note
import com.knowtech.kt_notes.ui.viewmodels.NotesViewModel
import com.knowtech.kt_notes.ui.NotesActivity
import kotlinx.android.synthetic.main.fragment_create_notes.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateNotesFragment : Fragment(R.layout.fragment_create_notes) {
    private lateinit var viewModel: NotesViewModel
    private var documentId: String? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // viewModel = (activity as NotesActivity).viewModel
        viewModel = NotesActivity.viewModel



        btnCreateNote.setOnClickListener {
            val noteTitle = etNoteTitle.text.toString()
            var id: String? = ""
            val noteContent = etNoteContent.text.toString()


            if (noteTitle.isNotEmpty() && noteContent.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {

                    var note = Note(0, "", noteTitle, noteContent,0,0)
                    viewModel.upsert(note)
                }

                findNavController().navigate(R.id.action_createNotesFragment_to_notesFragment)

            }
        }
    }


}