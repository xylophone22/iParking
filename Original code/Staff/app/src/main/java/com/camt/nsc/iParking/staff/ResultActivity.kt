package com.camt.nsc.iParking.staff

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.gms.vision.barcode.Barcode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import kotlinx.android.synthetic.main.activity_result.*
import org.jetbrains.anko.toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference

class ResultActivity : AppCompatActivity() {

    val REQUEST_CODE = 1001
    val PERMISSION_REQUEST = 2001

    //Firebase references
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    //UI elements
    private var tvFirstName: TextView? = null
    private var tvLastName: TextView? = null
    private var tvStatus: TextView? = null
    private var sendStatus: EditText? = null
    private var btnSendStatus: Button? = null


    //Grobal variable
    private var status: String? = null

    private val TAG: String = "Result Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        initialise()

        result_signOutBtn.setOnClickListener {
            mAuth!!.signOut()
            toast("Signed out!")
            Log.d(TAG, "Signed out!")
            startActivity(Intent(this@ResultActivity, MainActivity::class.java))
            finish()
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, Array(1, { android.Manifest.permission.CAMERA }), PERMISSION_REQUEST)


        scanBtn.setOnClickListener {
            startActivityForResult(Intent(applicationContext, ScanActivity::class.java), REQUEST_CODE)
        }

        updateInformation()

    }

    private fun initialise() {
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Users")
        mAuth = FirebaseAuth.getInstance()
        tvFirstName = findViewById<View>(R.id.tv_first_name) as TextView
        tvLastName = findViewById<View>(R.id.tv_last_name) as TextView
        tvStatus = findViewById<View>(R.id.result_status) as TextView
        sendStatus = findViewById<View>(R.id.send_status) as EditText
        btnSendStatus = findViewById<View>(R.id.btn_send_status) as Button
        btnSendStatus!!.setOnClickListener { updateStatus() }
    }

    override fun onStart() {
        super.onStart()
        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)

        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tvFirstName!!.text = snapshot.child("firstName").value as String
                tvLastName!!.text = snapshot.child("lastName").value as String
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val barcode: Barcode = data.getParcelableExtra("barcode")
                txtResult.text = barcode.displayValue
            }
        }
    }

    private fun updateInformation(){

        val mClient = FirebaseDatabase.getInstance().getReference("Users")
        val dataClient = mClient.child("LdbtCAxCLgdupOqshl4Lzb7jCHg1")

        dataClient.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                tvStatus!!.text = snapshot.child("status").value as String
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun updateStatus(){

        status = sendStatus?.text.toString()

        val mStatus = FirebaseDatabase.getInstance().getReference("Users")
        val condition = mStatus.child("LdbtCAxCLgdupOqshl4Lzb7jCHg1")
        condition.child("status").setValue(status)
    }
}
