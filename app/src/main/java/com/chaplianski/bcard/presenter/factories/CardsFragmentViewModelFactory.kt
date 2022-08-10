package com.chaplianski.bcard.presenter.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chaplianski.bcard.domain.usecases.AddCardUseCase
import com.chaplianski.bcard.domain.usecases.DeleteCardUseCase
import com.chaplianski.bcard.domain.usecases.GetCardsUseCase
import com.chaplianski.bcard.presenter.viewmodels.CardsFragmentViewModel
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class CardsFragmentViewModelFactory @Inject constructor(
    private val getCardUseCase: GetCardsUseCase,
    private val addCardUseCase: AddCardUseCase,
    private val deleteCardUseCase: DeleteCardUseCase
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CardsFragmentViewModel(getCardUseCase, addCardUseCase, deleteCardUseCase) as T
    }
}