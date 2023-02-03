package com.chaplianski.bcard.core.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaplianski.bcard.domain.model.Card
import com.chaplianski.bcard.domain.usecases.GetCardsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SaveCardDialogViewModel @Inject constructor(
    private val getCardsUseCase: GetCardsUseCase,
): ViewModel() {

    private var _cards = MutableLiveData<List<Card>>()
    val cards: LiveData<List<Card>> get() = _cards
    var cardList = emptyList<Card>()

    fun getCards() {
        viewModelScope.launch(Dispatchers.IO) {
            cardList = getCardsUseCase.execute()
            _cards.postValue(cardList)
//            Log.d("MyLog", "Get cards: ${list.size}")
        }
    }
}