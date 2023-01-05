package com.chaplianski.bcard.core.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chaplianski.bcard.domain.usecases.CheckLoginUseCase
import com.chaplianski.bcard.core.viewmodels.LoginFragmentViewModel
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class LoginFragmentViewModelFactory @Inject constructor (private val sentLoginUseCase: CheckLoginUseCase): ViewModelProvider.Factory {
     override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginFragmentViewModel(sentLoginUseCase) as T
    }
}