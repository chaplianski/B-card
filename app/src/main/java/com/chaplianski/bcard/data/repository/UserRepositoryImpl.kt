package com.chaplianski.bcard.data.repository

import com.chaplianski.bcard.data.storage.database.UserStorageImpl
import com.chaplianski.bcard.domain.model.User
import com.chaplianski.bcard.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userStorageImpl: UserStorageImpl,
    ): UserRepository {

    override suspend fun checkLoginPassword(user: User): Long {
        return userStorageImpl.checkUser(user.userMapDomainToData())
    }

    override fun registrationUser(user: User): Long {
       return userStorageImpl.addUser(user.userMapDomainToData())
    }

    override fun getSecretQuestion(login: String): User? {
        return userStorageImpl.getSecretQuestion(login)?.userMapDataToDomain()
    }

    override fun updateUserData(user: User) {
        return userStorageImpl.updateUser(user.userMapDomainToData())
    }
}