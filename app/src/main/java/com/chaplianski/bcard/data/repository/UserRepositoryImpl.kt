package com.chaplianski.bcard.data.repository

import androidx.lifecycle.MutableLiveData
import com.chaplianski.bcard.data.storage.database.UserStorageImpl
import com.chaplianski.bcard.data.storage.net.FirebaseAuthorization
import com.chaplianski.bcard.domain.model.User
import com.chaplianski.bcard.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userStorageImpl: UserStorageImpl,
//    private val firebaseAuthorization: FirebaseAuthorization
    ): UserRepository {

    override suspend fun checkLoginPassword(user: User): Long {
        return userStorageImpl.checkUser(user.userMapDomainToData())
//        val res = firebaseAuthorization.checkLoginPassword(email, password).value
//        return firebaseAuthorization.checkLoginPassword(email, password)


    }


//    override fun checkLoginPassword(email: String, password: String): String {
//
//        val res = firebaseAuthorization.checkLoginPassword(email, password){it}
//        return "res"
//    }

    override fun registrationUser(user: User): Long {
       return userStorageImpl.addUser(user.userMapDomainToData())
    }
}