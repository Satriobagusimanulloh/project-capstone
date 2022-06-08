package com.bangkit.capstone.c22_ps321.activities

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.capstone.c22_ps321.adapter.HistoryAdapter
import com.bangkit.capstone.c22_ps321.databinding.ActivityHistoryBinding
import com.bangkit.capstone.c22_ps321.models.HistoryModels
import com.bangkit.capstone.c22_ps321.user.UserPreferences
import com.bangkit.capstone.c22_ps321.viewmodels.HistoryViewModel
import com.bangkit.capstone.c22_ps321.viewmodels.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class HistoryActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityHistoryBinding.inflate(layoutInflater)
    }
    private lateinit var adapterHistory: HistoryAdapter
    private lateinit var viewModel: HistoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        adapterHistory = HistoryAdapter {}

        binding.rvHistory.apply {
            layoutManager =
                if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    GridLayoutManager(this@HistoryActivity, 2)
                } else {
                    LinearLayoutManager(this@HistoryActivity)
                }
            setHasFixedSize(true)
            adapter = adapterHistory
        }

        adapterHistory.setOnItemClickCallback(object: HistoryAdapter.IOnItemClickCallback {
            override fun onItemClicked(data: HistoryModels) {
                val intent = Intent(this@HistoryActivity, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_KEY, data)
                startActivity(intent)
            }

        })

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[HistoryViewModel::class.java]

        viewModel.isLoading.observe(this@HistoryActivity) {
            showLoading(it)
        }

        viewModel.isMessage.observe(this@HistoryActivity) {
            Toast.makeText(this@HistoryActivity, it.getContentIfNotHandled(), Toast.LENGTH_SHORT).show()
        }

        viewModel.getAllData()
        viewModel.responseData.observe(this@HistoryActivity) { data->
            Log.d("HistoryActivity", data.toString())
            if (data.isEmpty()) {
                binding.rvHistory.visibility = View.GONE
                binding.imgNotFound.visibility = View.VISIBLE
            } else {
                binding.imgNotFound.visibility = View.GONE
                adapterHistory.submitList(data)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbHistory.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}