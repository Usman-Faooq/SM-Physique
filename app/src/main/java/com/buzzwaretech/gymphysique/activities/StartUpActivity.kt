package com.buzzwaretech.gymphysique.activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import com.buzzwaretech.gymphysique.classes.Constants
import com.buzzwaretech.gymphysique.databinding.ActivityStartUpBinding
import com.buzzwaretech.gymphysique.models.UserModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class StartUpActivity : AppCompatActivity() {

    lateinit var binding : ActivityStartUpBinding

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        FirebaseApp.initializeApp(this)
        mDialog = ProgressDialog(this)
        mDialog.setMessage("Please wait...")
        mDialog.setCancelable(false)

        mAuth = FirebaseAuth.getInstance()

        setListener()
        checkUser()

    }

    private fun checkUser() {
        if (mAuth.currentUser != null){
            mDialog.show()
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            FirebaseFirestore.getInstance().collection("Users")
                .document(userId).get().addOnSuccessListener { task ->
                    mDialog.dismiss()

                    FirebaseMessaging.getInstance().token.addOnSuccessListener { t->
                        val token = t.toString()
                        FirebaseFirestore.getInstance().collection("Users").document(userId).update("token", token);
                    }

                    var user = task.toObject(UserModel::class.java)
                    Constants.currentUser = user!!
                    Constants.currentUser.userId = task.id

                    val intent = Intent(this, DashBoardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)

                }.addOnFailureListener {
                    mDialog.dismiss()
                    Log.d("LOGGER", "Error2: ${it.message}")
                }

        }
    }

    private fun setListener() {

        binding.startTV.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
        }

        binding.singInTV.setOnClickListener {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
        }

    }
}