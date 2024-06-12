package com.buzzwaretech.gymphysique.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.buzzwaretech.gymphysique.databinding.ActivityImageViewBinding

class ImageViewActivity : AppCompatActivity() {

    val binding : ActivityImageViewBinding by lazy {
        ActivityImageViewBinding.inflate(layoutInflater)
    }

    var imageUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backIV.setOnClickListener { finish() }

        imageUrl = intent.getStringExtra("URL").toString()

        Glide.with(this).load(imageUrl).into(binding.myZoomageView)
    }
}