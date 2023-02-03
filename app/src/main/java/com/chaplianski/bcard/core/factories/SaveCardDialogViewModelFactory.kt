package com.chaplianski.bcard.core.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chaplianski.bcard.core.viewmodels.SaveCardDialogViewModel
import com.chaplianski.bcard.domain.usecases.*
import javax.inject.Inject


@Suppress("UNCHECKED_CAST")
class SaveCardDialogViewModelFactory @Inject constructor(
    private val getCardsUseCase: GetCardsUseCase,

) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SaveCardDialogViewModel(
            getCardsUseCase,

        ) as T
    }
}