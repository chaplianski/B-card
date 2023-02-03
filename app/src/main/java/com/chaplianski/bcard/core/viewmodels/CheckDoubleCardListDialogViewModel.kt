package com.chaplianski.bcard.core.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaplianski.bcard.domain.model.Card
import com.chaplianski.bcard.domain.usecases.AddCardUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class CheckDoubleCardListDialogViewModel @Inject constructor(
    private val addCardUseCase: AddCardUseCase,
): ViewModel() {

    fun addCard(card: Card) {
        viewModelScope.launch(Dispatchers.IO) {
            addCardUseCase.execute(card)
        }
    }

}