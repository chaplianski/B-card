package com.chaplianski.bcard.core.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chaplianski.bcard.domain.usecases.AddCardUseCase
import com.chaplianski.bcard.domain.usecases.DeleteCardUseCase
import com.chaplianski.bcard.domain.usecases.GetCardUseCase
import com.chaplianski.bcard.domain.usecases.GetCardsUseCase
import com.chaplianski.bcard.core.viewmodels.CardsFragmentViewModel
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class CardsFragmentViewModelFactory @Inject constructor(
    private val getCardsUseCase: GetCardsUseCase,
    private val addCardUseCase: AddCardUseCase,
    private val deleteCardUseCase: DeleteCardUseCase,
    private val getCardUseCase: GetCardUseCase
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CardsFragmentViewModel(getCardsUseCase, addCardUseCase, deleteCardUseCase, getCardUseCase) as T
    }
}