package com.CAMT.NSC.iparking

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
import kotlinx.android.synthetic.main.activity_result.*
import org.jetbrains.anko.toast


class ResultActivity : AppCompatActivity() {

    val REQUEST_CODE = 1001
    val PERMISSION_REQUEST = 2001

    var mAuth: FirebaseAuth? = null
    var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private val TAG: String = "Result Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        mAuth = FirebaseAuth.getInstance()

        val user = mAuth!!.currentUser

        result_emailData.text = user!!.email
        result_uidData.text = user.uid

        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val users = firebaseAuth.currentUser
            if (users == null) {
                startActivity(Intent(this@ResultActivity, LoginActivity::class.java))
                finish()
            }
        }

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


    override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener { mAuthListener }
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            mAuth!!.removeAuthStateListener { mAuthListener }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true)
        }
        return super.onKeyDown(keyCode, event)
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
