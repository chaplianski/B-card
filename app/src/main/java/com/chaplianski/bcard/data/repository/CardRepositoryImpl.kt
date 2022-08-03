package com.chaplianski.bcard.data.repository

import com.chaplianski.bcard.data.storage.database.CardStorageImpl
import com.chaplianski.bcard.domain.model.Card
import com.chaplianski.bcard.domain.repository.CardRepository
import javax.inject.Inject

class CardRepositoryImpl @Inject constructor(private val cardStorageImpl: CardStorageImpl): CardRepository {

    override fun addCard(card: Card) {
        cardStorageImpl.addCard(card.cardMapDomainToData())
    }

    override fun getCards(): List<Card> {
//        return cardStorageImpl.getCards().map { it.cardMapDataToDomain() }
        val cardList = mutableListOf<Card>()
        cardList.add(Card(0, "Mike Naumenko", "", "+12345434543", "+34545345345", "mike@gmai.com", "Android developer", "Minsk, Belarus"))
        cardList.add(Card(1, "Andrew Moiseenkov", "", "+12345434543", "+34545345345", "mike@gmai.com", "Android developer", "Pinsk, Belarus"))
        cardList.add(Card(2, "Yaroslav Butetskiy", "", "+12345434543", "+34545345345", "mike@gmai.com", "Android developer", "Baranovichi, Belarus"))
        cardList.add(Card(3, "Kirrill Vastnecov", "", "+12345434543", "+34545345345", "mike@gmai.com", "Android developer", "Mogilev, Belarus"))
        cardList.add(Card(4, "John R. Richardson", "", "+12345434543", "+34545345345", "mike@gmai.com", "Android developer", "Berninsgem, Greate Britaine"))
        return cardList.toList()
    }
}