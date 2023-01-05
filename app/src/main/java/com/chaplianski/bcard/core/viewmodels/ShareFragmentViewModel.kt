package com.chaplianski.bcard.core.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaplianski.bcard.domain.model.Card
import com.chaplianski.bcard.domain.usecases.AddCardUseCase
import com.chaplianski.bcard.domain.usecases.GetCardUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class ShareFragmentViewModel @Inject constructor(
    private val addCardUseCase: AddCardUseCase,
    private val getCardUseCase: GetCardUseCase
    ): ViewModel() {

    val _currentCard = MutableLiveData<Card>()
    val currentCard: LiveData<Card> get() = _currentCard


    fun addCard(card: Card){
        viewModelScope.launch(Dispatchers.IO) {
            addCardUseCase.execute(card)
        }
    }

    fun getCard(cardId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val card = getCardUseCase.execute(cardId)
            _currentCard.postValue(card)
        }

    }
}