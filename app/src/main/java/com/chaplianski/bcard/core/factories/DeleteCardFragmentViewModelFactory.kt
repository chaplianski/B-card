package com.chaplianski.bcard.core.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chaplianski.bcard.domain.usecases.DeleteCardUseCase
import com.chaplianski.bcard.core.viewmodels.DeleteCardFragmentViewModel
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class DeleteCardFragmentViewModelFactory @Inject constructor(private val deleteCardUseCase: DeleteCardUseCase): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DeleteCardFragmentViewModel(deleteCardUseCase) as T
    }
}