package com.buzzwaretech.gymphysique.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.buzzwaretech.gymphysique.R
import com.buzzwaretech.gymphysique.databinding.ItemDesignKartCategoryBinding
import com.buzzwaretech.gymphysique.databinding.ItemDesignKartViewBinding
import com.buzzwaretech.gymphysique.models.KartCategoryModel
import com.buzzwaretech.gymphysique.models.KartModel
import com.buzzwaretech.gymphysique.models.PostModel

class KartCategoryAdapter(val context: Context, private val list: List<KartCategoryModel>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<KartCategoryAdapter.ViewHolder>() {


    interface OnItemClickListener {
        fun onItemClick(model : KartCategoryModel)
    }

    private var filteredList: ArrayList<KartCategoryModel> = ArrayList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemDesignKartCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val model = filteredList[position]

        holder.binding.nameTV.text = model.name
        Glide.with(context)
            .load(model.icon)
            .placeholder(R.drawable.equipment_holder)
            .into(holder.binding.kartIV)

        holder.binding.root.setOnClickListener {
            listener.onItemClick(filteredList[position])
        }

    }

    inner class ViewHolder(val binding: ItemDesignKartCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    fun filter(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(list)  // Add all items from the original list
        } else {
            val filtered = list.filter { model ->
                model.name.contains(query, ignoreCase = true)
            }
            filteredList.addAll(filtered)
        }
        notifyDataSetChanged()
    }

}
