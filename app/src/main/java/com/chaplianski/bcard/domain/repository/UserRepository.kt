package com.chaplianski.bcard.domain.repository

import com.chaplianski.bcard.domain.model.User

interface UserRepository {

    suspend fun checkLoginPassword(user: User): Long

    fun registrationUser(user: User): Long

    fun getSecretQuestion(login: String): User?

    fun updateUserData(user: User)
}