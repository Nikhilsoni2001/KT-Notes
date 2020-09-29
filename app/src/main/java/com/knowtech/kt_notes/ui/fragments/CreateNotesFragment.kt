package com.knowtech.kt_notes.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
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

        viewModel = (activity as NotesActivity).viewModel



        Firebase.firestore.collection(NotesActivity.collection_name!!)
            .addSnapshotListener { snapshot, _ ->
                documentId = snapshot?.documents.toString()
            }

        btnCreateNote.setOnClickListener {
            val noteTitle = etNoteTitle.text.toString()
            var id: String? = ""
            val noteContent = etNoteContent.text.toString()
            Log.d("docId", documentId.toString())

            if (noteTitle.isNotEmpty() && noteContent.isNotEmpty()) {
                var note =
                    Note(0, "", noteTitle, noteContent, note_favourite = false, note_sync = false)

                CoroutineScope(Dispatchers.IO).launch {
                    id = viewModel.saveNote(note)
                }



                note = Note(
                    0,
                    id.toString(),
                    noteTitle,
                    noteContent,
                    note_favourite = false,
                    note_sync = false
                )


                viewModel.upsert(note)
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