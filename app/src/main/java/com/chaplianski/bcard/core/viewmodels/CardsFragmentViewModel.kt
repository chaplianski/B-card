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
import com.chaplianski.bcard.domain.usecases.AddCardUseCase
import com.chaplianski.bcard.domain.usecases.DeleteCardUseCase
import com.chaplianski.bcard.domain.usecases.GetCardUseCase
import com.chaplianski.bcard.domain.usecases.GetCardsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File
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
    var cardList = emptyList<Card>()

    fun getCards() {
        viewModelScope.launch(Dispatchers.IO) {
            cardList = getCardsUseCase.execute()
            _cards.postValue(cardList)
//            Log.d("MyLog", "Get cards: ${list.size}")
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

    fun deleteCard(cardId: Long
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteCardUseCase.execute(cardId)


        }
    }

    fun deleteImage(imageUri: String, contentResolver: ContentResolver, launcher: ActivityResultLauncher<IntentSenderRequest>){


        val file = File(imageUri)
        val uri1 = file.absolutePath
        val uri  = uri1.toUri()

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




    fun addCard(card: Card) {
        addCardUseCase.execute(card)
    }

    fun sortCards(sortCategory: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val sortedList: List<Card> = when(sortCategory){
                SORT_NAME -> cardList.sortedBy { it.surname }
                SORT_PHONE -> cardList.sortedBy { it.workPhone }
                SORT_ORGANIZATION -> cardList.sortedBy { it.organization }
                SORT_TOWN -> cardList.sortedBy { it.town }
                else -> {cardList.sortedBy { it.town }}
            }
            _cards.postValue(sortedList)
        }
    }

    companion object {

        val SORT_NAME = "sort name"
        val SORT_ORGANIZATION = "sort organization"
        val SORT_PHONE = "sort phone"
        val SORT_TOWN = "sort town"

    }

}