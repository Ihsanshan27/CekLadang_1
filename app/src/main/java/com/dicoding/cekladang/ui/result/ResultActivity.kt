package com.dicoding.cekladang.ui.result

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.cekladang.BuildConfig
import com.dicoding.cekladang.data.local.entity.History
import com.dicoding.cekladang.databinding.ActivityResultBinding
import com.dicoding.cekladang.helper.ImageClassifierHelper
import com.dicoding.cekladang.ui.history.HistoryViewModel
import com.dicoding.cekladang.ui.viewmodels.MainActivity
import com.dicoding.cekladang.ui.viewmodels.ViewModelFactory
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.java.GenerativeModelFutures
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    private var plantName: String? = null
    private var labelName: String? = null
    private var modelPath: String? = null
    private var resultText: String? = null
    private var description: String? = null
    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private lateinit var historyViewModel: HistoryViewModel
    private var history: History = History()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.title = "Hasil Analisis"
        }

        // Menggunakan singleton getInstance
        val factory = ViewModelFactory.getInstance(this)
        historyViewModel = ViewModelProvider(this, factory)[HistoryViewModel::class.java]

        plantName = intent.getStringExtra("PLANT_NAME")
        labelName = intent.getStringExtra("LABEL_NAME")
        modelPath = intent.getStringExtra("MODEL_PATH")

        Log.d(TAG, "NameResult: $plantName, Label: $labelName  Model: $modelPath")

        // Cek apakah imageClassifierHelper sudah diinisialisasi
        if (!::imageClassifierHelper.isInitialized) {
            imageClassifierHelper = ImageClassifierHelper(
                context = this,
                classifierListener = object : ImageClassifierHelper.ClassifierListener {
                    override fun onError(errorMessage: String) {
                        Log.d(TAG, "Error: $errorMessage")
                    }

                    override fun onResults(results: List<String>, inferenceTime: Long) {
                        showResults(results)
                        if (results.isNotEmpty()) {
                            val label = results[0]
                            history.name = plantName
                            history.prediction = label
                            history.image = intent.getStringExtra(IMAGE_URI)
                            Log.d(TAG, "name: $plantName")

                            getResultGeminiAPI(label)
                        }
                    }
                }
            )
            // Update model dan label hanya sekali
            if (labelName != null && modelPath != null) {
                imageClassifierHelper.updateModelAndLabels(labelName!!, modelPath!!)
                imageClassifierHelper.init()
            }
        }

        if (labelName != null && modelPath != null) {
            Log.d(TAG, "Received label: $labelName and model path: $modelPath")
            val imageUriString = intent.getStringExtra(IMAGE_URI)
            if (imageUriString != null) {
                val imageUri = Uri.parse(imageUriString)
                displayImage(imageUri)

                val bitmap = uriToBitmap(imageUri)
                if (bitmap != null) {
                    imageClassifierHelper.classifyImage(bitmap)
                } else {
                    Log.e(TAG, "Failed to convert Uri to Bitmap")
                }
            } else {
                finish()
            }
        } else {
            finish()
        }

        binding.btnSave.setOnClickListener {
            Log.d(
                TAG,
                "Saving History: ${history.name}, ${history.prediction}, ${history.image} ${history.resultText}"
            )

            historyViewModel.insert(history)
            Toast.makeText(this, "Data Tersimpan", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun displayImage(uri: Uri) {
        binding.resultImage.setImageURI(uri)
    }

    private fun showResults(results: List<String>) {
        if (results.isNotEmpty()) {
            val name = plantName
            val label = results[0] // Mengambil hasil klasifikasi terbaik
            val resultText =
                "Berdasarkan hasil analisa gambar, tanaman $name dengan kondisi daun yaitu $label"
            binding.resultText.text = resultText
            history.resultText = resultText
        } else {
            Toast.makeText(this, "No Classification Result", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getResultGeminiAPI(results: String) {
        val name = plantName
        val label = results // Mengambil hasil klasifikasi terbaik
        val contentText =
            "apa saja tips atau cara penanganan pada tanaman $name dengan kondisi daun $label. Jelaskan dalam bentuk poin per poin"
        Log.d(TAG, "Prompt to Gemini API: $contentText")

        val generativeModel =
            GenerativeModel("gemini-1.5-flash-latest", BuildConfig.GEMINI_API_KEY)
        val modelFutures = GenerativeModelFutures.from(generativeModel)
        val content = Content.Builder()
            .text(contentText)
            .build()

        Log.d(TAG, "Request to Gemini API in progress...")
        val responseFuture: ListenableFuture<GenerateContentResponse> =
            modelFutures.generateContent(content)
        Log.d(TAG, "Request to Gemini API initiated.")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Futures.addCallback(responseFuture, object : FutureCallback<GenerateContentResponse> {
                override fun onSuccess(result: GenerateContentResponse) {
                    Log.d(TAG, "Gemini API berhasil merespons dengan teks: ${result.text}")
                    description = result.text?.replace("*", "")
                    binding.resultDesc.text = description
                    history.description = description
                }

                override fun onFailure(t: Throwable) {
                    binding.resultDesc.text = t.toString()
                }
            }, mainExecutor)
        }
    }

    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    companion object {
        const val IMAGE_URI = "img_uri"
        const val TAG = "AnalisisActivity"
    }
}