package com.knowtech.kt_notes.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.knowtech.kt_notes.R
import com.knowtech.kt_notes.db.Note
import kotlinx.android.synthetic.main.notes_single_card.view.*

class NotesAdapter : RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {
    inner class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.note_id == newItem.note_id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notes_single_card, parent, false)
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_list, parent, false)
        return NotesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Note) -> Unit)? = null

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val note = differ.currentList[position]
        holder.itemView.apply {
            tvNoteTitle.text = note.note_title
            tvNoteDescription.text = note.note_description
            cbFavourite.isChecked = note.note_favourite

            if (note.note_sync) tvSync.text = "Sync"
            else tvSync.text = "Not Sync"

            setOnClickListener {
                onItemClickListener?.let { it(note) }
            }
        }

    }

    fun setOnItemClickListener(listener: (Note) -> Unit) {
        onItemClickListener = listener
    }


}