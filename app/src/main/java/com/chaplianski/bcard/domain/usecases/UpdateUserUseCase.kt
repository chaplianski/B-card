package com.chaplianski.bcard.domain.usecases

import com.chaplianski.bcard.domain.model.User
import com.chaplianski.bcard.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(private val userRepository: UserRepository) {

    fun execute(user: User){
        return userRepository.updateUserData(user)
    }
}