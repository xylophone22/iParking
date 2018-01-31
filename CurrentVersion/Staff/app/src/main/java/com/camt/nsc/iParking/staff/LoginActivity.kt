package com.camt.nsc.iParking.staff

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private var email: String? = null
    private var password: String? = null

    private var tranEmail: EditText? = null
    private var tranPassword: EditText? = null
    private var loginBtn: Button? = null

    private var mProgressBar: ProgressDialog? = null

    private var mAuth: FirebaseAuth? = null
    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initialise()

        if (mAuth!!.currentUser != null) {
            startActivity(Intent(this@LoginActivity, ResultActivity::class.java))
            finish()
        }
    }
    private fun initialise() {
        tranEmail = findViewById<View>(R.id.loginEmail) as EditText
        tranPassword = findViewById<View>(R.id.loginPassword) as EditText
        loginBtn = findViewById<View>(R.id.loginBtn) as Button
        mProgressBar = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()
        loginBtn!!.setOnClickListener { loginUser() }
    }

    private fun loginUser() {
        email = tranEmail?.text.toString()
        password = tranPassword?.text.toString()
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            mProgressBar!!.setMessage("Registering User...")
            mProgressBar!!.show()
            Log.d(TAG, "Logging in user.")
            mAuth!!.signInWithEmailAndPassword(email!!, password!!)
                    .addOnCompleteListener(this) { task ->
                        mProgressBar!!.hide()
                        if (task.isSuccessful) {
                            // Sign in success, update UI with signed-in user's information
                            Log.d(TAG, "signInWithEmail:success")
                            updateUI()
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "signInWithEmail:failure", task.exception)
                            Toast.makeText(this@LoginActivity, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show()
                        }
                    }
        } else {
            Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI() {
        val intent = Intent(this@LoginActivity, ResultActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}
