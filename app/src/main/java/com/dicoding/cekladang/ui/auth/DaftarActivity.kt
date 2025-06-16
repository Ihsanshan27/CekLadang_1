package com.dicoding.cekladang.ui.auth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.cekladang.R
import com.dicoding.cekladang.databinding.ActivityDaftarBinding
import com.google.firebase.auth.FirebaseAuth

class DaftarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDaftarBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDaftarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        setupView()
        setupAction()
    }

    private fun setupAction() {
        binding.btnDaftar.setOnClickListener {
            val email = binding.emailEditText.text.toString()
//            val name = binding.nameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            when {
//                name.isEmpty() -> {
//                    binding.nameEditText.error = getString(R.string.name_empty)
//                }

                email.isEmpty() -> {
                    binding.emailEditText.error = getString(R.string.email_empty)
                }

                password.isEmpty() -> {
                    binding.passwordEditText.error = getString(R.string.password_empty)
                }

                password.length < 8 -> {
                    binding.passwordEditText.error = getString(R.string.password_short)
                }

                else -> {
                    RegisterFirebase(email, password)
                }
            }
        }

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

    }

    private fun RegisterFirebase(email: String, password: String) {
        binding.progressBar.visibility = View.VISIBLE
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                binding.progressBar.visibility = View.VISIBLE
                if (task.isSuccessful) {
                    Toast.makeText(this, "Register Berhasil", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
}