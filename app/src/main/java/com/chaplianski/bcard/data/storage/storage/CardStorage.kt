package com.chaplianski.bcard.data.storage.storage

import com.chaplianski.bcard.data.storage.modeldto.CardDTO

interface CardStorage {

    fun addCard(cardDTO: CardDTO)
    fun getCards(): List<CardDTO>
}