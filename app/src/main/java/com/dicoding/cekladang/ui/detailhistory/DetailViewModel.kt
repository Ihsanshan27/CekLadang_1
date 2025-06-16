package com.dicoding.cekladang.ui.detailhistory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.cekladang.data.local.entity.History
import com.dicoding.cekladang.repository.HistoryRepository

class DetailViewModel(private val historyRepository: HistoryRepository) : ViewModel() {
    fun getHistoryById(id: String): LiveData<History> = historyRepository.getHistoryById(id)
}