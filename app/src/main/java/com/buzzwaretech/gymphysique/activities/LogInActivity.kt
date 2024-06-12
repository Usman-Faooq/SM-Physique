package com.buzzwaretech.gymphysique.activities

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import com.buzzwaretech.gymphysique.R
import com.buzzwaretech.gymphysique.models.UserModel
import com.buzzwaretech.gymphysique.classes.Constants
import com.buzzwaretech.gymphysique.databinding.ActivityLogInBinding
import com.google.firebase.messaging.FirebaseMessaging

class LogInActivity : BaseActivity() {

    lateinit var binding : ActivityLogInBinding

    private var token : String = ""

    private var isPasswordVisible: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setView()
        setListener()

    }

    private fun setView() {
        binding.include.titleTV.text = "Sign In"

    }

    private fun setListener() {
        binding.include.backIV.setOnClickListener {
            finish()
        }

        binding.logInTV.setOnClickListener {

            loginUser()

        }

        binding.toggleIV.setOnClickListener {
            togglePasswordVisibility()
        }

        binding.tvSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
        }
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide password
            binding.passwordET.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.toggleIV.setImageResource(R.drawable.icon_show_password)
        } else {
            // Show password
            binding.passwordET.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.toggleIV.setImageResource(R.drawable.icon_hide_password)
        }
        // Move the cursor to the end of the text
        binding.passwordET.setSelection(binding.passwordET.text.length)
        isPasswordVisible = !isPasswordVisible
    }

    private fun loginUser() {
        val email = binding.emailET.text.toString()
        val password = binding.passwordET.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()){
            mDialog.show()
            mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                var id = it.user!!.uid
                FirebaseMessaging.getInstance().token.addOnSuccessListener { t ->
                    token = t.toString()
                    db.collection("Users").document(id).update("token", token)
                }

                db.collection("Users").document(id).get().addOnSuccessListener {doc ->
                    mDialog.dismiss()
                    val userModel = doc.toObject(UserModel::class.java)
                    userModel!!.userId = id
                    Constants.currentUser = userModel
                    val intent = Intent(this, DashBoardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)


                }.addOnFailureListener {
                    mDialog.dismiss()
                    Toast.makeText(this@LogInActivity, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }

            }.addOnFailureListener {
                mDialog.dismiss()
                Toast.makeText(this@LogInActivity, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }


        }else{
            Toast.makeText(this, "Fields Required", Toast.LENGTH_SHORT).show()
        }



    }
}