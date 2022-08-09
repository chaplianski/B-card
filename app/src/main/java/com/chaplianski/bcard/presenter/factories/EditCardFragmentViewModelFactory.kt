package com.chaplianski.bcard.presenter.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chaplianski.bcard.domain.usecases.AddCardUseCase
import com.chaplianski.bcard.domain.usecases.GetCardUseCase
import com.chaplianski.bcard.domain.usecases.UpdateCardUseCase
import com.chaplianski.bcard.presenter.viewmodels.EditCardFragmentViewModel
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class EditCardFragmentViewModelFactory @Inject constructor(
    private val addCardUseCase: AddCardUseCase,
    private val getCardUseCase: GetCardUseCase,
    private val updateCardUseCase: UpdateCardUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EditCardFragmentViewModel(addCardUseCase, getCardUseCase, updateCardUseCase) as T
    }
}