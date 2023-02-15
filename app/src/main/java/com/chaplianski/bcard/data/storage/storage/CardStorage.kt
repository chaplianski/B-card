package com.chaplianski.bcard.data.storage.storage

import com.chaplianski.bcard.data.storage.modeldto.CardDTO
import kotlinx.coroutines.flow.Flow

interface CardStorage {

//    fun addCard(cardDTO: CardDTO): Long
    fun getCards(): List<CardDTO>
//    fun getCard(cardId: Long): CardDTO
//    fun updateCard(cardDTO: CardDTO)
//    fun deleteCard(cardId: Long)
}