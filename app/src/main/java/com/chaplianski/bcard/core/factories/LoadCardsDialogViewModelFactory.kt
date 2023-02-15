package com.chaplianski.bcard.core.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chaplianski.bcard.core.viewmodels.LoadCardsDialogViewModel
import com.chaplianski.bcard.domain.usecases.AddCardUseCase
import com.chaplianski.bcard.domain.usecases.GetCardListUseCase
import javax.inject.Inject


@Suppress("UNCHECKED_CAST")
class LoadCardsDialogViewModelFactory @Inject constructor(
    private val addCardUseCase: AddCardUseCase,
    private val getCardListUseCase: GetCardListUseCase,

    ) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoadCardsDialogViewModel(
            addCardUseCase,
            getCardListUseCase
            ) as T
    }
}