package com.chaplianski.bcard.presenter.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaplianski.bcard.domain.model.Card
import com.chaplianski.bcard.domain.usecases.AddCardUseCase
import com.chaplianski.bcard.domain.usecases.DeleteCardUseCase
import com.chaplianski.bcard.domain.usecases.GetCardUseCase
import com.chaplianski.bcard.domain.usecases.GetCardsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class CardsFragmentViewModel @Inject constructor(
    private val getCardsUseCase: GetCardsUseCase,
    private val addCardUseCase: AddCardUseCase,
    private val deleteCardUseCase: DeleteCardUseCase,
    private val getCardUseCase: GetCardUseCase
) : ViewModel() {

    private var _cards = MutableLiveData<List<Card>>()
    val cards: LiveData<List<Card>> get() = _cards
    private var _currentCard = MutableLiveData<Card>()
    val currentCard: LiveData<Card> get() = _currentCard

    fun getCards() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = getCardsUseCase.execute()
            _cards.postValue(list)
            Log.d("MyLog", "Get cards: ${list.size}")
        }
    }

    fun getCard(cardId: Long){
        viewModelScope.launch(Dispatchers.IO) {
            val card = getCardUseCase.execute(cardId)
            _currentCard.postValue(card)
        }
    }

//    fun transferData(name: String, avatarUri: String, id: Long) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val list = listOf<Any>(name, avatarUri, id)
//            _currentCard.postValue(list)
//        }
//    }

    override fun onCleared() {
        viewModelScope.cancel()
    }

    fun deleteCard(cardId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteCardUseCase.execute(cardId)
        }
    }

    fun addCard(card: Card) {
        addCardUseCase.execute(card)
    }

}