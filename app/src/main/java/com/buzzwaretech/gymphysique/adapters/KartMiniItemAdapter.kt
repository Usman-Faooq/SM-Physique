package com.buzzwaretech.gymphysique.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.buzzwaretech.gymphysique.R
import com.buzzwaretech.gymphysique.databinding.ItemDesignKartMiniItemBinding
import com.buzzwaretech.gymphysique.databinding.ItemDesignKartViewBinding
import com.buzzwaretech.gymphysique.models.KartModel

class KartMiniItemAdapter(val context: Context, private val list: List<String>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<KartMiniItemAdapter.ViewHolder>() {


    interface OnItemClickListener {
        fun onItemClick(selectedImage : String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemDesignKartMiniItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Glide.with(context)
            .load(list[position])
            .placeholder(R.drawable.holder_post)
            .into(holder.binding.kartIV)

        holder.binding.kartIV.setOnClickListener {
            listener.onItemClick(list[position])
        }

    }

    inner class ViewHolder(val binding: ItemDesignKartMiniItemBinding) : RecyclerView.ViewHolder(binding.root)
}
