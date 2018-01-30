package com.camt.nsc.iParking.client

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_result.*
import org.jetbrains.anko.toast

class ResultActivity : AppCompatActivity() {

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    private var showFirstName: TextView? = null
    private var showLastName: TextView? = null
    private var showStatus: TextView? = null
    private var resetStatusBtn: TextView? = null
    private val TAG: String = "Result Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        initialise()

        resultSignOutBtn.setOnClickListener {
            mAuth!!.signOut()
            toast("Signed out!")
            Log.d(TAG, "Signed out!")
            startActivity(Intent(this@ResultActivity, MainActivity::class.java))
            finish()
        }

        qrBtn.setOnClickListener { startActivity(Intent(this@ResultActivity, GenQRActivity::class.java)) }
    }

    private fun initialise() {
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Users")
        mAuth = FirebaseAuth.getInstance()
        showFirstName = findViewById<View>(R.id.show_first_name) as TextView
        showLastName = findViewById<View>(R.id.show_last_name) as TextView
        showStatus = findViewById<View>(R.id.show_status) as TextView
        resetStatusBtn = findViewById<View>(R.id.returnBtn) as TextView
        resetStatusBtn!!.setOnClickListener{resetStatus()}
    }

    override fun onStart() {
        super.onStart()
        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)

        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                showFirstName!!.text = snapshot.child("firstName").value as String
                showLastName!!.text = snapshot.child("lastName").value as String
                showStatus!!.text = snapshot.child("status").value as String
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun resetStatus(){
        val mClient = mAuth!!.currentUser
        val mClientReference = mDatabaseReference!!.child(mClient!!.uid)

        mClientReference.child("status").setValue("out of service")
    }
}
