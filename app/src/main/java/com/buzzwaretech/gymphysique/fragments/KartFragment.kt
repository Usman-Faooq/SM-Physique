package com.buzzwaretech.gymphysique.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.buzzwaretech.gymphysique.R
import com.buzzwaretech.gymphysique.activities.KartDetailActivity
import com.buzzwaretech.gymphysique.activities.KartItemActivity
import com.buzzwaretech.gymphysique.activities.NotificationActivity
import com.buzzwaretech.gymphysique.adapters.HomeAdapter
import com.buzzwaretech.gymphysique.adapters.KartAdapter
import com.buzzwaretech.gymphysique.adapters.KartCategoryAdapter
import com.buzzwaretech.gymphysique.databinding.FragmentKartBinding
import com.buzzwaretech.gymphysique.models.KartCategoryModel
import com.buzzwaretech.gymphysique.models.KartModel
import com.buzzwaretech.gymphysique.models.PostModel

class KartFragment : BaseFragment(), KartCategoryAdapter.OnItemClickListener {

    lateinit var binding : FragmentKartBinding
    private lateinit var fragmentContext: Context
    private val kartList : ArrayList<KartCategoryModel> = arrayListOf()

    private lateinit var adapter: KartCategoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        binding = FragmentKartBinding.inflate(layoutInflater)

        setView()
        getKartItems()
        setListener()

        return binding.root
    }

    private fun setView() {

        Glide.with(fragmentContext)
            .load("")
            .placeholder(R.drawable.dummy_home_banner)
            .into(binding.layout.bannerIV)

        binding.layout.searchET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val query = p0.toString().trim()
                adapter.filter(query)
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

    private fun getKartItems() {
        mDialog.show()
        db.collection("MarketCategory").get().addOnSuccessListener {doc ->
            doc.forEach {
                val model = it.toObject(KartCategoryModel::class.java)
                model.id = it.id
                kartList.add(model)
            }

            setAdapter()

        }.addOnFailureListener {
            mDialog.dismiss()
        }
    }

    private fun setListener()
    {
        binding.layout.cvNotification.setOnClickListener {
            val intent = Intent(fragmentContext, NotificationActivity::class.java)
            startActivity(intent)
            requireActivity().overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
        }
    }

    private fun setAdapter() {
        mDialog.dismiss()
        binding.recyclerView.layoutManager = GridLayoutManager(fragmentContext, 2)
        adapter = KartCategoryAdapter(fragmentContext, kartList, this)
        binding.recyclerView.adapter = adapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

    override fun onItemClick(model: KartCategoryModel) {
        val intent = Intent(fragmentContext, KartItemActivity::class.java)
        intent.putExtra("modelId", model.id)
        intent.putExtra("name", model.name)
        startActivity(intent)
        requireActivity().overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
    }

}