package com.buzzwaretech.gymphysique.activities

import android.app.ProgressDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

open class BaseActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var mDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // You can add common functionality here that you want to execute in all activities

        FirebaseApp.initializeApp(this)
        mDialog = ProgressDialog(this)
        mDialog.setMessage("Please wait...")
        mDialog.setCancelable(false)

        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()


    }

    // You can add other common methods or properties here

}