package com.knowtech.kt_notes.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.common.api.ApiException
import com.knowtech.kt_notes.R
import com.knowtech.kt_notes.mvvm.db.Note
import com.knowtech.kt_notes.ui.viewmodels.NotesViewModel
import com.knowtech.kt_notes.ui.NotesActivity
import kotlinx.android.synthetic.main.fragment_update.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdateFragment : Fragment(R.layout.fragment_update) {

    lateinit var viewModel: NotesViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as NotesActivity).viewModel


        val args: UpdateFragmentArgs by navArgs()

        val noteRef = args.notes
        val docId = noteRef.document_id


        val noteId = noteRef.note_id
        val noteTitle = etTitle.text.toString()
        val noteDesc = etDescription.text.toString()
        val fav = noteRef.note_favourite
        val oldNote = Note(noteId, noteTitle, noteDesc, note_favourite = fav, note_sync = false)

        etTitle.setText(noteRef.note_title)
        etDescription.setText(noteRef.note_description)

        Log.d("docId_to_update", docId!!)

        btnUpdate.setOnClickListener {

            if (etTitle.text!!.isNotEmpty() && etDescription.text!!.isNotEmpty()) {
                    elTitle.error = null
                    elDescription.error = null

                    val map = mutableMapOf<String,Any>()
                    map["note_title"] = etTitle.text.toString()
                    map["note_description"] = etDescription.text.toString()

                val note = Note(noteId,docId, etTitle.text.toString(), etDescription.text.toString(), note_favourite = fav, note_sync = false)
                Log.d("docId_ready_to_update", note.document_id!!)


                CoroutineScope(Dispatchers.IO).launch {
                   // Log.d("docId2"," ${ map["note_title"].toString() } ${map["note_description"].toString() } ")

                    try {
                        Log.d("docId_update_started", note.document_id!!)
                        viewModel.updateData(note, map)
                        Log.d("docId_updated", note.document_id!!)
                    } catch(e: ApiException) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(),e.message,Toast.LENGTH_LONG).show()
                        }
                    }


                }


            } else {
                elTitle.error = "Enter title"
                elDescription.error = "Enter Description"
            }
        }
    }

    private fun openHome() {
        findNavController().navigate(R.id.action_updateFragment_to_notesFragment)
    }

}
