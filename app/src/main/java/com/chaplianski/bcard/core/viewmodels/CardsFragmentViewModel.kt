package com.chaplianski.bcard.core.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaplianski.bcard.domain.model.Card
import com.chaplianski.bcard.domain.model.GetCardListState
import com.chaplianski.bcard.domain.usecases.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class CardsFragmentViewModel @Inject constructor(
    private val getCardListUseCase: GetCardListUseCase,
    private val addCardUseCase: AddCardUseCase,
    private val deleteCardUseCase: DeleteCardUseCase,
    private val getCardUseCase: GetCardUseCase,
    private val updateCardUseCase: UpdateCardUseCase,
    private val getCardListBySearchValueUseCase: GetCardListBySearchValueUseCase
    ) : ViewModel() {

    private var _cards = MutableLiveData<List<Card>>()
    val cards: LiveData<List<Card>> get() = _cards
    private var _currentCard = MutableLiveData<Card>()
    val currentCard: LiveData<Card> get() = _currentCard

    private var _getCardListState = MutableStateFlow<GetCardListState>(GetCardListState.Loading)
    val getCardListState get() = _getCardListState.asStateFlow()

    suspend fun getCards(fieldForSorting: String) {
        getCardListUseCase.execute(fieldForSorting)
               .onSuccess {
                   _getCardListState.emit(GetCardListState.GetCardList(it)) }
               .onFailure {  }
    }

    suspend fun getCardListBySearchValue(searchValue: String){
        getCardListBySearchValueUseCase.execute(searchValue)
            .onSuccess {
                _getCardListState.emit(GetCardListState.GetCardList(it))
            }
            .onFailure {  }
    }
     fun getCard(cardId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val card = getCardUseCase.execute(cardId)
            _currentCard.postValue(card)
        }
    }

    override fun onCleared() {
        viewModelScope.cancel()
    }

    suspend fun deleteCard(cardId: Long) {
        deleteCardUseCase.execute(cardId)
            .onSuccess { _getCardListState.emit(GetCardListState.DeleteCard(it)) }
            .onFailure {  }
    }

    suspend fun addCard(card: Card){
        addCardUseCase.execute(card)
            .onSuccess {_getCardListState.emit(GetCardListState.AddCard(it))}
            .onFailure {  }
    }

    suspend fun updateCard(card: Card){
        updateCardUseCase.execute(card)
            .onSuccess { _getCardListState.emit(GetCardListState.UpdateCard(it)) }
            .onFailure {  }
    }
}