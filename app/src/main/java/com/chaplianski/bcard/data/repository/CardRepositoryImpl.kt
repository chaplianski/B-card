package com.chaplianski.bcard.data.repository

import android.content.Context
import com.chaplianski.bcard.core.utils.CURRENT_USER_ID
import com.chaplianski.bcard.data.storage.dao.CardDao
import com.chaplianski.bcard.domain.model.Card
import com.chaplianski.bcard.domain.repository.CardRepository
import javax.inject.Inject

class CardRepositoryImpl @Inject constructor(
    private val cardDao: CardDao,
//    private val cardStorageImpl: CardStorageImpl,
    private val context: Context
    ): CardRepository {

//    override fun addCard(card: Card) = cardStorageImpl.addCard(card.cardMapDomainToData())
    override fun addCard(card: Card): Long {
    if (currentId != null) {
        card.userId = currentId
    }
    return cardDao.insertCard(card.cardMapDomainToData())
}

    val sharedPref = context.getSharedPreferences("data", Context.MODE_PRIVATE)
    val currentId = sharedPref?.getLong(CURRENT_USER_ID, -1)

    override fun getCardList(fieldForSorting: String): List<Card> {
        return if (currentId != null){
            cardDao.getSortedCardList(fieldForSorting, currentId).map { it.cardMapDataToDomain() }
        } else emptyList()
    }


    override fun getCard(cardId: Long):
            Card = cardDao.getCard(cardId).cardMapDataToDomain()

    override fun updateCard(card: Card) = cardDao.updateCard(card.cardMapDomainToData())

    override fun deleteCard(cardId: Long) = cardDao.deleteCard(cardId)
    override fun getCardListBySearchValue(searchValue: String): List<Card>{
        return if (currentId != null){
            cardDao.getAllCardsBySearchValue(searchValue, currentId).map { it.cardMapDataToDomain()}
        } else emptyList()
    }





//        Log.d("MyLog", "getCardId = $cardId")
//        val cardDTO = cardStorageImpl.getCard(cardId) ?: CardDTO()
//        Log.d("MyLog", "cardDTO = $cardDTO")
//
//        val card = cardDTO.cardMapDataToDomain()
//        Log.d("MyLog", "card = $card")
//        return card
//    }


//        {
//        cardStorageImpl.updateCard(card.cardMapDomainToData())
//    }


//    {
//        Log.d("MyLog", "deleteCardId = $cardId")
//        cardStorageImpl.deleteCard(cardId)
////        Log.d("MyLog", "repository Impl delete")
//    }
}