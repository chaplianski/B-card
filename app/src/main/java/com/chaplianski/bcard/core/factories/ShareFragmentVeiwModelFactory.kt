package com.chaplianski.bcard.core.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chaplianski.bcard.domain.usecases.AddCardUseCase
import com.chaplianski.bcard.domain.usecases.GetCardUseCase
import com.chaplianski.bcard.core.viewmodels.ShareFragmentViewModel
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class ShareFragmentViewModelFactory @Inject constructor(
    private val addCardUseCase: AddCardUseCase,
    private val getCardUseCase: GetCardUseCase): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ShareFragmentViewModel(addCardUseCase, getCardUseCase) as T
    }
}