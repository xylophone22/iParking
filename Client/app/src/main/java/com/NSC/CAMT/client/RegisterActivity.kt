package com.NSC.CAMT.client

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import com.google.android.gms.vision.barcode.Barcode
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_result.*
import org.jetbrains.anko.toast

class RegisterActivity : AppCompatActivity() {
    var mAuth: FirebaseAuth? = null
    private val TAG: String = "Register Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()

        if (mAuth!!.currentUser != null) {
            startActivity(Intent(this@RegisterActivity, ResultActivity::class.java))
            finish()
        }

        register_registerBtn.setOnClickListener {
            val email = register_emailEditText.text.toString().trim { it <= ' ' }
            val password = register_passwordEditText.text.toString().trim { it <= ' ' }

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

            mAuth!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    if (password.length < 6) {
                        toast("Password too short! Please enter minimum 6 characters.")
                        Log.d(TAG, "Enter password less than 6 characters.")
                    } else {
                        toast("Authentication Failed: " + task.exception!!.message)
                        Log.d(TAG, "Authentication Failed: " + task.exception!!.message)
                    }
                } else {
                    toast("Create account successfully!")
                    Log.d(TAG, "Create account successfully!")
                    startActivity(Intent(this@RegisterActivity, ResultActivity::class.java))
                    finish()
                }
            }
        }

        register_signinBtn.setOnClickListener { startActivity(Intent(this@RegisterActivity, LoginActivity::class.java)) }
    }
}