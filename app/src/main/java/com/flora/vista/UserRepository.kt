package com.flora.vista

import com.flora.vista.data.pref.UserModel
import com.flora.vista.data.pref.UserPreferences
import com.flora.vista.data.retrofit.ApiService
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(
    private val userPreferences: UserPreferences, private val apiService: ApiService
) {

    suspend fun register(name: String, email: String, password: String) =
        apiService.register(name, email, password)

    suspend fun login(email: String, password: String) = apiService.login(email, password)

    suspend fun saveSession(user: UserModel) {
        userPreferences.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreferences.getSession()
    }

    suspend fun logout() {
        userPreferences.logout()
    }

    companion object {
        fun getInstance(
            userPreferences: UserPreferences,
            apiService: ApiService
        ) = UserRepository(userPreferences, apiService)
    }
}