package com.flora.vista.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.flora.vista.databinding.ActivityWelcomeBinding
import com.flora.vista.view.login.LoginActivity

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.getButton.setOnClickListener{
            val intent = Intent(this@WelcomeActivity,  LoginActivity::class.java)
            startActivity(intent)
        }

        setupView()
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


    private fun playAnimation() {
        val image = ObjectAnimator.ofFloat(binding.imageFloravista, View.ALPHA, 1f).setDuration(1000)
        val button = ObjectAnimator.ofFloat(binding.getButton, View.ALPHA, 1f).setDuration(700)
        AnimatorSet().apply {
            playSequentially(image, button)
            start()
        }
    }
}