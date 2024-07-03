package com.flora.vista.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.flora.vista.data.pref.UserPreferences
import com.flora.vista.data.pref.dataStore
import com.flora.vista.databinding.FragmentProfileBinding
import com.flora.vista.view.login.LoginActivity
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val userPreferences = UserPreferences.getInstance(requireContext().dataStore)
        lifecycleScope.launch {
            userPreferences.getSession().collect { user ->
                binding.username.text = user.name
            }
        }

        binding.logout.setOnClickListener {
            logout()
        }

        return root
    }

    private fun logout() {
        val userPreferences = UserPreferences.getInstance(requireContext().dataStore)
        lifecycleScope.launch {
            userPreferences.logout()
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}