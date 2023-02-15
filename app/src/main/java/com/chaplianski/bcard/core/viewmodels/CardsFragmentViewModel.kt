package com.chaplianski.bcard.core.viewmodels

import android.app.PendingIntent
import android.app.RecoverableSecurityException
import android.content.ContentResolver
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaplianski.bcard.domain.model.Card
import com.chaplianski.bcard.domain.model.DeleteCardState
import com.chaplianski.bcard.domain.model.GetCardListState
import com.chaplianski.bcard.domain.model.UpdateCardState
import com.chaplianski.bcard.domain.usecases.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


class CardsFragmentViewModel @Inject constructor(
    private val getCardListUseCase: GetCardListUseCase,
    private val addCardUseCase: AddCardUseCase,
    private val deleteCardUseCase: DeleteCardUseCase,
    private val getCardUseCase: GetCardUseCase,
    private val updateCardUseCase: UpdateCardUseCase
    ) : ViewModel() {

    private var _cards = MutableLiveData<List<Card>>()
    val cards: LiveData<List<Card>> get() = _cards
    private var _currentCard = MutableLiveData<Card>()
    val currentCard: LiveData<Card> get() = _currentCard

//    private var _addCardState = MutableStateFlow<AddCardState>(AddCardState.Loading)
//    val addCardsState get() = _addCardState.asStateFlow()
//    private var _deleteCardState = MutableStateFlow<DeleteCardState>(DeleteCardState.Loading)
//    val deleteCardsState get() = _deleteCardState.asStateFlow()
//    private var _updateCardState = MutableStateFlow<UpdateCardState>(UpdateCardState.Loading)
//    val updateCardsState get() = _updateCardState.asStateFlow()
    private var _getCardListState = MutableStateFlow<GetCardListState>(GetCardListState.Loading)
    val getCardListState get() = _getCardListState.asStateFlow()

//    var cardList = emptyList<Card>()

    suspend fun getCards(fieldForSorting: String) {
        Log.d("MyLog", "cardList 2")
        getCardListUseCase.execute(fieldForSorting)
               .onSuccess {
                   Log.d("MyLog", "cardList 5 = $it")
                   _getCardListState.emit(GetCardListState.GetCardList(it)) }
               .onFailure {  }
    }

    suspend fun switchToLoadingStateGetCards(){
        _getCardListState.emit(GetCardListState.Loading)
    }
     fun getCard(cardId: Long) {
        Log.d("MyLog", "card id in vm = $cardId")
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

    suspend fun deleteCard(cardId: Long) {
        deleteCardUseCase.execute(cardId)
            .onSuccess { _getCardListState.emit(GetCardListState.DeleteCard(it)) }
            .onFailure {  }
//        deleteCardUseCase.execute(cardId)
//                .onSuccess { _deleteCardState.emit(DeleteCardState.Success(it)) }
//                .onFailure {  }

    }

    fun deleteImage(
        imageUri: String,
        contentResolver: ContentResolver,
        launcher: ActivityResultLauncher<IntentSenderRequest>
    ) {


        val file = File(imageUri)
        val uri1 = file.absolutePath
        val uri = uri1.toUri()

        Log.d("MyLog", "uri1 = $uri")
        try {
            contentResolver.delete(uri, null, null)
        } catch (e: SecurityException) {
            var pendingIntent: PendingIntent? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val collection = ArrayList<Uri>()
                collection.add(uri)
                pendingIntent = MediaStore.createDeleteRequest(contentResolver, collection)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                //if exception is recoverable then again send delete request using intent
                if (e is RecoverableSecurityException) {
                    pendingIntent = e.userAction.actionIntent
                }
            }
            if (pendingIntent != null) {
                val sender = pendingIntent.intentSender
                val request = IntentSenderRequest.Builder(sender).build()
                launcher.launch(request)
            }
        }
    }


//    suspend fun addCard(card: Card) {
//        viewModelScope.launch(Dispatchers.IO) {
//            addCardUseCase.execute(card)
//        }
//    }

    suspend fun addCard(card: Card){
        addCardUseCase.execute(card)
            .onSuccess {_getCardListState.emit(GetCardListState.AddCard(it))}
            .onFailure {  }
//        addCardUseCase.execute(card)
//            .onSuccess {_addCardState.emit(AddCardState.Success(it))}
//            .onFailure {  }

    }

    suspend fun updateCard(card: Card){
        updateCardUseCase.execute(card)
            .onSuccess { _getCardListState.emit(GetCardListState.UpdateCard(it)) }
            .onFailure {  }
//        updateCardUseCase.execute(card)
//            .onSuccess { _updateCardState.emit(UpdateCardState.Success(it)) }
//            .onFailure {  }
    }

//    fun sortCards(sortCategory: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val sortedList: List<Card> = when (sortCategory) {
//                SORT_NAME -> cardList.sortedBy { it.surname }
//                SORT_PHONE -> cardList.sortedBy { it.workPhone }
//                SORT_ORGANIZATION -> cardList.sortedBy { it.organization }
//                SORT_TOWN -> cardList.sortedBy { it.town }
//                else -> {
//                    cardList.sortedBy { it.town }
//                }
//            }
//            _cards.postValue(sortedList)
//        }
//    }

    sealed class AddCardState{
       object Loading: AddCardState()
        class Success(val cardId: Long): AddCardState()
        class Failure(val exception: String): AddCardState()

    }



    companion object {

        val SORT_NAME = "sort name"
        val SORT_ORGANIZATION = "sort organization"
        val SORT_PHONE = "sort phone"
        val SORT_TOWN = "sort town"

    }

}