package com.chaplianski.bcard.core.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chaplianski.bcard.core.viewmodels.PersonInfoDialogViewModel
import com.chaplianski.bcard.domain.usecases.GetCardUseCase
import javax.inject.Inject
//
//@Suppress("UNCHECKED_CAST")
//class PersonInfoDialogViewModelFactory @Inject constructor(
//    private val getCardUseCase: GetCardUseCase
//): ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        return PersonInfoDialogViewModel(getCardUseCase) as T
//    }
//}