package com.chaplianski.bcard.core.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chaplianski.bcard.domain.usecases.CheckLoginUseCase
import com.chaplianski.bcard.core.viewmodels.LoginFragmentViewModel
import com.chaplianski.bcard.domain.usecases.AddUserUseCase
import com.chaplianski.bcard.domain.usecases.GetSecretQuestionUseCase
import com.chaplianski.bcard.domain.usecases.UpdateUserUseCase
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class LoginFragmentViewModelFactory @Inject constructor (
    private val checkLoginUseCase: CheckLoginUseCase,
    private val addUserUseCase: AddUserUseCase,
    private val getSecretQuestionUseCase: GetSecretQuestionUseCase,
    private val updateUserUseCase: UpdateUserUseCase
    ): ViewModelProvider.Factory {
     override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginFragmentViewModel(checkLoginUseCase, addUserUseCase, getSecretQuestionUseCase, updateUserUseCase) as T
    }
}