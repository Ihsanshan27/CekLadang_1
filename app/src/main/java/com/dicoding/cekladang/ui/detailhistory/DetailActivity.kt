package com.dicoding.cekladang.ui.detailhistory

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.cekladang.data.local.entity.History
import com.dicoding.cekladang.databinding.ActivityDetailBinding
import com.dicoding.cekladang.ui.viewmodels.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: DetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[DetailViewModel::class.java]

        val historyId = intent.getIntExtra("EXTRA_HISTORY_ID", -1)

        if (historyId != -1) {
            viewModel.getHistoryById(historyId.toString()).observe(this, Observer { history ->
                if (history != null) {
                    Log.d("DetailActivity", "Data found: ${history.name}")
                    displayHistoryDetails(history)
                } else {
                    Log.d("DetailActivity", "No data found for ID: $historyId")
                }
            })
        } else {
            Log.d("DetailActivity", "historyId is null")
        }
    }

    private fun displayHistoryDetails(history: History) {
        binding.resultName.text = "Nama Tanaman : ${history.name}"
        binding.resultAnalisis.text = "Hasil Analisis : ${history.prediction}"
        binding.tvItemDescription.text = "${history.description}"
        val dateFormat = SimpleDateFormat("dd MMM yyy, HH:mm", Locale.getDefault())
        val date = Date(history.timestamp)
        binding.timestamp.text = dateFormat.format(date)
        Glide.with(this)
            .load(history.image)
            .into(binding.resultImage)
    }
}
