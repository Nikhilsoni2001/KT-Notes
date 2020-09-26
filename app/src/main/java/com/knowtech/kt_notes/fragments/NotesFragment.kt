package com.knowtech.kt_notes.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.knowtech.kt_notes.R
import com.knowtech.kt_notes.adapters.NotesAdapter
import com.knowtech.kt_notes.mvvm.db.Note
import com.knowtech.kt_notes.mvvm.viewmodels.NotesViewModel
import com.knowtech.kt_notes.screens.NotesActivity
import kotlinx.android.synthetic.main.fragment_notes.*
import kotlinx.android.synthetic.main.note_list.*
import kotlinx.coroutines.*

class NotesFragment : Fragment(R.layout.fragment_notes) {

    lateinit var viewModel: NotesViewModel
    lateinit var notesAdapter: NotesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as NotesActivity).viewModel
        notesAdapter = NotesAdapter()

        notesAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("notes", it)
            }
            findNavController().navigate(R.id.action_notesFragment_to_updateFragment, bundle)
        }

        rvNotes.apply {
            adapter = notesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }



        CoroutineScope(Dispatchers.IO).launch {
            viewModel.deleteAllNotes()
            val notesList = viewModel.retrieveNotes()
            for (element in notesList) {
                viewModel.upsert(element)
            }

        }
        viewModel.getAllNotes().observe(viewLifecycleOwner, Observer { notes ->
            notesAdapter.differ.submitList(notes)
        })


        val itemTouchHelperCallback = object: ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }


            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val note = notesAdapter.differ.currentList[position]
                viewModel.delete(note)
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(rvNotes)
        }




        createNotesFabButton.setOnClickListener { findNavController().navigate(R.id.action_notesFragment_to_createNotesFragment) }
    }
}