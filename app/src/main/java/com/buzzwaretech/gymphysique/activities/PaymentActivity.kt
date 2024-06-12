package com.buzzwaretech.gymphysique.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.buzzwaretech.gymphysique.adapters.CardAdapter
import com.buzzwaretech.gymphysique.databinding.ActivityPaymentBinding

class PaymentActivity : AppCompatActivity(), CardAdapter.OnItemClickListener {
    lateinit var binding : ActivityPaymentBinding
    private lateinit var allCards: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListener()
        setAdapter()
    }

    private fun setListener() {
        binding.backIV.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        allCards = ArrayList()
        allCards.add(".... 4242")
    }

    private fun setAdapter() {
        binding.rvWallets.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvWallets.adapter = CardAdapter(this, allCards, this)
    }

    override fun onItemClick(itemName: String, type: String) {

    }
}