package com.buzzwaretech.gymphysique.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.buzzwaretech.gymphysique.R
import com.buzzwaretech.gymphysique.classes.Constants
import com.buzzwaretech.gymphysique.databinding.ItemDesignFollowersLayoutBinding
import com.buzzwaretech.gymphysique.databinding.ItemNotificationBinding
import com.buzzwaretech.gymphysique.models.UserModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class FollowersAdapter(val context: Context, val list: List<UserModel>, private val listener: OnItemClickListener, val type : String) :
    RecyclerView.Adapter<FollowersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemDesignFollowersLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return list.size//list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val model = list[position]

        Glide.with(context)
            .load(model.imageUrl)
            .placeholder(R.drawable.holder_profile)
            .into(holder.binding.profileIV)

        holder.binding.userNameTV.text = model.name

        if (type == "Followers"){
            if (Constants.currentUser.following.containsKey(model.userId)){
                holder.binding.btnFollow.text = "UnFollow"
            }else{
                holder.binding.btnFollow.text = "Follow Back"
            }
        }else{
            holder.binding.btnFollow.text = "UnFollow"
        }

        holder.binding.btnFollow.setOnClickListener {
            if (Constants.currentUser.following.containsKey(model.userId)){
                FirebaseFirestore.getInstance().collection("Users").document(Constants.currentUser.userId).update("following.${model.userId}", FieldValue.delete())
                FirebaseFirestore.getInstance().collection("Users").document(model.userId).update("followers.${Constants.currentUser.userId}", FieldValue.delete())
                Constants.currentUser.following.remove(model.userId)
                holder.binding.btnFollow.text = "Follow Back"
            }else{
                FirebaseFirestore.getInstance().collection("Users").document(Constants.currentUser.userId).update("following.${model.userId}", "following")
                FirebaseFirestore.getInstance().collection("Users").document(model.userId).update("followers.${Constants.currentUser.userId}", "follower")
                Constants.currentUser.following[model.userId] = "following"
                holder.binding.btnFollow.text = "UnFollow"
            }
        }

        holder.binding.root.setOnClickListener {
            listener.onItemClick(model.userId)
        }


    }

    inner class ViewHolder(val binding: ItemDesignFollowersLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    interface OnItemClickListener {
        fun onItemClick(userID: String)
    }
}