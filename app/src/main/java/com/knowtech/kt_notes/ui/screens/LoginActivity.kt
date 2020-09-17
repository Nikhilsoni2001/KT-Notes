package com.knowtech.kt_notes.ui.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.knowtech.kt_notes.R
import com.knowtech.kt_notes.models.Constants.Companion.GOOGLE_SIGN_IN_REQUEST_CODE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

class LoginActivity : AppCompatActivity() {

    private lateinit var elEmail: TextInputLayout
    private lateinit var etEmail: TextInputEditText
    private lateinit var elPassword: TextInputLayout
    private lateinit var etPassword: TextInputEditText
    private lateinit var txtRegister: TextView
    private lateinit var btnLogin: MaterialButton
    private lateinit var imgGoogle: ImageView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // initializing the firebase auth object
        auth = FirebaseAuth.getInstance()

        // initializing the elements
        initializations()

        // for switching to signup activity
        txtRegister.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        // on pressing login button
        btnLogin.setOnClickListener {
            loginWithEmailAndPassword()
        }

        imgGoogle.setOnClickListener {
            loginWithGoogle()
        }


    }

    // login with google
    private fun loginWithGoogle() {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.webclient_id))
            .requestEmail()
            .build()

        val signInIntent = GoogleSignIn.getClient(this, options).signInIntent
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE)
    }

    // selecting google account
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        CoroutineScope(Dispatchers.IO).launch {
            if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val exception = task.exception
                if (task.isSuccessful) {
                    try {
                        // Google Sign In was successful, authenticate with Firebase
                        val account = task.getResult(ApiException::class.java)!!
                        Log.d("Login", "firebaseAuthWithGoogle:" + account.id)
                        withContext(Dispatchers.Main) {
                            firebaseAuthWithGoogle(account.idToken!!)
                        }
                    } catch (e: ApiException) {
                        // Google Sign In failed, update UI appropriately
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@LoginActivity,
                                "Google sign in failed",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@LoginActivity,
                            exception.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credentials = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credentials).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Fail", Toast.LENGTH_LONG).show()
            }
        }
    }

    // login with email and password
    private fun loginWithEmailAndPassword() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isNotEmpty()) {
            elEmail.error = null
            if (password.isNotEmpty()) {
                elPassword.error = null
                loginUser(email, password)
            } else {
                elPassword.error = "Please enter a valid Password!!"
            }
        } else {
            elEmail.error = "Please enter a valid Email!!"
        }
    }

    // firebase login
    private fun loginUser(email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                withContext(Dispatchers.Main) {
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // for initializing all the items
    private fun initializations() {
        elEmail = findViewById(R.id.elEmail)
        etEmail = findViewById(R.id.etEmail)
        elPassword = findViewById(R.id.elPassword)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        txtRegister = findViewById(R.id.txtRegister)
        imgGoogle = findViewById(R.id.btnGoogleSignIn)
    }
}