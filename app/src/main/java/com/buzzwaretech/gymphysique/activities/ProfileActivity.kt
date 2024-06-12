package com.buzzwaretech.gymphysique.activities

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.buzzwaretech.gymphysique.R
import com.buzzwaretech.gymphysique.adapters.HomeAdapter
import com.buzzwaretech.gymphysique.adapters.HomeGridAdapter
import com.buzzwaretech.gymphysique.classes.Constants
import com.buzzwaretech.gymphysique.databinding.ActivityProfileBinding
import com.buzzwaretech.gymphysique.models.PostModel
import com.buzzwaretech.gymphysique.models.UserModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class ProfileActivity: BaseActivity(), HomeGridAdapter.OnItemClickListener {

    lateinit var binding : ActivityProfileBinding

    private val postList : ArrayList<PostModel> = arrayListOf()
    private var userId = ""
    private var userProfileIV = ""
    private var userBannerIV = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getStringExtra("userID").toString()

        getUserData()
        getPosts()
        setListener()

    }

    private fun getUserData() {
        db.collection("Users")
            .document(userId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e(TAG, "Error fetching user data: ${exception.message}")
                    return@addSnapshotListener
                }

                if (!isFinishing && !isDestroyed) { // Check if activity is still valid
                    if (snapshot != null && snapshot.exists()) {
                        val model = snapshot.toObject(UserModel::class.java)

                        binding.txtUserName.text = model!!.name
                        binding.followerCountTV.text = model.followers.size.toString()
                        binding.videoCountTV.text = model.videoCount.toString()
                        binding.followingCountTV.text = model.following.size.toString()
                        binding.bioTV.text = model.bio


                        Glide.with(this@ProfileActivity) // Use this@ProfileActivity instead of this
                            .load(model.imageUrl)
                            .placeholder(R.drawable.holder_profile)
                            .into(binding.userImg)

                        Glide.with(this@ProfileActivity)
                            .load(model.bannerUrl)
                            .placeholder(R.drawable.dummy_banner_holder)
                            .into(binding.bannerIV)

                        userProfileIV = model.imageUrl
                        userBannerIV = model.bannerUrl

                        if (model.followers.containsKey(Constants.currentUser.userId)){
                            binding.btnFollow.text = "Following"
                        }else{
                            binding.btnFollow.text = "Follow"
                        }
                    } else {
                        Log.d("LOGGER", "Current data: null")
                    }
                }
            }
    }

    private fun getPosts() {
        mDialog.show()
        db.collection("Posts")
            .whereEqualTo("userId", userId)
            .orderBy("postDate", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                postList.clear()
                for (document in querySnapshot.documents) {
                    val model = document.toObject(PostModel::class.java)
                    model?.postId = document.id
                    model?.let { postList.add(it) }
                }
                setAdapter()
            }
            .addOnFailureListener { e ->
                mDialog.dismiss()
                Log.d("LOGGER", "Error getting posts for user $userId: ${e.message}")
                // Handle failure
            }
    }


    private fun setListener() {
        binding.backIV.setOnClickListener {
            finish()
        }

        binding.btnFollow.setOnClickListener {
            if (binding.btnFollow.text.toString() == "Following"){
                Constants.currentUser.following.remove(userId)
                db.collection("Users").document(userId).update("followers.${Constants.currentUser.userId}", FieldValue.delete())
                db.collection("Users").document(Constants.currentUser.userId).update("following.$userId", FieldValue.delete())
            }else{
                Constants.currentUser.following[userId] = "follower"
                db.collection("Users").document(userId).update("followers.${Constants.currentUser.userId}", "follower")
                db.collection("Users").document(Constants.currentUser.userId).update("following.$userId", "following")
            }

        }

        binding.cvImg.setOnClickListener {
            if (userProfileIV.isNotEmpty()){
                val intent = Intent(this, ImageViewActivity::class.java)
                intent.putExtra("URL", userProfileIV)
                startActivity(intent)
                overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
            }
        }

        binding.bannerIV.setOnClickListener {
            if (userBannerIV.isNotEmpty()){
                val intent = Intent(this, ImageViewActivity::class.java)
                intent.putExtra("URL", userBannerIV)
                startActivity(intent)
                overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
            }
        }

    }

    private fun setAdapter() {
        mDialog.dismiss()
        //binding.rvPosts.layoutManager = LinearLayoutManager(this)
        binding.rvPosts.layoutManager = GridLayoutManager(this, 3)
        binding.rvPosts.adapter = HomeGridAdapter(this, postList, this, "otherProfile")
    }


    override fun onItemClick(model: PostModel) {
        val intent = if (model.mediaType == "video") {
            Intent(this, VideViewActivity::class.java)
        }else{
            Intent(this, ImageViewActivity::class.java)
        }
        intent.putExtra("URL", model.mediaUrl)
        startActivity(intent)
        overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
    }


}