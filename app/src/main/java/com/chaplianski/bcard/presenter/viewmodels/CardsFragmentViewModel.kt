package com.chaplianski.bcard.presenter.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaplianski.bcard.domain.model.Card
import com.chaplianski.bcard.domain.usecases.AddCardUseCase
import com.chaplianski.bcard.domain.usecases.GetCardsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class CardsFragmentViewModel @Inject constructor(
    private val getCardUseCase: GetCardsUseCase,
    private val addCardUseCase: AddCardUseCase,
                                                 ) : ViewModel() {

    var _cards = MutableLiveData<List<Card>>()
    val cards: LiveData<List<Card>> get() = _cards
    var _currentCard = MutableLiveData<List<Any>>()
    val currentCard: LiveData<List<Any>> get() = _currentCard

    fun getCards (){
        viewModelScope.launch(Dispatchers.IO) {
            val list = getCardUseCase.execute()
            _cards.postValue(list)
        }
    }

    fun transferData(name: String, avatarUri: String, id: Long){
        viewModelScope.launch(Dispatchers.IO) {
          val list = listOf<Any>(name, avatarUri, id)
           _currentCard.postValue(list)
        }
    }

    override fun onCleared() {
        viewModelScope.cancel()
    }

    fun insertPhoto(imageUri: Uri) {

    }

    fun addCard(card: Card){
        addCardUseCase.execute(card)
    }

}