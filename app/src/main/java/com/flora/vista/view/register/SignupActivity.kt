package com.flora.vista.view.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import com.flora.vista.ViewModelFactory
import com.flora.vista.databinding.ActivitySignupBinding
import com.flora.vista.view.login.LoginActivity

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val signupViewModel by viewModels<SignUpViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        authenticationPass()
        playAnimation()
    }

    private fun playAnimation() {
        val name = ObjectAnimator.ofFloat(binding.nameTitle, View.ALPHA, 1f).setDuration(500)
        val nbox = ObjectAnimator.ofFloat(binding.nameTextbox, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.emailTitle, View.ALPHA, 1f).setDuration(500)
        val ebox = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val pass = ObjectAnimator.ofFloat(binding.passwordTitle, View.ALPHA, 1f).setDuration(500)
        val pbox = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val button = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(500)
        AnimatorSet().apply {
            playTogether(name, nbox, email, ebox, pass, pbox,button)
            start()
        }
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val email = binding.emailEditTextLayout.text.toString()
            val name = binding.nameTextbox.text.toString()
            val password = binding.passwordEditTextLayout.text.toString()
            if (name.isBlank() || email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                signupViewModel.register(name, email, password)
            }
        }

    }

    private fun authenticationPass() {
        signupViewModel.registerResponse.observe(this) { register ->
            if (register != null) {
                showLoading(false)
                if (register.error != null) {
                    showLoading(false)
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                } else {
                    showLoading(false)
                    Toast.makeText(this, "Sign Up Success!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            } else {
                showLoading(false)
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
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

    private fun showLoading(isLoading: Boolean) {
        //binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}