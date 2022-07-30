package com.chaplianski.bcard.domain.repository

import androidx.lifecycle.MutableLiveData

interface AuthorizationRepository {

    suspend fun checkLoginPassword(email: String, password: String): MutableLiveData<String>

    fun registrationUser(email: String, password: String): String
}