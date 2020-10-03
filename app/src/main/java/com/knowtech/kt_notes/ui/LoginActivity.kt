package com.knowtech.kt_notes.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.knowtech.kt_notes.R
import com.knowtech.kt_notes.util.Constants.Companion.GOOGLE_SIGN_IN_REQUEST_CODE
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

class LoginActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth

    override fun onStart() {
        super.onStart()
        auth.signOut()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()


      btnLogin.setOnClickListener {
            signupUser()
      }

        btnGoogleSignIn.setOnClickListener { signInWithGoogle() }
        txtRegisterScreen.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun signInWithGoogle() {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.webclient_id))
            .requestEmail()
            .build()

        val signInClient = GoogleSignIn.getClient(this, options)
        signInClient.signInIntent.also {
            startActivityForResult(it, GOOGLE_SIGN_IN_REQUEST_CODE) }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            super.onActivityResult(requestCode, resultCode, data)
            if(requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
                val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
                account?.let {
                    googleAuthForFirebase(it)
                } } } catch (e: Exception) {
            Toast.makeText(this,e.message,Toast.LENGTH_LONG).show()
        }

    }

    private fun googleAuthForFirebase(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.signInWithCredential(credentials).await()
                withContext(Dispatchers.Main) {
                }
            } catch(e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun signupUser() {
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main) {
                        Intent(applicationContext, NotesActivity::class.java).also {
                            startActivity(it)
                        }
                        finish()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}