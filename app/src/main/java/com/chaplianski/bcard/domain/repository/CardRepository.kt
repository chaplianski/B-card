package com.chaplianski.bcard.domain.repository

import com.chaplianski.bcard.domain.model.Card
import kotlinx.coroutines.flow.Flow

interface CardRepository {

    fun addCard(card: Card): Long

    fun getCardList(fieldForSorting: String): List<Card>

    fun getCard(cardId: Long): Card

    fun updateCard(card: Card): Int

    fun deleteCard(cardId: Long): Int

}