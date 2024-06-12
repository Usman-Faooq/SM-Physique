package com.buzzwaretech.gymphysique.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buzzwaretech.gymphysique.databinding.ItemCardBinding

class CardAdapter(val context: Context, private val list: ArrayList<String>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<CardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.root.setOnClickListener {
            listener.onItemClick("Item Name", "Item Type")
        }
        holder.binding.cardNumber.text = list[position]

    }

    inner class ViewHolder(val binding: ItemCardBinding) : RecyclerView.ViewHolder(binding.root)

    interface OnItemClickListener {
        fun onItemClick(itemName: String, type: String)
    }
}