package com.knowtech.kt_notes.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.knowtech.kt_notes.repositories.NotesRepository

class NotesViewModelFactory(private val context: Context,private val repository: NotesRepository): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NotesViewModel(context,repository) as T
    }
}