package com.chaplianski.bcard.domain.usecases

import com.chaplianski.bcard.domain.model.User
import com.chaplianski.bcard.domain.repository.UserRepository
import javax.inject.Inject

class GetSecretQuestionUseCase @Inject constructor(private val userRepository: UserRepository) {

    fun execute(login: String): User? {
        return userRepository.getSecretQuestion(login)
    }
}