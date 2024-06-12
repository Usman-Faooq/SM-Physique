package com.buzzwaretech.gymphysique.fragments

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

open class BaseFragment : Fragment() {

    lateinit var mAuth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var mDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // You can add common functionality here that you want to execute in all fragments

        // Initialize Firebase
        FirebaseApp.initializeApp(requireContext())

        // Initialize ProgressDialog
        mDialog = ProgressDialog(requireContext())
        mDialog.setMessage("Please wait...")
        mDialog.setCancelable(false)

        // Initialize Firestore and FirebaseAuth
        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
    }

    // You can add other common methods or properties here
}
