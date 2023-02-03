package com.chaplianski.bcard.core.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chaplianski.bcard.core.viewmodels.CardsFragmentViewModel
import com.chaplianski.bcard.domain.usecases.*
import javax.inject.Inject

//@Suppress("UNCHECKED_CAST")
//class CardsFragmentViewModelFactory @Inject constructor(
//    private val getCardsUseCase: GetCardsUseCase,
//    private val addCardUseCase: AddCardUseCase,
//    private val deleteCardUseCase: DeleteCardUseCase,
//    private val getCardUseCase: GetCardUseCase,
//    private val updateCardUseCase: UpdateCardUseCase
//) : ViewModelProvider.Factory {
//
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        return CardsFragmentViewModel(
//            getCardsUseCase,
//            addCardUseCase,
//            deleteCardUseCase,
//            getCardUseCase,
//            updateCardUseCase
//        ) as T
//    }
//}