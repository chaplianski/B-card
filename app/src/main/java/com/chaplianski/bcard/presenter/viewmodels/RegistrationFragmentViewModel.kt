package com.chaplianski.bcard.presenter.viewmodels

import androidx.lifecycle.ViewModel
import com.chaplianski.bcard.domain.model.User
import com.chaplianski.bcard.domain.usecases.AddUserUseCase
import ezvcard.property.Email
import javax.inject.Inject

class RegistrationFragmentViewModel @Inject constructor(private val addUserUseCase: AddUserUseCase) : ViewModel() {

        fun addUser(email: String, password: String): Long{
            val newUser = User (0, email, password)
            return addUserUseCase.execute(newUser)
        }
}