package com.chaplianski.bcard.core.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaplianski.bcard.domain.model.Card
import com.chaplianski.bcard.domain.usecases.GetCardUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class AdditionalInfoDialogViewModel @Inject constructor(
    private val getCardUseCase: GetCardUseCase
): ViewModel() {

    private val _currentCard = MutableLiveData<Card>()
    val currentCard: LiveData<Card> get() = _currentCard

    fun getCardData(cardId: Long){
        viewModelScope.launch(Dispatchers.IO) {
            val card = getCardUseCase.execute(cardId)
            _currentCard.postValue(card)
        }
    }

}