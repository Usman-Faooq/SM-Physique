package com.buzzwaretech.gymphysique.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.buzzwaretech.gymphysique.R
import com.buzzwaretech.gymphysique.databinding.ItemDesignKartViewBinding
import com.buzzwaretech.gymphysique.models.KartCategoryModel
import com.buzzwaretech.gymphysique.models.KartModel

class KartAdapter(val context: Context, private val list: List<KartModel>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<KartAdapter.ViewHolder>() {


    interface OnItemClickListener {
        fun onItemClick(model : KartModel)
    }

    private var filteredList: ArrayList<KartModel> = ArrayList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemDesignKartViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val model = filteredList[position]

        holder.binding.priceTV.text = "$${model.price}"
        holder.binding.nameTV.text = model.name
        Glide.with(context)
            .load(model.images[0])
            .placeholder(R.drawable.holder_post)
            .into(holder.binding.kartIV)


        holder.binding.root.setOnClickListener {
            listener.onItemClick(list[position])
        }

    }

    inner class ViewHolder(val binding: ItemDesignKartViewBinding) : RecyclerView.ViewHolder(binding.root)

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
