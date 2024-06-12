package com.buzzwaretech.gymphysique.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.buzzwaretech.gymphysique.R
import com.buzzwaretech.gymphysique.adapters.KartAdapter
import com.buzzwaretech.gymphysique.adapters.KartCategoryAdapter
import com.buzzwaretech.gymphysique.databinding.ActivityKartItemBinding
import com.buzzwaretech.gymphysique.models.KartCategoryModel
import com.buzzwaretech.gymphysique.models.KartModel

class KartItemActivity : BaseActivity(), KartAdapter.OnItemClickListener {

    val binding : ActivityKartItemBinding by lazy{
        ActivityKartItemBinding.inflate(layoutInflater)
    }

    private val kartList : ArrayList<KartModel> = arrayListOf()

    private lateinit var adapter: KartAdapter

    var categoryId = ""
    var categoryName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        categoryId = intent.getStringExtra("modelId").toString()
        categoryName = intent.getStringExtra("name").toString()

        //binding.layout.titleTV.text = categoryName
        binding.layout.backIV.visibility = View.VISIBLE
        binding.layout.backIV.setOnClickListener { finish() }

        Glide.with(this)
            .load("")
            .placeholder(R.drawable.dummy_home_banner)
            .into(binding.layout.bannerIV)

        getKartItems()
        setView()

    }

    private fun setView() {
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
        db.collection("MarketPlace").whereEqualTo("categoryId", categoryId).get().addOnSuccessListener {doc ->
            doc.forEach {
                val model = it.toObject(KartModel::class.java)
                model.itemId = it.id
                kartList.add(model)
            }

            setAdapter()

        }.addOnFailureListener {
            mDialog.dismiss()
        }
    }

    private fun setAdapter() {
        mDialog.dismiss()
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        adapter = KartAdapter(this, kartList, this)
        binding.recyclerView.adapter = adapter
    }

    override fun onItemClick(model: KartModel) {
        val intent = Intent(this, KartDetailActivity::class.java)
        intent.putExtra("kartModel", model)
        startActivity(intent)
        overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
    }

}