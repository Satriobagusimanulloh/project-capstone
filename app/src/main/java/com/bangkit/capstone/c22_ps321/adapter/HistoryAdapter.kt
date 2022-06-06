package com.bangkit.capstone.c22_ps321.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.capstone.c22_ps321.R
import com.bangkit.capstone.c22_ps321.databinding.ItemRowHistoryBinding
import com.bangkit.capstone.c22_ps321.models.HistoryModels
import com.bumptech.glide.Glide

class HistoryAdapter(private val listData: (HistoryModels) -> Unit): ListAdapter<HistoryModels, HistoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: IOnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: IOnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class ViewHolder(var binding: ItemRowHistoryBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(data: HistoryModels){
            binding.apply {
                tvItemNamePlant.text = data.name
                tvItemResult.text = data.disease
                Glide.with(itemView.context)
                    .load(data.photoUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_place_holder)
                    .into(imgItemPhoto)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRowHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val items = getItem(position)
        holder.bind(items)

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(items)
        }
    }

    interface IOnItemClickCallback {
        fun onItemClicked(data: HistoryModels)
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<HistoryModels> =
            object: DiffUtil.ItemCallback<HistoryModels>() {
                override fun areItemsTheSame(
                    oldItem: HistoryModels,
                    newItem: HistoryModels
                ): Boolean {
                    return oldItem.name == newItem.name
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(
                    oldItem: HistoryModels,
                    newItem: HistoryModels
                ): Boolean {
                    return oldItem == newItem
                }

            }
    }
}