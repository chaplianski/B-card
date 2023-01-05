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

class CheckCardListFragmentViewModel @Inject constructor(
    private val getCardsUseCase: GetCardsUseCase,
): ViewModel() {

    val _cardList = MutableLiveData<List<Card>>()
    val cardList: LiveData<List<Card>> get() = _cardList

    fun getCardList(){
       viewModelScope.launch(Dispatchers.IO) {
           val list = getCardsUseCase.execute()
           _cardList.postValue(list)
       }

    }

}