package com.chaplianski.bcard.presenter.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chaplianski.bcard.domain.usecases.AddCardUseCase
import com.chaplianski.bcard.presenter.viewmodels.ShareFragmentViewModel
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class ShareFragmentViewModelFactory @Inject constructor(private val addCardUseCase: AddCardUseCase): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ShareFragmentViewModel(addCardUseCase) as T
    }
}