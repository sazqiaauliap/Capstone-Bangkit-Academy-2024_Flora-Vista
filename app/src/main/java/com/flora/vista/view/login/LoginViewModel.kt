package com.flora.vista.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flora.vista.UserRepository
import com.flora.vista.data.pref.UserModel
import com.flora.vista.data.response.LoginResponse
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse> = _loginResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = userRepository.login(email, password)
                _loginResponse.value = response
                _isLoading.value = false

                // Save session if login is successful
                response.token?.let {
                    val user = UserModel(
                        email = email,
                        token = it,
                        name = response.user?.name ?: "",
                        isLogin = true
                    )
                    saveSession(user)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _loginResponse.value = LoginResponse(message = e.message ?: "Error")
                _isLoading.value = false
            }
        }
    }

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            userRepository.saveSession(user)
        }
    }
}