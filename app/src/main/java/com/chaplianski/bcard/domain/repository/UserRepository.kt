package com.chaplianski.bcard.domain.repository

import androidx.lifecycle.MutableLiveData
import com.chaplianski.bcard.domain.model.User

interface UserRepository {

    suspend fun checkLoginPassword(user: User): Long

    fun registrationUser(user: User): Long
}