package com.buzzwaretech.gymphysique.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.buzzwaretech.gymphysique.R
import com.buzzwaretech.gymphysique.adapters.FollowersAdapter
import com.buzzwaretech.gymphysique.databinding.ActivityFollowersBinding
import com.buzzwaretech.gymphysique.models.UserModel
import com.google.android.gms.tasks.Tasks

class FollowersActivity : BaseActivity(), FollowersAdapter.OnItemClickListener {

    val binding : ActivityFollowersBinding by lazy {
        ActivityFollowersBinding.inflate(layoutInflater)
    }

    var title = ""
    var userIDList : ArrayList<String> = arrayListOf()

    var userList : ArrayList<UserModel> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        title = intent.getStringExtra("title").toString()
        userIDList = intent.getStringArrayListExtra("userList")!!

        Log.d("LOGGER", "Ids: $userIDList")
        setView()
        getUsers()
        setListener()
    }

    private fun setView() {
        binding.titleTV.text = title
    }

    private fun getUsers() {
        mDialog.show()
        val tasks = userIDList.map { userId ->
            db.collection("Users").document(userId).get()
                .addOnSuccessListener { doc ->
                    val model = doc.toObject(UserModel::class.java)
                    model?.let {
                        it.userId = doc.id
                        userList.add(it)
                    }
                }.addOnFailureListener {
                    Log.d("LOGGER", "Exception: ${it.message}")
                }
        }

        // Wait for all tasks to complete
        Tasks.whenAllComplete(tasks).addOnCompleteListener {
            mDialog.dismiss()
            setAdapter()
        }
    }


    private fun setAdapter() {
        Log.d("LOGGER", "userList Size: ${userList.size}")
        if (userList.isNotEmpty()){
            binding.emptyTV.visibility = View.GONE
            binding.followerRV.layoutManager = LinearLayoutManager(this)
            binding.followerRV.adapter = FollowersAdapter(this, userList, this, title)
        }else{
            binding.emptyTV.visibility = View.VISIBLE
        }


    }

    private fun setListener() {
        binding.backIV.setOnClickListener { finish() }
    }

    override fun onItemClick(userID: String) {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra("userID", userID)
        startActivity(intent)
        overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
    }
}