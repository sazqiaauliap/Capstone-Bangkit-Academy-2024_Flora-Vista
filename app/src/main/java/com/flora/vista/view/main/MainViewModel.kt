package com.flora.vista.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.flora.vista.UserRepository
import com.flora.vista.data.pref.UserModel
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {
    private val _userSession = MutableLiveData<UserModel>()

    init {
        loadSession()
    }

    private fun loadSession() {
        viewModelScope.launch {
            repository.getSession().collect { user ->
                _userSession.value = user
            }
        }
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

}