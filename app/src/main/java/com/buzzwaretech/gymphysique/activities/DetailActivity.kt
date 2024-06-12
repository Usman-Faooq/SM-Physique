package com.buzzwaretech.gymphysique.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.buzzwaretech.gymphysique.R
import com.buzzwaretech.gymphysique.adapters.CommentAdapter
import com.buzzwaretech.gymphysique.classes.Constants
import com.buzzwaretech.gymphysique.databinding.ActivityDetailBinding
import com.buzzwaretech.gymphysique.models.CommentModel
import com.buzzwaretech.gymphysique.models.PostModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class DetailActivity : BaseActivity() {

    lateinit var binding : ActivityDetailBinding

    lateinit var model : PostModel

    private val commentList : ArrayList<CommentModel> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        model = intent.getParcelableExtra("postModel")!!

        setView()
        getComment()
        setListener()
    }

    private fun setView() {

        binding.userNameTV.text = model.description
        binding.postTimeTV.text = Constants.getTimeAgo(model.postDate)
        binding.descriptionTV.text = model.description
        binding.likeCounterTV.text = model.likes.size.toString()
        binding.commentCounterTV.text = model.commentCount.toString()

        if (model.mediaType == "video"){
            binding.imgPlayPost.visibility = View.VISIBLE
        }else{
            binding.imgPlayPost.visibility = View.INVISIBLE
        }

        Glide.with(this)
            .load(model.mediaUrl)
            .placeholder(R.drawable.holder_post)
            .into(binding.postImage)

        if (model.userId != Constants.currentUser.userId){

            FirebaseFirestore.getInstance().collection("Users")
                .document(model.userId).get().addOnSuccessListener {

                    val userName = it.getString("name")
                    val userImage = it.getString("imageUrl")

                    binding.userNameTV.text = userName
                    Glide.with(this)
                        .load(userImage)
                        .placeholder(R.drawable.holder_profile)
                        .into(binding.userIV)

                }

        }else{
            binding.userNameTV.text = Constants.currentUser.name
            Glide.with(this)
                .load(Constants.currentUser.imageUrl)
                .placeholder(R.drawable.holder_profile)
                .into(binding.userIV)
        }

    }

    private fun getComment() {

        db.collection("Posts").document(model.postId).collection("Comments")
            .orderBy("timeStamp", Query.Direction.DESCENDING).addSnapshotListener { value, error ->
                if (error != null){
                    Log.d("LOGGER", "Comment Exception: ${error.message}")
                    return@addSnapshotListener
                }

                commentList.clear()
                value!!.forEach {
                    val comment = it.toObject(CommentModel::class.java)
                    comment.commentId = it.id
                    commentList.add(comment)
                }

                binding.commentRV.layoutManager = LinearLayoutManager(this)
                binding.commentRV.adapter = CommentAdapter(this, commentList)

            }

    }

    private fun setListener() {
        binding.backIV.setOnClickListener {
            finish()
        }

        binding.imgPlayPost.setOnClickListener {
            val intent = Intent(this, VideViewActivity::class.java)
            intent.putExtra("URL", model.mediaUrl)
            startActivity(intent)
            overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
        }

        binding.postImage.setOnClickListener {
            if (model.mediaType == "image"){
                val intent = Intent(this, ImageViewActivity::class.java)
                intent.putExtra("URL", model.mediaUrl)
                startActivity(intent)
                overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
            }
        }


        binding.userIV.setOnClickListener {
            if (model.userId != Constants.currentUser.userId){
                val intent = Intent(this, ProfileActivity::class.java)
                intent.putExtra("userID", model.userId)
                startActivity(intent)
                overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
            }
        }

        binding.linearLayout.setOnClickListener {
            if (model.userId != Constants.currentUser.userId){
                val intent = Intent(this, ProfileActivity::class.java)
                intent.putExtra("userID", model.userId)
                startActivity(intent)
                overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
            }
        }

        binding.attacIV.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start(100)
        }

        binding.sendIV.setOnClickListener {
            val commentText = binding.commentET.text.toString().trim()
            if (commentText.isNotEmpty()) {
                addComment(commentText, "text")
                hideKeyboard(it)
            } else {
                // Handle the case when the text is empty
                Toast.makeText(this@DetailActivity, "Please enter a comment", Toast.LENGTH_SHORT).show()
            }
        }

        binding.commentET.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val commentText = binding.commentET.text.toString().trim()
                if (commentText.isNotEmpty()) {
                    hideKeyboard(binding.commentET)
                    addComment(commentText, "text")
                } else {
                    // Handle the case when the text is empty
                    Toast.makeText(this@DetailActivity, "Please enter a comment", Toast.LENGTH_SHORT).show()
                }
                true
            } else {
                false
            }
        }

        binding.likeLayout.setOnClickListener {
            if (model.likes.containsKey(Constants.currentUser.userId)){
                db.collection("Posts").document(model.postId).update("likes.${Constants.currentUser.userId}", FieldValue.delete())
                model.likes.remove(Constants.currentUser.userId)
                binding.likeCounterTV.text = model.likes.size.toString()
            }else{
                db.collection("Posts").document(model.postId).update("likes.${Constants.currentUser.userId}", "like")
                model.likes[Constants.currentUser.userId] = "like"
                binding.likeCounterTV.text = model.likes.size.toString()
            }
        }
    }

    private fun addComment(content: String, type: String) {
        mDialog.show()
        val map = mapOf(
            "content" to content,
            "fromID" to Constants.currentUser.userId,
            "timeStamp" to System.currentTimeMillis(),
            "type" to type,
        )

        Log.d("LOGGER", "POstId : ${model.postId}")
        db.collection("Posts").document(model.postId).collection("Comments").document().set(map)
            .addOnSuccessListener {
                mDialog.dismiss()
                db.collection("Posts").document(model.postId).update("commentCount", FieldValue.increment(1))
                model.commentCount++
                binding.commentCounterTV.text = model.commentCount.toString()
                binding.commentET.setText("")
            }.addOnFailureListener {
                mDialog.dismiss()
                Log.d("LOGGER", "Error00: ${it.message}")
                Toast.makeText(this@DetailActivity, "Error000: ${it.message}", Toast.LENGTH_SHORT).show()
            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 100) {
                val uri = data!!.data
                uploadImage(uri)

            }
        }
    }

    private fun uploadImage(uri: Uri?) {
        mDialog.show()
        var storageRef = FirebaseStorage.getInstance().reference.child("Posts/${model.postId}/comments/${Constants.currentUser.userId}/${UUID.randomUUID()}.jpg")
        var uploadTask = storageRef.putFile(uri!!)
        uploadTask.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener {task ->

                var imageUrl = task.toString()
                addComment(imageUrl, "image")

            }.addOnFailureListener {
                mDialog.dismiss()
                Log.d("LOGGER", "Error00: ${it.message}")
                Toast.makeText(this@DetailActivity, "Error00: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            mDialog.dismiss()
            Log.d("LOGGER", "Error00: ${it.message}")
            Toast.makeText(this@DetailActivity, "Error01: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hideKeyboard(view: View) {
        try{
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }catch(e : Exception){

        }
    }
}