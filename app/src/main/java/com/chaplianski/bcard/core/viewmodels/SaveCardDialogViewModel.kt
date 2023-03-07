package com.chaplianski.bcard.core.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chaplianski.bcard.domain.model.Card
import com.chaplianski.bcard.domain.usecases.GetCardListUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class SaveCardDialogViewModel @Inject constructor(
    private val getCardListUseCase: GetCardListUseCase,
): ViewModel() {

    private var _cards = MutableLiveData<List<Card>>()
    val cards: LiveData<List<Card>> get() = _cards
    var cardList = emptyList<Card>()
    private var _getCardListState = MutableStateFlow<GetCardsState>(GetCardsState.Loading)
    val getCardListState get() = _getCardListState.asStateFlow()

    suspend fun getCards(fieldBySorting: String) {
        getCardListUseCase.execute(fieldBySorting)
            .onSuccess {
                _getCardListState.emit(GetCardsState.Success(it)) }
            .onFailure {  }
    }

    sealed class GetCardsState{
        object Loading: GetCardsState()
        class Success(val cardList: List<Card>): GetCardsState()
        class Failure(val exception: String): GetCardsState()
    }
}