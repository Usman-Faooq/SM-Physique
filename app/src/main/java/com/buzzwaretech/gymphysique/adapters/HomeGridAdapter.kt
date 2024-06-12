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
import com.buzzwaretech.gymphysique.databinding.ItemDesignPostGridLayoutBinding
import com.buzzwaretech.gymphysique.databinding.ItemDesignPostLayoutBinding
import com.buzzwaretech.gymphysique.models.PostModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class HomeGridAdapter(
    val context: Context,
    private val list: ArrayList<PostModel>,
    private val listener: OnItemClickListener, private val actType : String) :
    RecyclerView.Adapter<HomeGridAdapter.ViewHolder>() {

    private var filteredList: ArrayList<PostModel> = ArrayList(list)

    interface OnItemClickListener {
        fun onItemClick(itemName: PostModel)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemDesignPostGridLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val model = filteredList[position]


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
        Glide.with(context)
            .load(model.mediaUrl)
            .placeholder(R.drawable.holder_post)
            .into(holder.binding.postImage)


        holder.binding.root.setOnClickListener {
            listener.onItemClick(filteredList[position])
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

    inner class ViewHolder(val binding: ItemDesignPostGridLayoutBinding) : RecyclerView.ViewHolder(binding.root)

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
