package com.flora.vista.view.login


import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.flora.vista.MenuBottom
import com.flora.vista.ViewModelFactory
import com.flora.vista.data.pref.UserModel
import com.flora.vista.databinding.ActivityLoginBinding
import com.flora.vista.view.register.SignupActivity


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerNow.setOnClickListener{
            val intent = Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(intent)
        }

        setupView()
        setupAction()
        authenticationPass()
        playAnimation()
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

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditTextLayout.text.toString()
            val password = binding.passwordEditTextLayout.text.toString()
            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                binding.apply {
                    loginViewModel.login(
                        emailEditTextLayout.text.toString(),
                        passwordEditTextLayout.text.toString()
                    )
                }
            }
        }
    }

    private fun authenticationPass() {
        loginViewModel.loginResponse.observe(this){ login ->
            if (login.user != null) {
                loginViewModel.saveSession(
                    UserModel(
                        email = binding.emailEditTextLayout.text.toString(),
                        token = login.token.toString(),
                        name = login.user.name ?: "",
                        isLogin = true
                    )
                )
                showLoading(false)
                val intent = Intent(this@LoginActivity, MenuBottom::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent,
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            } else {
                showLoading(false)
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.welcomeImg, View.TRANSLATION_Y, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val welcome = ObjectAnimator.ofFloat(binding.textView, View.ALPHA, 1f).setDuration(500)
        val welkam = ObjectAnimator.ofFloat(binding.latinView, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val pass = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val button = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(500)
        val not = ObjectAnimator.ofFloat(binding.notMember, View.ALPHA, 1f).setDuration(500)
        val reg = ObjectAnimator.ofFloat(binding.registerNow, View.ALPHA, 1f).setDuration(500)
        AnimatorSet().apply {
            playSequentially(welcome, welkam, email, pass, button, not, reg)
            start()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        //binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}