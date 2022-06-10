package com.bangkit.capstone.c22_ps321.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bangkit.capstone.c22_ps321.R
import com.bangkit.capstone.c22_ps321.databinding.ActivityResultBinding
import com.bangkit.capstone.c22_ps321.user.UserPreferences
import com.bangkit.capstone.c22_ps321.viewmodels.ResultViewModel
import com.bangkit.capstone.c22_ps321.viewmodels.ViewModelFactory
import com.bumptech.glide.Glide

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class ResultActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityResultBinding.inflate(layoutInflater)
    }
    private lateinit var viewModel: ResultViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[ResultViewModel::class.java]

        viewModel.isLoading.observe(this@ResultActivity) {
            showLoading(it)
        }

        viewModel.isMessage.observe(this@ResultActivity) {
            Toast.makeText(this@ResultActivity, it.getContentIfNotHandled(), Toast.LENGTH_SHORT).show()
        }

        val key = intent.getStringExtra(EXTRA_KEY)
        if (key != null) {
            Log.d("ExtraKey", key)
        }

        viewModel.getDataById(key)
        viewModel.responseData.observe(this@ResultActivity) { data->
            if (data != null) {
                binding.apply {
                    tvTitle.text = data.name
                    tvDescription.text = data.description
                    tvTreatment.text = data.treatment
                    tvDisease.text = data.disease

                    Glide.with(this@ResultActivity)
                        .load(data.photoUrl)
                        .centerCrop()
                        .placeholder(R.drawable.ic_place_holder)
                        .into(imgPhoto)
                }
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbResult.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val EXTRA_KEY = "id_new_data"
    }
}