package com.camt.nsc.iParking.client

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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private var tranFirstName: EditText? = null
    private var tranLastName: EditText? = null
    private var tranEmail: EditText? = null
    private var tranPassword: EditText? = null
    private var tranTelephone: EditText? = null
    private var createAccountBtn: Button? = null
    private var mProgressBar: ProgressDialog? = null

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null
    private val TAG = "RegisterActivity"

    private var firstName: String? = null
    private var lastName: String? = null
    private var email: String? = null
    private var password: String? = null
    private var status: String? = "out of service"
    private var telephone: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initialise()
        signInBtn.setOnClickListener { startActivity(Intent(this@RegisterActivity, LoginActivity::class.java)) }
    }

    private fun initialise() {
        tranFirstName = findViewById<View>(R.id.registerFirstName) as EditText
        tranLastName = findViewById<View>(R.id.registerLastName) as EditText
        tranEmail = findViewById<View>(R.id.registerEmail) as EditText
        tranPassword = findViewById<View>(R.id.registerPassword) as EditText
        tranTelephone = findViewById<View>(R.id.registerTelephone) as EditText
        createAccountBtn = findViewById<View>(R.id.registerBtn) as Button
        mProgressBar = ProgressDialog(this)
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Users")
        mAuth = FirebaseAuth.getInstance()
        createAccountBtn!!.setOnClickListener { createNewAccount() }
    }

    private fun createNewAccount() {
        firstName = tranFirstName?.text.toString()
        lastName = tranLastName?.text.toString()
        email = tranEmail?.text.toString()
        password = tranPassword?.text.toString()
        telephone = tranTelephone?.text.toString()

        if (!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName)
                && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)
                && !TextUtils.isEmpty(password)) {
        } else {
            Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show()
        }
        mProgressBar!!.setMessage("Registering User...")
        mProgressBar!!.show()

        mAuth!!.createUserWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(this) { task ->
                    mProgressBar!!.hide()
                    if (task.isSuccessful) {
                        Log.d(TAG, "createUserWithEmail:success")
                        val userId = mAuth!!.currentUser!!.uid
                        val currentUserDb = mDatabaseReference!!.child(userId)
                        currentUserDb.child("firstName").setValue(firstName)
                        currentUserDb.child("lastName").setValue(lastName)
                        currentUserDb.child("telephone").setValue(telephone)
                        currentUserDb.child("status").setValue(status)
                        updateUserInfoAndUI()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(this@RegisterActivity, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun updateUserInfoAndUI() {
        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}
