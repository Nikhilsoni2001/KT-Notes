package com.knowtech.kt_notes.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.knowtech.kt_notes.R
import com.knowtech.kt_notes.auth.LoginActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //Firebase.auth.signOut()

        if(Firebase.auth.currentUser==null) {

       val intent = Intent(this, LoginActivity::class.java)
        GlobalScope.launch {
            delay(3000)
            startActivity(intent)
            finish()
        } }
        else {
            val intent = Intent(this, NotesActivity::class.java)
            GlobalScope.launch {
                delay(3000)
                startActivity(intent)
                finish()
            }

        }




    }
}