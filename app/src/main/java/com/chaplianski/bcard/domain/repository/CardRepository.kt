package com.chaplianski.bcard.domain.repository

import com.chaplianski.bcard.domain.model.Card

interface CardRepository {

    fun addCard(card: Card)

    fun getCards(): List<Card>

    fun getCard(cardId: Long): Card

    fun updateCard(card: Card)

    fun deleteCard(cardId: Long)

}