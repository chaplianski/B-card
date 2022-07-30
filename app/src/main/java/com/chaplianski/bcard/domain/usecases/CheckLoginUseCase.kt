package com.chaplianski.bcard.domain.usecases

import androidx.lifecycle.MutableLiveData
import com.chaplianski.bcard.domain.repository.AuthorizationRepository
import javax.inject.Inject

class CheckLoginUseCase @Inject constructor(private val authorizationRepository: AuthorizationRepository) {

    suspend fun execute(email: String, password: String): MutableLiveData<String>{
        return authorizationRepository.checkLoginPassword(email, password)
    }
}