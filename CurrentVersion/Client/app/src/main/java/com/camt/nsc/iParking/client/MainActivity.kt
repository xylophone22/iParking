package com.camt.nsc.iParking.client

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        main_emailBtn.setOnClickListener { startActivity(Intent(this@MainActivity, LoginActivity::class.java)) }

        main_createBtn.setOnClickListener { startActivity(Intent(this@MainActivity, RegisterActivity::class.java)) }
    }
}
