package com.knowtech.kt_notes.ui.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.knowtech.kt_notes.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

       val intent = Intent(this, LoginActivity::class.java)
        GlobalScope.launch {
            delay(3000)
            startActivity(intent)
            finish()
        }


    }
}