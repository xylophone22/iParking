package com.camt.nsc.iParking.client

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_gen_qr.*
import net.glxn.qrgen.android.QRCode

class GenQRActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    private val TAG: String = "GenQR Activity"


    private var mButtonCreate: Button? = null
    private var mImagePreview: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gen_qr)

        mAuth = FirebaseAuth.getInstance()

        val user = mAuth!!.currentUser


        mButtonCreate = findViewById(R.id.buttonCreate) as Button
        mImagePreview = findViewById(R.id.imagePreview) as ImageView

        (mButtonCreate as Button).setOnClickListener {

            val user = user!!.uid

            if (user.isEmpty()) {
                Toast.makeText(this, getString(R.string.hint_enter_text_to_create_barcode),
                        Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val bitmap = QRCode.from(user).withSize(500, 500).bitmap()
            (mImagePreview as ImageView).setImageBitmap(bitmap)
            hideKeyboard()
        }
        btnBack.setOnClickListener { startActivity(Intent(this@GenQRActivity, ResultActivity::class.java)) }
    }

    fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}
