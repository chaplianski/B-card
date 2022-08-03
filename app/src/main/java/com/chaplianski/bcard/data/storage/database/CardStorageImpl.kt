package com.chaplianski.bcard.data.storage.database

import com.chaplianski.bcard.data.storage.dao.CardDao
import com.chaplianski.bcard.data.storage.modeldto.CardDTO
import com.chaplianski.bcard.data.storage.storage.CardStorage
import javax.inject.Inject

class CardStorageImpl @Inject constructor(): CardStorage {

    @Inject
    lateinit var cardDao: CardDao

    override fun addCard(cardDTO: CardDTO) {
        cardDao.insertCard(cardDTO)
    }

    override fun getCards(): List<CardDTO> {
        return cardDao.getAllCards()
    }
}