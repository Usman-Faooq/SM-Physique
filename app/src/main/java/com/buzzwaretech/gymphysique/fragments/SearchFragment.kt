package com.buzzwaretech.gymphysique.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.buzzwaretech.gymphysique.R
import com.buzzwaretech.gymphysique.activities.DetailActivity
import com.buzzwaretech.gymphysique.activities.NotificationActivity
import com.buzzwaretech.gymphysique.adapters.HomeAdapter
import com.buzzwaretech.gymphysique.adapters.HomeGridAdapter
import com.buzzwaretech.gymphysique.classes.Constants
import com.buzzwaretech.gymphysique.databinding.FragmentSearchBinding
import com.buzzwaretech.gymphysique.models.PostModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query


class SearchFragment : BaseFragment(), HomeGridAdapter.OnItemClickListener {

    lateinit var binding : FragmentSearchBinding
    private lateinit var fragmentContext: Context
    val postList : ArrayList<PostModel> = arrayListOf()

    private lateinit var adapter: HomeGridAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater)

        //binding.layout.titleTV.text = "Search"

        setAdapter()
        getPosts()
        setView()
        setListener()

        return binding.root
    }

    private fun setView() {

        /*Glide.with(fragmentContext)
            .load("")
            .placeholder(R.drawable.dummy_home_banner)
            .into(binding.layout.bannerIV)*/

        binding.searchET.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val query = p0.toString().trim()

                if (::adapter.isInitialized) {
                    adapter.filter(query)
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

    private fun getPosts() {
        mDialog.show()
        db.collection("Posts")
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
        binding.cvNotification.setOnClickListener {
            val intent = Intent(fragmentContext, NotificationActivity::class.java)
            startActivity(intent)
            requireActivity().overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
        }
    }


    private fun setAdapter() {
        if (mDialog.isShowing){
            mDialog.dismiss()
        }

        adapter = HomeGridAdapter(fragmentContext, postList, this, "otherProfile")

        binding.recyclerView.layoutManager = GridLayoutManager(fragmentContext, 3)
        binding.recyclerView.adapter = adapter
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


}