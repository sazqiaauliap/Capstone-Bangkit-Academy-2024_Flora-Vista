package com.flora.vista

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController

import androidx.navigation.ui.setupWithNavController
import com.flora.vista.databinding.ActivityMenuBottomBinding

class MenuBottom : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBottomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuBottomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_menu_bottom)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)
        setupWithNavController(bottomNavigationView,  navController)
        navView.setupWithNavController(navController)
    }
}