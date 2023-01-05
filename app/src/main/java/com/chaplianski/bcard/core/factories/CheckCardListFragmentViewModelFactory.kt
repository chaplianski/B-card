package com.chaplianski.bcard.core.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chaplianski.bcard.core.viewmodels.CheckCardListFragmentViewModel
import com.chaplianski.bcard.domain.usecases.GetCardsUseCase
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class CheckCardListFragmentViewModelFactory @Inject constructor(
    private val getCardsUseCase: GetCardsUseCase,
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CheckCardListFragmentViewModel(getCardsUseCase) as T
    }
}