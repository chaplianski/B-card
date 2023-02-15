package com.chaplianski.bcard.core.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaplianski.bcard.domain.model.Card
import com.chaplianski.bcard.domain.usecases.GetCardListUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class CheckCardListFragmentViewModel @Inject constructor(
    private val getCardListUseCase: GetCardListUseCase,
): ViewModel() {

    val _cardList = MutableLiveData<List<Card>>()
    val cardList: LiveData<List<Card>> get() = _cardList

//    fun getCardList(){
//       viewModelScope.launch(Dispatchers.IO) {
//           val list = getCardListUseCase.execute()
//           _cardList.postValue(list)
//       }
//
//    }

}