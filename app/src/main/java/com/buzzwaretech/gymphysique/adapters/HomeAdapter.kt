package com.buzzwaretech.gymphysique.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.buzzwaretech.gymphysique.R
import com.buzzwaretech.gymphysique.classes.Constants
import com.buzzwaretech.gymphysique.databinding.ItemDesignPostLayoutBinding
import com.buzzwaretech.gymphysique.models.PostModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class HomeAdapter(
    val context: Context,
    private val list: ArrayList<PostModel>,
    private val listener: OnItemClickListener, private val actType : String) :
    RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    private var filteredList: ArrayList<PostModel> = ArrayList(list)

    interface OnItemClickListener {
        fun onItemClick(itemName: PostModel)
        fun onPostClick(itemName: PostModel)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemDesignPostLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val model = filteredList[position]

        if (model.userId != Constants.currentUser.userId){

            FirebaseFirestore.getInstance().collection("Users")
                .document(model.userId).get().addOnSuccessListener {

                    val userName = it.getString("name")
                    val userImage = it.getString("imageUrl")

                    model.userName = userName.toString()
                    holder.binding.nameUser.text = userName
                    Glide.with(context)
                        .load(userImage)
                        .placeholder(R.drawable.holder_profile)
                        .into(holder.binding.imgUser)

                }

        }else{
            model.userName = Constants.currentUser.name
            holder.binding.nameUser.text = Constants.currentUser.name
            Glide.with(context)
                .load(Constants.currentUser.imageUrl)
                .placeholder(R.drawable.holder_profile)
                .into(holder.binding.imgUser)
        }

        if (model.mediaType == "video"){
            holder.binding.imgPlayPost.visibility = View.VISIBLE
        }else{
            holder.binding.imgPlayPost.visibility = View.INVISIBLE
        }

        if (actType == "myProfile"){
            holder.binding.deleteIV.visibility = View.VISIBLE
        }else{
            holder.binding.deleteIV.visibility = View.INVISIBLE
        }


        holder.binding.postText.text = model.description
        holder.binding.timePost.text = Constants.getTimeAgo(model.postDate)
        holder.binding.likeCounterTV.text = model.likes.size.toString()
        holder.binding.commentCounterTV.text = model.commentCount.toString()
        Glide.with(context)
            .load(model.mediaUrl)
            .placeholder(R.drawable.holder_post)
            .into(holder.binding.postImage)


        holder.binding.root.setOnClickListener {
            listener.onItemClick(filteredList[position])
        }

        holder.binding.postImage.setOnClickListener {
            listener.onPostClick(filteredList[position])
        }

        holder.binding.likeLayout.setOnClickListener {
            if (model.likes.containsKey(Constants.currentUser.userId)){
                FirebaseFirestore.getInstance().collection("Posts").document(model.postId)
                    .update("likes.${Constants.currentUser.userId}", FieldValue.delete())
                model.likes.remove(Constants.currentUser.userId)
            }else{
                FirebaseFirestore.getInstance().collection("Posts").document(model.postId)
                    .update("likes.${Constants.currentUser.userId}", "like")
                model.likes[Constants.currentUser.userId] = "like"
            }
            holder.binding.likeCounterTV.text = model.likes.size.toString()
        }

        holder.binding.deleteIV.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Confirmation")
                .setMessage("Are you sure you want to delete?")
                .setPositiveButton("Yes") { dialog, _ ->
                    FirebaseFirestore.getInstance().collection("Posts")
                        .document(model.postId).delete().addOnSuccessListener {
                            Constants.currentUser.videoCount--
                            FirebaseFirestore.getInstance().collection("Users")
                                .document(Constants.currentUser.userId).update("videoCount", FieldValue.increment(-1))
                            Toast.makeText(context, "Delete Success", Toast.LENGTH_SHORT).show()
                            list.removeAt(position)
                            filteredList.removeAt(position)
                            notifyDataSetChanged()
                        }.addOnFailureListener {
                            Toast.makeText(context, "Delete Error: ${it.message}", Toast.LENGTH_SHORT).show()
                        }

                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }


    }

    inner class ViewHolder(val binding: ItemDesignPostLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    fun filter(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(list)  // Add all items from the original list
        } else {
            val filtered = list.filter { postModel ->
                postModel.description.contains(query, ignoreCase = true) || postModel.userName.contains(query, ignoreCase = true)
            }
            filteredList.addAll(filtered)
        }
        notifyDataSetChanged()
    }

}
