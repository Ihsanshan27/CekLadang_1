package com.dicoding.cekladang.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.cekladang.di.Injection
import com.dicoding.cekladang.repository.HistoryRepository
import com.dicoding.cekladang.ui.detailhistory.DetailViewModel
import com.dicoding.cekladang.ui.history.HistoryViewModel

class ViewModelFactory(
    private val historyRepository: HistoryRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> {
                HistoryViewModel(historyRepository) as T
            }

            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(historyRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                val history = Injection.provideHistoryRepository(context)
                ViewModelFactory(history).also { instance = it }
            }
    }
}