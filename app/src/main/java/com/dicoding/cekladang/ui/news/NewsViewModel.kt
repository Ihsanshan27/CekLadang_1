package com.dicoding.cekladang.ui.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.cekladang.data.remote.response.ArticlesItem
import com.dicoding.cekladang.helper.Result
import com.dicoding.cekladang.repository.ArticleRepository

class NewsViewModel(private val repository: ArticleRepository) : ViewModel() {
    fun getAllArticles(): LiveData<Result<List<ArticlesItem>>> = repository.getSearchNews()
}