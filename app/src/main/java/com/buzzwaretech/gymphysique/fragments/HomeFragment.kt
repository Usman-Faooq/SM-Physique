package com.buzzwaretech.gymphysique.fragments

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.buzzwaretech.gymphysique.R
import com.buzzwaretech.gymphysique.activities.DetailActivity
import com.buzzwaretech.gymphysique.activities.EditProfileActivity
import com.buzzwaretech.gymphysique.activities.ImageViewActivity
import com.buzzwaretech.gymphysique.activities.NotificationActivity
import com.buzzwaretech.gymphysique.activities.VideViewActivity
import com.buzzwaretech.gymphysique.adapters.HomeAdapter
import com.buzzwaretech.gymphysique.classes.Constants
import com.buzzwaretech.gymphysique.databinding.FragmentHomeBinding
import com.buzzwaretech.gymphysique.models.PostModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : BaseFragment(), HomeAdapter.OnItemClickListener {

    lateinit var binding : FragmentHomeBinding
    private lateinit var fragmentContext: Context
    private val postList : ArrayList<PostModel> = arrayListOf()

    var videoUri : Uri? = null
    var imageUri : Uri? = null

    companion object {
        private const val REQUEST_IMAGE = 100
    }

    private val videoPicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedVideoUri: Uri? = result.data?.data
            videoUri = selectedVideoUri!!
            imageUri = null
        }
    }

    private val takeVideo = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val capturedVideoUri: Uri? = result.data?.data
            videoUri = capturedVideoUri!!
            imageUri = null
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)

        //getPosts()
        setView()
        setListener()
        setAdapter()

        return binding.root
    }

    private fun setView() {
        binding.layout.userNameTV.text = Constants.currentUser.name


        Glide.with(fragmentContext)
            .load(Constants.currentUser.imageUrl)
            .placeholder(R.drawable.holder_profile)
            .into(binding.layout.roundedImageView)

        Glide.with(fragmentContext)
            .load(Constants.currentUser.imageUrl)
            .placeholder(R.drawable.holder_profile)
            .into(binding.layout.roundedImageView2)

        Glide.with(fragmentContext)
            .load("")
            .placeholder(R.drawable.dummy_home_banner)
            .into(binding.layout.bannerIV)
    }

    private fun getPosts() {
        val following = Constants.currentUser.following.keys

        val followingKeysList: ArrayList<String> = ArrayList(following)
        followingKeysList.add(Constants.currentUser.userId)

        mDialog.show()
        Log.d("LOGGER", "Following List: $following")
        db.collection("Posts")
            .whereIn("userId", followingKeysList)
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
                Log.d("LOGGER", "Error getting posts: ${e.message}")
                // Handle failure
            }
    }


    private fun setListener() {

        binding.layout.cvNotification.setOnClickListener {
            val intent = Intent(fragmentContext, NotificationActivity::class.java)
            startActivity(intent)
            requireActivity().overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
        }

        binding.layout.attachIV.setOnClickListener {

            val options = arrayOf("Image Picker", "Pick Video", "Capture Video")

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Select Video Source")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> {
                            ImagePicker.with(this)
                                .crop()
                                .compress(1024)
                                .maxResultSize(1080, 1080)
                                .start(REQUEST_IMAGE)
                        }
                        1 -> {
                            val videoPickerIntent = Intent(Intent.ACTION_PICK)
                            videoPickerIntent.type = "video/*"
                            videoPicker.launch(videoPickerIntent)
                        }
                        2 -> {
                            val captureVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                            takeVideo.launch(captureVideoIntent)
                        }
                    }
                }

            builder.create().show()

        }

        binding.layout.postWorkTV.setOnClickListener {

            var description = binding.layout.descriptionET.text.toString()
            if (videoUri != null || imageUri != null){
                if (description.isNotEmpty()){

                    if (imageUri != null) {
                        uploadVideo(imageUri!!, "image")
                    } else {
                        uploadVideo(videoUri!!, "video")
                    }

                }else{
                    Toast.makeText(fragmentContext, "Description Required", Toast.LENGTH_SHORT).show()
                }

            }else{
                Toast.makeText(fragmentContext, "Media Required", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun uploadVideo(mediaUri: Uri, mediaType : String) {
        mDialog.show()
        var description = binding.layout.descriptionET.text.toString()
        val videoRef = FirebaseStorage.getInstance().reference.child("Users/${Constants.currentUser.userId}/videos/${UUID.randomUUID()}")
        val uploadTask = videoRef.putFile(mediaUri)
        uploadTask.addOnSuccessListener {
            videoRef.downloadUrl.addOnSuccessListener {task ->
                var mediaUrl = task.toString()

                val map = mapOf(
                    "description" to description,
                    "mediaType" to mediaType,
                    "mediaUrl" to mediaUrl,
                    "postDate" to System.currentTimeMillis(),
                    "userId" to Constants.currentUser.userId,
                )

                db.collection("Posts").document()
                    .set(map).addOnSuccessListener {
                        videoUri = null
                        binding.layout.descriptionET.setText("")
                        mDialog.dismiss()
                        Constants.currentUser.videoCount = Constants.currentUser.videoCount + 1
                        db.collection("Users").document(Constants.currentUser.userId).update("videoCount", FieldValue.increment(1))
                        Toast.makeText(fragmentContext, "Upload Success", Toast.LENGTH_SHORT).show()
                        getPosts()

                    }.addOnFailureListener {
                        mDialog.dismiss()
                        Log.d("LOGGER", "Error01: ${it.message}")
                        Toast.makeText(fragmentContext, "Error01: ${it.message}", Toast.LENGTH_SHORT).show()
                    }

            }.addOnFailureListener {
                mDialog.dismiss()
                Log.d("LOGGER", "Error02: ${it.message}")
                Toast.makeText(fragmentContext, "Error02: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            mDialog.dismiss()
            Log.d("LOGGER", "Error03: ${it.message}")
            Toast.makeText(fragmentContext, "Error03: ${it.message}", Toast.LENGTH_SHORT).show()
        }

    }

    private fun setAdapter() {
        mDialog.dismiss()
        if (postList.isNotEmpty()){
            binding.recyclerView.visibility = View.VISIBLE
            binding.recyclerView.layoutManager = LinearLayoutManager(fragmentContext, LinearLayoutManager.VERTICAL, false)
            binding.recyclerView.adapter = HomeAdapter(fragmentContext, postList, this, "home")

        }else{
            binding.recyclerView.visibility = View.INVISIBLE
        }
    }

    override fun onItemClick(model: PostModel) {
        val intent = Intent(fragmentContext, DetailActivity::class.java)
        intent.putExtra("postModel", model)
        startActivity(intent)
        requireActivity().overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
    }

    override fun onPostClick(postModel: PostModel) {
        if (postModel.mediaType == "video"){
            val intent = Intent(fragmentContext, VideViewActivity::class.java)
            intent.putExtra("URL", postModel.mediaUrl)
            startActivity(intent)
            requireActivity().overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)

        }else{
            val intent = Intent(fragmentContext, ImageViewActivity::class.java)
            intent.putExtra("URL", postModel.mediaUrl)
            startActivity(intent)
            requireActivity().overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
        }

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

    override fun onResume() {
        super.onResume()
        Log.d("LOGGER", "On Resume")
        getPosts()
        setView()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE) {
                val uri = data!!.data
                imageUri = uri
                videoUri = null

            }
        }
    }

}