package com.chaplianski.bcard.core.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chaplianski.bcard.core.viewmodels.CheckDoubleCardListDialogViewModel
import com.chaplianski.bcard.domain.usecases.AddCardUseCase
import javax.inject.Inject


@Suppress("UNCHECKED_CAST")
class CheckDoubleCardListDialogViewModelFactory @Inject constructor(
    private val addCardUseCase: AddCardUseCase
       ) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CheckDoubleCardListDialogViewModel(
            addCardUseCase
        ) as T
    }
}