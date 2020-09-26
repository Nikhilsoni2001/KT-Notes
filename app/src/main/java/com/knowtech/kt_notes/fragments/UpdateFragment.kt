package com.knowtech.kt_notes.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.knowtech.kt_notes.R
import com.knowtech.kt_notes.mvvm.db.Note
import com.knowtech.kt_notes.mvvm.viewmodels.NotesViewModel
import com.knowtech.kt_notes.screens.NotesActivity
import kotlinx.android.synthetic.main.fragment_update.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UpdateFragment : Fragment(R.layout.fragment_update) {

    lateinit var viewModel: NotesViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as NotesActivity).viewModel


        val args: UpdateFragmentArgs by navArgs()

        val noteRef = args.notes

        etTitle.setText(noteRef.note_title)
        etDescription.setText(noteRef.note_description)

        btnUpdate.setOnClickListener {

            val noteId = noteRef.note_id
            val noteTitle = etTitle.text.toString()
            val noteDesc = etDescription.text.toString()
            val fav = noteRef.note_favourite

            if (noteTitle.isNotEmpty() && noteDesc.isNotEmpty()) {
                elTitle.error = null
                elDescription.error = null

                val note =
                    Note(noteId, noteTitle, noteDesc, note_favourite = fav, note_sync = false)

                viewModel.upsert(note)

                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.updateData(note)
                }

                openHome()
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
