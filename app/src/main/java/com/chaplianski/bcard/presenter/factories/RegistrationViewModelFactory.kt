package com.chaplianski.bcard.presenter.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chaplianski.bcard.domain.usecases.UserRegistrationUseCase
import com.chaplianski.bcard.presenter.viewmodels.RegistrationFragmentViewModel
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class RegistrationViewModelFactory @Inject constructor(private val userRegistrationUseCase: UserRegistrationUseCase): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RegistrationFragmentViewModel(userRegistrationUseCase) as T
    }
}