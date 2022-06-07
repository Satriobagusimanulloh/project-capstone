package com.bangkit.capstone.c22_ps321.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bangkit.capstone.c22_ps321.R
import com.bangkit.capstone.c22_ps321.databinding.ActivityDetailBinding
import com.bangkit.capstone.c22_ps321.models.HistoryModels
import com.bumptech.glide.Glide

class DetailActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityDetailBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val data = intent.getParcelableExtra<HistoryModels>(EXTRA_KEY)

        binding.apply {
            if (data != null) {
                tvTitle.text = data.name
                tvDisease.text = data.disease
                tvDescription.text = data.description
                tvTreatment.text = data.treatment
                Glide.with(this@DetailActivity)
                    .load(data.photoUrl)
                    .placeholder(R.drawable.ic_place_holder)
                    .centerCrop()
                    .into(imgPhoto)
            }
        }
    }

    companion object {
        const val EXTRA_KEY = "details_data"
    }
}