package com.chaplianski.bcard.data.repository

import com.chaplianski.bcard.data.storage.dao.CardDao
import com.chaplianski.bcard.data.storage.database.CardStorageImpl
import com.chaplianski.bcard.domain.model.Card
import com.chaplianski.bcard.domain.repository.CardRepository
import javax.inject.Inject

class CardRepositoryImpl @Inject constructor(
    private val cardDao: CardDao,
    private val cardStorageImpl: CardStorageImpl
    ): CardRepository {

//    override fun addCard(card: Card) = cardStorageImpl.addCard(card.cardMapDomainToData())
    override fun addCard(card: Card) = cardDao.insertCard(card.cardMapDomainToData())

    override fun getCardList(fieldForSorting: String): List<Card> =
        cardDao.getSortedCardList(fieldForSorting).map { it.cardMapDataToDomain() }

    override fun getCard(cardId: Long):
            Card = cardDao.getCard(cardId).cardMapDataToDomain()

    override fun updateCard(card: Card) = cardDao.updateCard(card.cardMapDomainToData())

    override fun deleteCard(cardId: Long) = cardDao.deleteCard(cardId)

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