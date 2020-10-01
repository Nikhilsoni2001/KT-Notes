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

        viewModel = (activity as NotesActivity).viewModel



        btnCreateNote.setOnClickListener {
            val noteTitle = etNoteTitle.text.toString()
            var id: String? = ""
            val noteContent = etNoteContent.text.toString()
            Log.d("docId_initialize", documentId.toString())

            if (noteTitle.isNotEmpty() && noteContent.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {

                    var note = Note(0, "", noteTitle, noteContent,false,false)
                    id = viewModel.saveNote(note)
                    Log.d("docId_saved_0", note.document_id!!)

                    var noteCollectionRef = Firebase.firestore.collection(NotesActivity.collection_name!!)
                   // val newNote = Note(0, id, noteTitle, noteContent, false,false)
                    val map = mutableMapOf<String, Any>()
                    map["document_id"] = id!!

                    noteCollectionRef.document(id!!).set(map, SetOptions.merge())

                    /*
                    val newNote = Note(0, id, noteTitle, noteContent, false,false)
                    val map = mutableMapOf<String, Any>()

                    viewModel.updateData(newNote,map)
                    Log.d("docId_updated", id.toString())

                    var realNote = Note(0, id.toString(), noteTitle, noteContent, note_favourite = false, note_sync = false)
                    viewModel.upsert(realNote)
                    Log.d("docId_added_to_room", realNote.document_id!!) */

                }




                findNavController().navigate(R.id.action_createNotesFragment_to_notesFragment)

            }
        }
    }


}