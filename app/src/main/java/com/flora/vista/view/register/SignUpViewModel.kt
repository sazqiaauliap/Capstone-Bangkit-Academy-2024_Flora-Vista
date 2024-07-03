package com.flora.vista.view.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flora.vista.UserRepository
import com.flora.vista.data.response.RegisterResponse
import kotlinx.coroutines.launch

class SignUpViewModel(private val userRepository: UserRepository): ViewModel() {
    private val _registerResponse = MutableLiveData<RegisterResponse>()
    val registerResponse: LiveData<RegisterResponse> = _registerResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = userRepository.register(name, email, password)
                _registerResponse.value = response
                _isLoading.value = false
            } catch (e: Exception) {
                e.printStackTrace()
                _registerResponse.value =
                    RegisterResponse(error = true.toString(), message = e.message ?: "Error")
                _isLoading.value = false
            }
        }
    }
}