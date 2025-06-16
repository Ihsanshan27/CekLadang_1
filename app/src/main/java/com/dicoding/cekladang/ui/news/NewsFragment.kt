package com.dicoding.cekladang.ui.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.cekladang.databinding.FragmentNewsBinding
import com.dicoding.cekladang.helper.Result
import com.dicoding.cekladang.ui.adapter.NewsAdapter

class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!
    private lateinit var newsViewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newsAdapter = NewsAdapter()

        val factory = NewsViewModelFactory.getInstance()
        newsViewModel = ViewModelProvider(this, factory)[NewsViewModel::class.java]

        binding.rvArticle.setHasFixedSize(true)
        binding.rvArticle.layoutManager = LinearLayoutManager(context)
        binding.rvArticle.adapter = newsAdapter

        newsViewModel.getAllArticles().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    val articles = result.data
                    newsAdapter.submitList(articles)
                    binding.progressBar.isVisible = false
                }

                is Result.Error -> {
                    Toast.makeText(requireContext(), "Error: ${result.error}", Toast.LENGTH_SHORT)
                        .show()
                    binding.progressBar.isVisible = false
                }

                is Result.Loading -> {
                    binding.progressBar.isVisible = true
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
