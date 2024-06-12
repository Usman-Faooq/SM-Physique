package com.buzzwaretech.gymphysique.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.buzzwaretech.gymphysique.R
import com.buzzwaretech.gymphysique.activities.ImageViewActivity
import com.buzzwaretech.gymphysique.classes.Constants
import com.buzzwaretech.gymphysique.databinding.ItemCardBinding
import com.buzzwaretech.gymphysique.databinding.ItemDesignCommentLayoutBinding
import com.buzzwaretech.gymphysique.models.CommentModel
import com.google.firebase.firestore.FirebaseFirestore

class CommentAdapter(val context: Context, private val list: ArrayList<CommentModel>) :
    RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemDesignCommentLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val model = list[position]

        if (model.fromID != Constants.currentUser.userId){

            FirebaseFirestore.getInstance().collection("Users")
                .document(model.fromID).get().addOnSuccessListener {

                    val userName = it.getString("name")
                    val userImage = it.getString("imageUrl")

                    holder.binding.userNameTV.text = userName
                    Glide.with(context)
                        .load(userImage)
                        .placeholder(R.drawable.holder_profile)
                        .into(holder.binding.userIV)

                }

        }else{
            holder.binding.userNameTV.text = Constants.currentUser.name
            Glide.with(context)
                .load(Constants.currentUser.imageUrl)
                .placeholder(R.drawable.holder_profile)
                .into(holder.binding.userIV)
        }


        if(model.type == "text"){

            holder.binding.commentIV.visibility = View.GONE
            holder.binding.commentTV.visibility = View.VISIBLE
            holder.binding.commentTV.text = model.content

        }else{

            holder.binding.commentIV.visibility = View.VISIBLE
            holder.binding.commentTV.visibility = View.GONE
            Glide.with(context)
                .load(model.content)
                .placeholder(R.drawable.holder_post)
                .into(holder.binding.commentIV)


            holder.binding.commentIV.setOnClickListener {
                val intent = Intent(context, ImageViewActivity::class.java)
                intent.putExtra("URL", model.content)
                context.startActivity(intent)
            }
        }


    }

    inner class ViewHolder(val binding: ItemDesignCommentLayoutBinding) : RecyclerView.ViewHolder(binding.root)

}