package com.chaplianski.bcard.data.repository

import android.util.Log
import com.chaplianski.bcard.data.storage.database.CardStorageImpl
import com.chaplianski.bcard.domain.model.Card
import com.chaplianski.bcard.domain.repository.CardRepository
import javax.inject.Inject

class CardRepositoryImpl @Inject constructor(private val cardStorageImpl: CardStorageImpl): CardRepository {

    override fun addCard(card: Card) {
        cardStorageImpl.addCard(card.cardMapDomainToData())
    }

    override fun getCards(): List<Card> {
        return cardStorageImpl.getCards().map { it.cardMapDataToDomain() }
    }


    override fun getCard(cardId: Long): Card {
        return cardStorageImpl.getCard(cardId).cardMapDataToDomain()
    }

    override fun updateCard(card: Card) {
        cardStorageImpl.updateCard(card.cardMapDomainToData())
    }

    override fun deleteCard(cardId: Long) {
        cardStorageImpl.deleteCard(cardId)
        Log.d("MyLog", "repository Impl delete")
    }
}