package com.dicoding.cekladang.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.cekladang.data.local.entity.Plants

class HomeViewModel : ViewModel() {

    private val _plantList = MutableLiveData<List<Plants>>()
    val plantList: LiveData<List<Plants>> get() = _plantList

    init {
        loadPlants()
    }

    private fun loadPlants() {
        // Data statis tanaman
        _plantList.value = listOf(
            Plants(1, "Jagung", "corn_labels.txt", "model_corn_quantized.tflite"),
            Plants(2, "Kedelai", "soybean_label.txt", "model_soybean_quantized.tflite"),
            Plants(3, "Kacang Tanah", "groundnut_label.txt", "model_groundnut_quantized.tflite")
        )
    }
}