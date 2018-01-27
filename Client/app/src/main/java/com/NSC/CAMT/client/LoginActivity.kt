package com.NSC.CAMT.client

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.toast

class LoginActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    private val TAG: String = "Login Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()

        if (mAuth!!.currentUser != null) {
            startActivity(Intent(this@LoginActivity, ResultActivity::class.java))
            finish()
        }

        login_loginBtn.setOnClickListener {
            val email = login_emailEditText.text.toString().trim { it <= ' ' }
            val password = login_passwordEditText.text.toString().trim { it <= ' ' }

            if (email.isEmpty()) {
                toast("Please enter your email address.")
                Log.d(TAG, "Email was empty!")
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                toast("Please enter your password.")
                Log.d(TAG, "Password was empty!")
                return@setOnClickListener
            }

            mAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    if (password.length < 6) {
                        login_passwordEditText.error = "Please check your password. Password must have minimum 6 characters."
                        Log.d(TAG, "Enter password less than 6 characters.")
                    } else {
                        toast("Authentication Failed: " + task.exception!!.message)
                        Log.d(TAG, "Authentication Failed: " + task.exception!!.message)
                    }
                } else {
                    toast("Sign in successfully!")
                    Log.d(TAG, "Sign in successfully!")
                    startActivity(Intent(this@LoginActivity, ResultActivity::class.java))
                    finish()
                }
            }
        }

        login_backBtn.setOnClickListener { onBackPressed() }
    }
}
