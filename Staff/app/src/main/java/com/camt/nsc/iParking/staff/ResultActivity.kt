package com.camt.nsc.iParking.staff

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.vision.barcode.Barcode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
//-----------------------------------------------------//
import kotlinx.android.synthetic.main.activity_result.*
import org.jetbrains.anko.toast

//-----------------------------------------------------//
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
    private var tvEmail: TextView? = null
    private var tvUID: TextView? = null

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
    }

    private fun initialise() {
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Users")
        mAuth = FirebaseAuth.getInstance()
        tvFirstName = findViewById<View>(R.id.tv_first_name) as TextView
        tvLastName = findViewById<View>(R.id.tv_last_name) as TextView
        tvEmail = findViewById<View>(R.id.result_emailData) as TextView
        tvUID = findViewById<View>(R.id.result_uidData) as TextView

    }

    override fun onStart() {
        super.onStart()
        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)

        tvEmail!!.text = mUser.email
        tvUID!!.text = mUser.uid

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
                txtResult.setOnClickListener { setClipboard(applicationContext, barcode.displayValue) }
            }
        }
    }

    private fun setClipboard(context: Context, text: String) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.text.ClipboardManager
            clipboard.text = text
        } else {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText("Copied Text", text)
            clipboard.primaryClip = clip
        }
        Toast.makeText(context, "Copied Text", Toast.LENGTH_LONG).show()
    }
}
