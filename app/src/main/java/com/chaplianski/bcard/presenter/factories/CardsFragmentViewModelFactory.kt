package com.chaplianski.bcard.presenter.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chaplianski.bcard.domain.usecases.GetCardUseCase
import com.chaplianski.bcard.presenter.viewmodels.CardsFragmentViewModel
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class CardsFragmentViewModelFactory @Inject constructor(private val getCardUseCase: GetCardUseCase): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CardsFragmentViewModel(getCardUseCase) as T
    }
}