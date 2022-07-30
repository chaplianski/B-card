package com.chaplianski.bcard.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.chaplianski.bcard.data.storage.net.FirebaseAuthorization
import com.chaplianski.bcard.domain.repository.AuthorizationRepository
import javax.inject.Inject

class AuthorizationRepositoryImpl @Inject constructor(private val firebaseAuthorization: FirebaseAuthorization): AuthorizationRepository {

    override suspend fun checkLoginPassword(email: String, password: String): MutableLiveData<String> {

        val res = firebaseAuthorization.checkLoginPassword(email, password).value
        Log.d("MyLog", "res = ${res}")
        return firebaseAuthorization.checkLoginPassword(email, password)

    }


//    override fun checkLoginPassword(email: String, password: String): String {
//
//        val res = firebaseAuthorization.checkLoginPassword(email, password){it}
//        return "res"
//    }

    override fun registrationUser(email: String, password: String): String {
        TODO("Not yet implemented")
    }
}