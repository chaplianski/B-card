package com.chaplianski.bcard.core.viewmodels

import androidx.lifecycle.*
import com.chaplianski.bcard.domain.model.Card
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

    fun getCardList(fieldForSorting: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getCardListUseCase.execute(fieldForSorting)
                .onSuccess {
                    _getCardListState.emit(GetCardListState.GetCardList(it)) }
                .onFailure {  }
        }
    }

    fun getCardListBySearchValue(searchValue: String) = viewModelScope.launch(Dispatchers.IO) {
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

    fun deleteCard(cardId: Long) = viewModelScope.launch(Dispatchers.IO) {
        deleteCardUseCase.execute(cardId)
            .onSuccess { _getCardListState.emit(GetCardListState.DeleteCard(it)) }
            .onFailure {  }
    }

    fun addCard(card: Card) = viewModelScope.launch(Dispatchers.IO) {
        addCardUseCase.execute(card)
            .onSuccess {_getCardListState.emit(GetCardListState.AddCard(it))}
            .onFailure {  }
    }

    fun updateCard(card: Card) = viewModelScope.launch(Dispatchers.IO) {
        updateCardUseCase.execute(card)
            .onSuccess { _getCardListState.emit(GetCardListState.UpdateCard(it)) }
            .onFailure {  }
    }

    sealed class GetCardListState{
        object Loading: GetCardListState()
        class GetCardList(val cardList: List<Card>): GetCardListState()
        class UpdateCard(val result: Int): GetCardListState()
        class DeleteCard(val result: Int): GetCardListState()
        class AddCard(val cardId: Long): GetCardListState()
        class Failure(val exception: String): GetCardListState()

    }
}