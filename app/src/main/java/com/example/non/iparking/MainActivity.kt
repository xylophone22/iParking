package com.example.non.iparking

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    private val TAG: String = "Main Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()

        if (mAuth!!.currentUser != null) {
            Log.d(TAG, "Continue with: " + mAuth!!.currentUser!!.email)
            startActivity(Intent(this@MainActivity, ResultActivity::class.java))
            finish()
        }

        main_emailBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }

        main_createBtn.setOnClickListener { startActivity(Intent(this@MainActivity, RegisterActivity::class.java)) }
    }
}
