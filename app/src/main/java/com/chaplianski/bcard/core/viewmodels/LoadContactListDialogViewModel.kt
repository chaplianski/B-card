package com.chaplianski.bcard.core.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaplianski.bcard.domain.model.Card
import com.chaplianski.bcard.domain.usecases.AddCardUseCase
import com.chaplianski.bcard.domain.usecases.GetCardListUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoadContactListDialogViewModel @Inject constructor(
    private val addCardUseCase: AddCardUseCase,
    private val getCardListUseCase: GetCardListUseCase,
) : ViewModel() {

    private var _getCardListState = MutableStateFlow<GetCardsState>(
        GetCardsState.Loading)
    val getCardListState get() = _getCardListState.asStateFlow()

    suspend fun getCards(fieldBySorting: String) {
        getCardListUseCase.execute(fieldBySorting)
            .onSuccess { _getCardListState.emit(GetCardsState.Success(it)) }
            .onFailure {  }
    }

    fun addCard(card: Card) {
        viewModelScope.launch(Dispatchers.IO) {
            addCardUseCase.execute(card)
        }
    }


    sealed class GetCardsState{
        object Loading: GetCardsState()
        class Success(val cardList: List<Card>): GetCardsState()
        class Failure(val exception: String): GetCardsState()

    }
}