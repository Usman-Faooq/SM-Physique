package com.buzzwaretech.gymphysique.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.buzzwaretech.gymphysique.R
import com.buzzwaretech.gymphysique.activities.*
import com.buzzwaretech.gymphysique.adapters.HomeAdapter
import com.buzzwaretech.gymphysique.adapters.HomeGridAdapter
import com.buzzwaretech.gymphysique.classes.Constants
import com.buzzwaretech.gymphysique.databinding.FragmentProfileBinding
import com.buzzwaretech.gymphysique.models.PostModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query


class ProfileFragment: BaseFragment(), HomeGridAdapter.OnItemClickListener {

    lateinit var binding : FragmentProfileBinding
    private lateinit var fragmentContext: Context

    private val postList : ArrayList<PostModel> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)

        getMyPosts()
        setView()
        setListener()
        setAdapter()

        return binding.root
    }

    /*private fun getMyPosts() {
        db.collection("Posts")
            .whereEqualTo("userId", Constants.currentUser.userId)
            .orderBy("postDate", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null){
                    Log.d("LOGGER", "Exception: ${error.message}")
                    return@addSnapshotListener
                }

                postList.clear()
                value!!.forEach {

                    val model = it.toObject(PostModel::class.java)
                    model.postId = it.id
                    postList.add(model)

                }

                setAdapter()
        }
    }*/

    private fun getMyPosts() {
        mDialog.show()
        db.collection("Posts")
            .whereEqualTo("userId", Constants.currentUser.userId)
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
                Log.d("LOGGER", "Error getting my posts: ${e.message}")
                // Handle failure
            }
    }


    private fun setView(){
        Constants.currentUser.following.remove(Constants.currentUser.userId)
        Constants.currentUser.followers.remove(Constants.currentUser.userId)
        binding.txtUserName.text = Constants.currentUser.name
        binding.followerCountTV.text = Constants.currentUser.followers.size.toString()
        binding.videoCountTV.text = Constants.currentUser.videoCount.toString()
        binding.followingCountTV.text = Constants.currentUser.following.size.toString()
        binding.bioTV.text = Constants.currentUser.bio
        Glide.with(this)
            .load(Constants.currentUser.imageUrl)
            .placeholder(R.drawable.holder_profile)
            .into(binding.userImg)

        Glide.with(this)
            .load(Constants.currentUser.bannerUrl)
            .placeholder(R.drawable.dummy_banner_holder)
            .into(binding.bannerIV)
    }

    private fun setListener() {
        binding.cvNotification.setOnClickListener {
            val intent = Intent(fragmentContext, EditProfileActivity::class.java)
            startActivity(intent)
            requireActivity().overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
        }

        binding.cvLogout.setOnClickListener {
            db.collection("Users").document(Constants.currentUser.userId).update("token", "")
            mAuth.signOut()
            val intent = Intent(fragmentContext, StartUpActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }

        binding.cvImg.setOnClickListener {
            if (Constants.currentUser.imageUrl.isNotEmpty()){
                val intent = Intent(fragmentContext, ImageViewActivity::class.java)
                intent.putExtra("URL", Constants.currentUser.imageUrl)
                startActivity(intent)
                requireActivity().overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
            }
        }

        binding.bannerIV.setOnClickListener {
            if (Constants.currentUser.bannerUrl.isNotEmpty()){
                val intent = Intent(fragmentContext, ImageViewActivity::class.java)
                intent.putExtra("URL", Constants.currentUser.bannerUrl)
                startActivity(intent)
                requireActivity().overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
            }
        }

        binding.followerLayout.setOnClickListener {
            val followerUserKeys : ArrayList<String> = ArrayList(Constants.currentUser.followers.keys)
            val intent = Intent(fragmentContext, FollowersActivity::class.java)
            intent.putExtra("title", "Followers")
            intent.putStringArrayListExtra("userList", followerUserKeys)
            startActivity(intent)
            requireActivity().overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
        }

        binding.followingLayout.setOnClickListener {
            val followingUserKeys : ArrayList<String> = ArrayList(Constants.currentUser.following.keys)
            val intent = Intent(fragmentContext, FollowersActivity::class.java)
            intent.putExtra("title", "Following")
            intent.putStringArrayListExtra("userList", followingUserKeys)
            startActivity(intent)
            requireActivity().overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
        }

    }

    private fun setAdapter() {
        mDialog.dismiss()
        //binding.rvPosts.layoutManager = LinearLayoutManager(fragmentContext)
        binding.rvPosts.layoutManager = GridLayoutManager(fragmentContext, 3)
        binding.rvPosts.adapter = HomeGridAdapter(fragmentContext, postList, this, "myProfile")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }


    override fun onItemClick(model: PostModel) {
        val intent = Intent(fragmentContext, DetailActivity::class.java)
        intent.putExtra("postModel", model)
        startActivity(intent)
        requireActivity().overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
    }

    override fun onResume() {
        super.onResume()
        setView()
    }

}