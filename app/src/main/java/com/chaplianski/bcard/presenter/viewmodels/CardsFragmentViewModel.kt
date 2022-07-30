package com.chaplianski.bcard.presenter.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaplianski.bcard.domain.model.Card
import com.chaplianski.bcard.domain.usecases.GetCardUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class CardsFragmentViewModel @Inject constructor(private val getCardUseCase: GetCardUseCase) : ViewModel() {

    var _cards = MutableLiveData<List<Card>>()
    val cards: LiveData<List<Card>> get() = _cards

    fun getCards (){
        viewModelScope.launch(Dispatchers.IO) {
            val list = getCardUseCase.execute()
            _cards.postValue(list)
        }
    }

    override fun onCleared() {
        viewModelScope.cancel()
    }

}