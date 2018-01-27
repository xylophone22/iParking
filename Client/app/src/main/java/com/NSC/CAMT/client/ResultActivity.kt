package com.NSC.CAMT.client

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.zxing.qrcode.encoder.QRCode
import kotlinx.android.synthetic.main.activity_result.*
import org.jetbrains.anko.toast

class ResultActivity : AppCompatActivity() {
    //
    private val tag = "QRCGEN"
    private val REQUEST_PERMISSION = 0xf0

    private var self: MainActivity? = null
    private var snackbar: Snackbar? = null
    private var qrImage: Bitmap? = null

    private var txtQRText: EditText? = null
    private var txtSaveHint: TextView? = null
    private var btnGenerate: Button? = null, private var btnReset:Button? = null
    private var imgResult: ImageView? = null
    private var loader: ProgressBar? = null
    //
    var mAuth: FirebaseAuth? = null
    var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private val TAG: String = "Result Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
//
        self = this

        txtQRText = findViewById(R.id.txtQR) as EditText
        txtSaveHint = findViewById(R.id.txtSaveHint) as TextView
        btnGenerate = findViewById(R.id.btnGenerate) as Button
        btnReset = findViewById(R.id.btnReset) as Button
        imgResult = findViewById(R.id.imgResult) as ImageView
        loader = findViewById(R.id.loader) as ProgressBar




//

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

}
