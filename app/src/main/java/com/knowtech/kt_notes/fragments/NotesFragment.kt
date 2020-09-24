package com.knowtech.kt_notes.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.knowtech.kt_notes.R
import com.knowtech.kt_notes.adapters.NotesAdapter
import com.knowtech.kt_notes.mvvm.db.Note
import com.knowtech.kt_notes.mvvm.viewmodels.NotesViewModel
import com.knowtech.kt_notes.screens.NotesActivity
import kotlinx.android.synthetic.main.fragment_notes.*
import kotlinx.coroutines.*

class NotesFragment : Fragment(R.layout.fragment_notes) {

    lateinit var viewModel: NotesViewModel
    lateinit var notesList: List<Note>
    lateinit var notesAdapter: NotesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as NotesActivity).viewModel
        notesAdapter = NotesAdapter()

        rvNotes.apply {
            adapter = notesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        CoroutineScope(Dispatchers.IO).launch {
            viewModel.deleteAllNotes()
        val notesList = viewModel.retrieveNotes()
            for(i in 0..notesList.size-1) {
                viewModel.upsert(notesList[i])
            }

        }
        viewModel.getAllNotes().observe(viewLifecycleOwner, Observer { notes ->
            notesAdapter.differ.submitList(notes)
        })

        createNotesFabButton.setOnClickListener { findNavController().navigate(R.id.action_notesFragment_to_createNotesFragment) }
    }
}