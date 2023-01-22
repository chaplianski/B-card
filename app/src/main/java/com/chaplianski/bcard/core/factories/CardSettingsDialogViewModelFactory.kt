package com.chaplianski.bcard.core.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chaplianski.bcard.core.viewmodels.CardSettingsDialogViewModel
import com.chaplianski.bcard.domain.usecases.GetCardUseCase
import javax.inject.Inject


@Suppress("UNCHECKED_CAST")
class CardSettingsDialogViewModelFactory @Inject constructor(
    private val getCardUseCase: GetCardUseCase
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CardSettingsDialogViewModel(getCardUseCase) as T
    }
}