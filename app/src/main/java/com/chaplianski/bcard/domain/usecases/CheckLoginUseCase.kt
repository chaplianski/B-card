package com.chaplianski.bcard.domain.usecases

import com.chaplianski.bcard.domain.model.User
import com.chaplianski.bcard.domain.repository.UserRepository
import javax.inject.Inject

class CheckLoginUseCase @Inject constructor(private val authorizationRepository: UserRepository) {

    suspend fun execute(user: User): Long{
        return authorizationRepository.checkLoginPassword(user)
    }
}