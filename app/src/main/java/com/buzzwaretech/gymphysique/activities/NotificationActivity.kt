package com.buzzwaretech.gymphysique.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.buzzwaretech.gymphysique.adapters.NotificationAdapter
import com.buzzwaretech.gymphysique.databinding.ActivityNotificationsBinding
import com.buzzwaretech.gymphysique.models.NotificationModel
import com.buzzwaretech.gymphysique.models.PostModel

class NotificationActivity : BaseActivity(), NotificationAdapter.OnItemClickListener{

    lateinit var binding : ActivityNotificationsBinding

    val list : ArrayList<NotificationModel> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getNotifications()
        setListener()

    }

    private fun setListener() {
        binding.backIV.setOnClickListener {
            finish()
        }

    }


    private fun getNotifications() {
        mDialog.show()
        db.collection("Notification").get()
            .addOnSuccessListener {

                it.forEach {doc ->
                    val model = doc.toObject(NotificationModel::class.java)
                    model.id = doc.id
                    list.add(model)
                }

                setAdapter()

            }.addOnFailureListener {
                mDialog.dismiss()
            }
    }


    private fun setAdapter() {
        mDialog.dismiss()
        if (list.isNotEmpty()){
            binding.emptyTV.visibility = View.GONE
            binding.rvNotifications.layoutManager = LinearLayoutManager(this)
            binding.rvNotifications.adapter = NotificationAdapter(this, list, this)
        }else{
            binding.emptyTV.visibility = View.VISIBLE
        }

    }

    override fun onItemClick(model: NotificationModel) {
        if (model.type == "follow"){
            val userId = model.extradata.getValue("senderId").toString()
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("userID", userId)
            startActivity(intent)
            overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
        }else{

            mDialog.show()
            val postId = model.extradata.getValue("postid").toString()

            if (postId.isNotEmpty()){
                db.collection("Posts").document(postId)
                    .get().addOnSuccessListener {
                        mDialog.dismiss()
                        if (it.exists()){
                            val postModel = it.toObject(PostModel::class.java)
                            postModel!!.postId = postId
                            val intent = Intent(this, DetailActivity::class.java)
                            intent.putExtra("postModel", postModel)
                            startActivity(intent)
                            overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
                        }else{
                            Toast.makeText(this, "Document Not Exist", Toast.LENGTH_SHORT).show()
                        }


                    }.addOnFailureListener {
                        mDialog.dismiss()
                        Toast.makeText(this, "Fail: ${it.message}", Toast.LENGTH_SHORT).show()
                    }

            }else{
                Toast.makeText(this, "Null Id here", Toast.LENGTH_SHORT).show()
            }

        }

    }

}