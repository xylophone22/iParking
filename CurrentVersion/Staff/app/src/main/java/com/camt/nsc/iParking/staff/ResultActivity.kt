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

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    private var showFirstName: TextView? = null
    private var showLastName: TextView? = null
    private var showStatus: TextView? = null
    private var sendStatus: EditText? = null
    private var sendStatusBtn: Button? = null

    private var status: String? = null
    private var qrcodeDataResult:String = "lB3y1YjRT3SuecnYRbwvNITacKh2"

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
        showFirstName = findViewById<View>(R.id.show_first_name) as TextView
        showLastName = findViewById<View>(R.id.show_last_name) as TextView
        showStatus = findViewById<View>(R.id.show_status) as TextView
        sendStatus = findViewById<View>(R.id.send_status) as EditText
        sendStatusBtn = findViewById<View>(R.id.send_statusBtn) as Button
        sendStatusBtn!!.setOnClickListener { updateStatus() }
    }

    override fun onStart() {
        super.onStart()
        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)

        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                showFirstName!!.text = snapshot.child("firstName").value as String
                showLastName!!.text = snapshot.child("lastName").value as String
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val barcode: Barcode = data.getParcelableExtra("barcode")
                txtResult.text = barcode.rawValue
                    this.qrcodeDataResult = txtResult.text.toString()
            }
        }
    }

    private fun updateInformation(){
            val mClient = FirebaseDatabase.getInstance().getReference("Users")
            val dataClient = mClient.child(this.qrcodeDataResult)

            dataClient.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    showStatus!!.text = snapshot.child("status").value as String
                }
                override fun onCancelled(databaseError: DatabaseError) {}
            })

    }

    private fun updateStatus(){
            status = sendStatus?.text.toString()
            val mStatus = FirebaseDatabase.getInstance().getReference("Users")
            val condition = mStatus.child(this.qrcodeDataResult)
            condition.child("status").setValue(status)
    }
}
