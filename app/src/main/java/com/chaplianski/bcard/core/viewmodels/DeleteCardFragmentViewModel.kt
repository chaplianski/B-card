package com.chaplianski.bcard.core.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaplianski.bcard.domain.usecases.DeleteCardUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class DeleteCardFragmentViewModel @Inject constructor(private val deleteCardUseCase: DeleteCardUseCase): ViewModel() {

    fun deleteCard(cardId: Long){
        viewModelScope.launch(Dispatchers.IO) {
            deleteCardUseCase.execute(cardId)
        }
    }

    override fun onCleared() {
        viewModelScope.cancel()
    }
}