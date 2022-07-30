package com.chaplianski.bcard.domain.usecases

import com.chaplianski.bcard.domain.repository.AuthorizationRepository
import javax.inject.Inject

class UserRegistrationUseCase @Inject constructor(private val authorizationRepository: AuthorizationRepository) {

    fun execute(email: String, password: String){
        authorizationRepository.registrationUser(email, password)
    }
}