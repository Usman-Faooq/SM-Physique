package com.buzzwaretech.gymphysique.activities

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Toast
import com.buzzwaretech.gymphysique.R
import com.buzzwaretech.gymphysique.models.UserModel
import com.buzzwaretech.gymphysique.classes.Constants
import com.buzzwaretech.gymphysique.databinding.ActivitySignUpBinding
import com.google.firebase.messaging.FirebaseMessaging

class SignUpActivity : BaseActivity() {

    lateinit var binding : ActivitySignUpBinding


    private var token : String = ""

    private var isPasswordVisible: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setView()
        setListener()
    }

    private fun setView() {
        binding.include.titleTV.text = "Sign Up"
    }

    private fun setListener() {
        binding.include.backIV.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.signUpTV.setOnClickListener {

            signUpUser()

        }

        binding.toggleIV.setOnClickListener {
            togglePasswordVisibility()
        }

        binding.tvSignIn.setOnClickListener {
            val intent = Intent(this, LogInActivity::class.java)
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

    private fun signUpUser() {
        val name = binding.fullNameET.text.toString()
        val email = binding.emailET.text.toString()
        val phone = binding.phoneET.text.toString()
        val password = binding.passwordET.text.toString()

        if (name.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty() && password.isNotEmpty()){
            mDialog.show()
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    var id = it.user!!.uid
                    FirebaseMessaging.getInstance().token.addOnSuccessListener { t ->
                        token = t.toString()

                        val map = mapOf(
                            "name" to name,
                            "email" to email,
                            "phone" to phone,
                            "password" to password,
                            "token" to token,
                            "userDate" to System.currentTimeMillis(),
                            "deviceType" to "Android",
                        )

                        db.collection("Users").document(id).set(map)
                            .addOnSuccessListener {
                                mDialog.dismiss()
                                val model = UserModel(id, name, email, phone, "", password, "", "", token, System.currentTimeMillis(), "Android", "")
                                Constants.currentUser = model

                                val intent = Intent(this, DashBoardActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)

                            }.addOnFailureListener {
                                mDialog.dismiss()
                                Log.d("LOGGER", "Error01: ${it.message}")
                                Toast.makeText(this@SignUpActivity, "Error02: ${it.message}", Toast.LENGTH_SHORT).show()
                            }

                    }.addOnFailureListener {
                        mDialog.dismiss()
                        Log.d("LOGGER", "Error01: ${it.message}")
                        Toast.makeText(this@SignUpActivity, "Error01: ${it.message}", Toast.LENGTH_SHORT).show()
                    }


                }.addOnFailureListener {
                    mDialog.dismiss()
                    Log.d("LOGGER", "Error01: ${it.message}")
                    Toast.makeText(this@SignUpActivity, "Error03: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }else{
            Toast.makeText(this, "Fields Required", Toast.LENGTH_SHORT).show()
        }

    }
}