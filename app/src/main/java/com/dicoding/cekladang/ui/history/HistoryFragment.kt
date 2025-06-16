package com.dicoding.cekladang.ui.history

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.cekladang.databinding.FragmentHistoryBinding
import com.dicoding.cekladang.repository.HistoryRepository
import com.dicoding.cekladang.ui.adapter.HistoryAdapter
import com.dicoding.cekladang.ui.detailhistory.DetailActivity
import com.dicoding.cekladang.ui.viewmodels.ViewModelFactory

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val historyRepository =
            HistoryRepository.getInstance(requireContext())
        val factory =
            ViewModelFactory.getInstance(requireContext())
        historyViewModel = ViewModelProvider(this, factory)[HistoryViewModel::class.java]

        adapter = HistoryAdapter()
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        binding.rvHistory.addItemDecoration(itemDecoration)
        binding.rvHistory.adapter = adapter

        adapter.onDeleteClick = { history ->
            historyViewModel.delete(history)
        }

        adapter.onItemClick = { history ->
            val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                putExtra("EXTRA_HISTORY_ID", history.id)
            }
            startActivity(intent)
        }

        historyViewModel.getAllHistoryUser().observe(viewLifecycleOwner, Observer { list ->
            if (list != null) {
                adapter.submitList(list)
                binding.rvHistory.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}