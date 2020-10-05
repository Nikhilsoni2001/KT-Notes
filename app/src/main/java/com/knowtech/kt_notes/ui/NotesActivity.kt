package com.knowtech.kt_notes.ui

import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.knowtech.kt_notes.R
import com.knowtech.kt_notes.db.Note
import com.knowtech.kt_notes.ui.fragments.FavouriteFragment
import com.knowtech.kt_notes.ui.fragments.NotesFragment
import com.knowtech.kt_notes.db.NotesDatabase
import com.knowtech.kt_notes.repositories.NotesRepository
import com.knowtech.kt_notes.ui.viewmodels.NotesViewModel
import com.knowtech.kt_notes.ui.viewmodels.NotesViewModelFactory
import com.knowtech.kt_notes.util.NetworkConnection
import kotlinx.android.synthetic.main.activity_notes.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NotesActivity : AppCompatActivity() {


    private lateinit var toggle: ActionBarDrawerToggle
    private var mode: Int = 0

    companion object {
        val collection_name = Firebase.auth.currentUser?.email
        lateinit var viewModel: NotesViewModel
        var internet = true


       fun syncNotes(owner: LifecycleOwner) {
            viewModel.getNotesToSync().observe(owner, Observer { notes ->
                for (i in 0..notes.size - 1) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val syncNote = Note(
                            notes[i].note_id,
                            notes[i].document_id,
                            notes[i].note_title,
                            notes[i].note_description,
                            notes[i].note_favourite,
                            1
                        )
                        val id = viewModel.saveNote(syncNote)
                        val noteCollectionRef = Firebase.firestore.collection(collection_name!!)
                        val map = mutableMapOf<String, Any>()
                        map["document_id"] = id
                        noteCollectionRef.document(id).set(map, SetOptions.merge())

                        val mainNote = Note(
                            notes[i].note_id,
                            id,
                            notes[i].note_title,
                            notes[i].note_description,
                            notes[i].note_favourite,
                            1
                        )
                        viewModel.upsert(mainNote)
                    }} })
        }













    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        val database = NotesDatabase(this)
        val repository = NotesRepository(database)
        val factory = NotesViewModelFactory(this, repository)
        viewModel = ViewModelProvider(this, factory).get(NotesViewModel::class.java)

        internet = false


        val networkConnection = NetworkConnection(applicationContext)

        networkConnection.observe(this, Observer { isConnected ->
                internet = true
        })

        if(internet==true) { syncNotes(this) }


        checkTheme()

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val drwView = navView.inflateHeaderView(R.layout.nav_header)
        val userInfo = drwView.findViewById<TextView>(R.id.tvAuthUserInfo)
        userInfo.text = collection_name


        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.miHome -> openHome()
                R.id.miFavourites -> openFavourites()
                R.id.miDarkMode -> Toast.makeText(
                    applicationContext,
                    "Dark Mode",
                    Toast.LENGTH_LONG
                ).show()
                R.id.miInfo -> Toast.makeText(
                    applicationContext,
                    "App made by Ishant, Nikhil and Aditya",
                    Toast.LENGTH_LONG
                ).show()
                R.id.miSignOut -> {
                    Firebase.auth.signOut()

                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }
    }



    private fun openHome() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, NotesFragment())
            .commit()
    }

    private fun openFavourites() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, FavouriteFragment())
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu?.findItem(R.id.search)
        val searchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                searchView.setQuery("", false)
                searchItem.collapseActionView()
                Toast.makeText(this@NotesActivity, "Looking for $query", Toast.LENGTH_SHORT).show()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Toast.makeText(this@NotesActivity, "Looking for $newText", Toast.LENGTH_SHORT)
                    .show()
                return false
            }
        })
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        // for selecting theme
        when (item.itemId) {
            R.id.theme -> {
                chooseThemeDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun chooseThemeDialog() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.choose_theme_text))
        val styles = arrayOf("Light", "Dark", "System default")
        val intChecked = viewModel.getDark()

        builder.setSingleChoiceItems(styles, intChecked) { dialog, mode ->

            when (mode) {
                0 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    viewModel.setDark(0)
                    delegate.applyDayNight()
                    dialog.dismiss()
                }
                1 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    viewModel.setDark(1)
                    delegate.applyDayNight()
                    dialog.dismiss()
                }
                2 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    viewModel.setDark(2)
                    delegate.applyDayNight()
                    dialog.dismiss()
                }
            }
        }

        val dialog = builder.create()
        dialog.show()
    }


  /*  private fun syncNotes() {
        viewModel.getNotesToSync().observe(this, Observer { notes ->
            for (i in 0..notes.size - 1) {
                CoroutineScope(Dispatchers.IO).launch {
                    val syncNote = Note(
                        notes[i].note_id,
                        notes[i].document_id,
                        notes[i].note_title,
                        notes[i].note_description,
                        notes[i].note_favourite,
                        1
                    )
                    val id = viewModel.saveNote(syncNote)
                    val noteCollectionRef = Firebase.firestore.collection(collection_name!!)
                    val map = mutableMapOf<String, Any>()
                    map["document_id"] = id
                    noteCollectionRef.document(id).set(map, SetOptions.merge())

                    val mainNote = Note(
                        notes[i].note_id,
                        id,
                        notes[i].note_title,
                        notes[i].note_description,
                        notes[i].note_favourite,
                        1
                    )
                    viewModel.upsert(mainNote)
                }} })
    }

    */


    private fun checkTheme() {
        when (viewModel.getDark()) {

            0 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                delegate.applyDayNight()
            }
            1 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                delegate.applyDayNight()
            }
            2 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                delegate.applyDayNight()
            }
        }
    }
}