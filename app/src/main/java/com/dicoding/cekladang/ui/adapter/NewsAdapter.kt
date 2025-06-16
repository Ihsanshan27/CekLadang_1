package com.dicoding.cekladang.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.cekladang.R
import com.dicoding.cekladang.data.remote.response.ArticlesItem
import com.dicoding.cekladang.databinding.ItemNewsBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class NewsAdapter : ListAdapter<ArticlesItem, NewsAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    class MyViewHolder(private var binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(currentItem: ArticlesItem) {
            binding.tvTitle.text = currentItem.title
            binding.tvDescription.text = currentItem.description

            val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            originalFormat.timeZone = TimeZone.getTimeZone("UTC")

            val date: Date? = try {
                originalFormat.parse(currentItem.publishedAt)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

            if (date != null) {
                val displayFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                binding.tvDatetime.text = displayFormat.format(date)
            } else {
                binding.tvDatetime.text = "Unknown Date"
            }

            // Mengecek apakah URL gambar ada atau tidak
            val imageUrl = currentItem.urlToImage
            if (!imageUrl.isNullOrEmpty()) {
                Glide.with(binding.root)
                    .load(imageUrl)
                    .into(binding.ivImage)
            } else {
                Glide.with(binding.root)
                    .load(R.drawable.baseline_broken_image_24)
                    .into(binding.ivImage)
            }
        }
    }


    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ArticlesItem>() {
            override fun areItemsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}