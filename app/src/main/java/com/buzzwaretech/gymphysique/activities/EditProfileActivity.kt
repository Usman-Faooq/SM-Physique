package com.buzzwaretech.gymphysique.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.buzzwaretech.gymphysique.R
import com.buzzwaretech.gymphysique.classes.Constants
import com.buzzwaretech.gymphysique.databinding.ActivityEditProfileBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class EditProfileActivity : BaseActivity() {

    lateinit var binding : ActivityEditProfileBinding

    var imageUri : Uri? = null
    var bannerUri : Uri? = null

    companion object {
        private const val REQUEST_IMAGE = 100
        private const val REQUEST_BANNER_IMAGE = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setView()
        setListener()

    }

    private fun setView() {

        binding.titleTV.text = "Edit Profile"

        Glide.with(this)
            .load(Constants.currentUser.imageUrl)
            .placeholder(R.drawable.holder_profile)
            .into(binding.userImageIV)

        Glide.with(this)
            .load(Constants.currentUser.bannerUrl)
            //.placeholder(R.drawable.holder_profile)
            .into(binding.bannerIV)

        binding.userNameTV.setText(Constants.currentUser.name)
        binding.userEmailTV.text = Constants.currentUser.email
        binding.userPhoneTV.setText(Constants.currentUser.phone)
        binding.bioET.setText(Constants.currentUser.bio)

    }

    private fun setListener() {

        binding.backIV.setOnClickListener {
            finish()
        }

        binding.userImageIV.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start(REQUEST_IMAGE)
        }

        binding.bannerIV.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start(REQUEST_BANNER_IMAGE)
        }

        binding.updateBtn.setOnClickListener {
            val name = binding.userNameTV.text.toString()
            val phone = binding.userPhoneTV.text.toString()
            val bio = binding.bioET.text.toString()

            if (name.isNotEmpty() && phone.isNotEmpty() && bio.isNotEmpty()){
                mDialog.show()
                if (imageUri != null && bannerUri != null) {
                    // Upload both image and banner
                    uploadImageAndBanner(name, phone, bio)
                } else if (imageUri != null) {
                    // Upload only image
                    uploadImage(name, phone, bio)
                } else if (bannerUri != null) {
                    // Upload only banner
                    uploadBanner(name, phone, bio)
                } else {
                    // Update user data without image or banner
                    updateUserData(name, phone, bio, Constants.currentUser.imageUrl, Constants.currentUser.bannerUrl)
                }
            }else{
                Toast.makeText(this@EditProfileActivity, "Field Required", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun uploadImageAndBanner(name: String, phone: String, bio: String) {
        val imageStorageRef = FirebaseStorage.getInstance().reference.child("Users/${Constants.currentUser.userId}/Profile/${UUID.randomUUID()}.jpg")
        val imageUploadTask = imageStorageRef.putFile(imageUri!!)
        val bannerStorageRef = FirebaseStorage.getInstance().reference.child("Users/${Constants.currentUser.userId}/Banner/${UUID.randomUUID()}.jpg")
        val bannerUploadTask = bannerStorageRef.putFile(bannerUri!!)

        imageUploadTask.addOnSuccessListener {
            imageStorageRef.downloadUrl.addOnSuccessListener {task ->

                val imageUrl = task.toString()

                bannerUploadTask.addOnSuccessListener {
                    bannerStorageRef.downloadUrl.addOnSuccessListener {bannerTask ->

                        val bannerUrl = bannerTask.toString()

                        updateUserData(name, phone, bio, imageUrl, bannerUrl)

                    }.addOnFailureListener {
                        mDialog.dismiss()
                        Log.d("LOGGER", "Error000: ${it.message}")
                        Toast.makeText(this@EditProfileActivity, "Error000: ${it.message}", Toast.LENGTH_SHORT).show()
                    }

                }.addOnFailureListener {
                    mDialog.dismiss()
                    Log.d("LOGGER", "Error001: ${it.message}")
                    Toast.makeText(this@EditProfileActivity, "Error001: ${it.message}", Toast.LENGTH_SHORT).show()
                }

            }.addOnFailureListener {
                mDialog.dismiss()
                Log.d("LOGGER", "Error002: ${it.message}")
                Toast.makeText(this@EditProfileActivity, "Error002: ${it.message}", Toast.LENGTH_SHORT).show()
            }

        }.addOnFailureListener {
            mDialog.dismiss()
            Log.d("LOGGER", "Error00: ${it.message}")
            Toast.makeText(this@EditProfileActivity, "Error01: ${it.message}", Toast.LENGTH_SHORT).show()
        }

    }

    private fun uploadImage(name: String, phone: String, bio: String) {
        val storageRef = FirebaseStorage.getInstance().reference.child("Users/${Constants.currentUser.userId}/Profile/${UUID.randomUUID()}.jpg")
        val uploadTask = storageRef.putFile(imageUri!!)
        uploadTask.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener {task ->

                val imageUrl = task.toString()

                updateUserData(name, phone, bio, imageUrl, Constants.currentUser.bannerUrl)

            }.addOnFailureListener {
                mDialog.dismiss()
                Log.d("LOGGER", "Error00: ${it.message}")
                Toast.makeText(this@EditProfileActivity, "Error00: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            mDialog.dismiss()
            Log.d("LOGGER", "Error01: ${it.message}")
            Toast.makeText(this@EditProfileActivity, "Error01: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadBanner(name: String, phone: String, bio: String) {
        var storageRef = FirebaseStorage.getInstance().reference.child("Users/${Constants.currentUser.userId}/Banner/${UUID.randomUUID()}.jpg")
        var uploadTask = storageRef.putFile(bannerUri!!)
        uploadTask.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener {task ->

                var bannerUrl = task.toString()

                updateUserData(name, phone, bio, Constants.currentUser.imageUrl, bannerUrl)

            }.addOnFailureListener {
                mDialog.dismiss()
                Log.d("LOGGER", "Error0: ${it.message}")
                Toast.makeText(this@EditProfileActivity, "Error0: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            mDialog.dismiss()
            Log.d("LOGGER", "Error1: ${it.message}")
            Toast.makeText(this@EditProfileActivity, "Error1: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun updateUserData(name: String, phone: String, bio: String, imageUrl: String, bannerUrl: String) {

        val updates = hashMapOf<String, Any>(
            "name" to name,
            "phone" to phone,
            "bio" to bio,
            "imageUrl" to imageUrl,
            "bannerUrl" to bannerUrl
        )

        FirebaseFirestore.getInstance().collection("Users").document(Constants.currentUser.userId).update(updates)
            .addOnSuccessListener {
                mDialog.dismiss()
                Constants.currentUser.name = name
                Constants.currentUser.phone= phone
                Constants.currentUser.bio= bio
                Constants.currentUser.imageUrl = imageUrl
                Constants.currentUser.bannerUrl = bannerUrl
                Toast.makeText(this, "updated successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                mDialog.dismiss()
                Toast.makeText(this, "Failed to update user: ${e.message}", Toast.LENGTH_SHORT).show()
            }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_IMAGE) {
            val uri = data!!.data
            imageUri = uri
            binding.userImageIV.setImageURI(imageUri)
            binding.bannerIV.setImageURI(bannerUri)
        }else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_BANNER_IMAGE){
            val uri = data!!.data
            bannerUri = uri
            binding.userImageIV.setImageURI(imageUri)
            binding.bannerIV.setImageURI(bannerUri)

        }

        Log.d("LOGGER", "Banner Uri : $bannerUri")
        Log.d("LOGGER", "profile Uri : $imageUri")
    }

}