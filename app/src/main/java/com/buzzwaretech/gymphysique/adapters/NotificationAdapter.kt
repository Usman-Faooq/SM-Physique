package com.buzzwaretech.gymphysique.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buzzwaretech.gymphysique.databinding.ItemNotificationBinding
import com.buzzwaretech.gymphysique.models.NotificationModel
import com.buzzwaretech.gymphysique.models.PostModel

class NotificationAdapter(val context: Context, val list: List<NotificationModel>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(model: NotificationModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return list.size//list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]

        holder.binding.titleTV.text = if (model.type == "postlike") {
            "Post Like"
        } else if (model.type == "postcomments") {
            "New Comment"
        } else {
            "New Follower"
        }
        holder.binding.detailTV.text = model.content

        holder.binding.root.setOnClickListener {
            listener.onItemClick(model)
        }

    }

    inner class ViewHolder(val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root)

}