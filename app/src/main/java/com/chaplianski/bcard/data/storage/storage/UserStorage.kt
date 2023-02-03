package com.chaplianski.bcard.data.storage.storage

import com.chaplianski.bcard.data.storage.modeldto.UserDTO

interface UserStorage {

    fun addUser(userDTO: UserDTO): Long

    fun checkUser(userDTO: UserDTO): Long

    fun getSecretQuestion(login: String): UserDTO?

    fun updateUser(userDTO: UserDTO)
}