package com.flora.vista.injection

import android.content.Context
import com.flora.vista.UserRepository
import com.flora.vista.data.pref.UserPreferences
import com.flora.vista.data.pref.dataStore
import com.flora.vista.data.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreferences.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(pref, apiService)
    }
}