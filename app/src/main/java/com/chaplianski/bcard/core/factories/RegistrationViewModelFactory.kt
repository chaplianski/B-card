package com.chaplianski.bcard.core.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chaplianski.bcard.domain.usecases.AddUserUseCase
import com.chaplianski.bcard.core.viewmodels.RegistrationFragmentViewModel
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class RegistrationViewModelFactory @Inject constructor(private val userRegistrationUseCase: AddUserUseCase): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RegistrationFragmentViewModel(userRegistrationUseCase) as T
    }
}