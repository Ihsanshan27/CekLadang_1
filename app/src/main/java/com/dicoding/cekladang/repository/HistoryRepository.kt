package com.dicoding.cekladang.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.dicoding.cekladang.data.local.entity.History
import com.dicoding.cekladang.data.local.room.HistoryDao
import com.dicoding.cekladang.data.local.room.HistoryDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class HistoryRepository private constructor(context: Context) {
    private val mHistoryDao: HistoryDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = HistoryDatabase.getDatabase(context)
        mHistoryDao = db.historyDao()
    }

    fun insert(history: History) {
        executorService.execute { mHistoryDao.insert(history) }
    }

    fun delete(history: History) {
        executorService.execute { mHistoryDao.delete(history) }
    }

    fun getAllHistoryUser(): LiveData<List<History>> = mHistoryDao.getAllHistoryUser()

    fun getHistoryById(id: String): LiveData<History> {
        return mHistoryDao.getHistoryById(id)
    }

    companion object {
        @Volatile
        private var instance: HistoryRepository? = null

        fun getInstance(context: Context): HistoryRepository =
            instance ?: synchronized(this) {
                instance ?: HistoryRepository(context).also { instance = it }
            }
    }
}
