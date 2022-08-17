package com.chaplianski.bcard.domain.usecases

import com.chaplianski.bcard.domain.model.User
import com.chaplianski.bcard.domain.repository.UserRepository
import javax.inject.Inject

class AddUserUseCase @Inject constructor(private val userRepository: UserRepository) {

    fun execute(user: User): Long{
        return userRepository.registrationUser(user)
    }
}