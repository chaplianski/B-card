package com.chaplianski.bcard.data.storage.database

import com.chaplianski.bcard.data.storage.dao.CardDao
import com.chaplianski.bcard.data.storage.modeldto.CardDTO
import com.chaplianski.bcard.data.storage.storage.CardStorage
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CardStorageImpl @Inject constructor(private val cardDao: CardDao): CardStorage {

//    @Inject
//    lateinit var cardDao: CardDao

//    override fun addCard(cardDTO: CardDTO) = cardDao.insertCard(cardDTO)

    override fun getCards(): List<CardDTO> {
        return cardDao.getAllCards()
    }

//    override fun getCard(cardId: Long): CardDTO {
//        return cardDao.getCard(cardId)
//    }

//    override fun updateCard(cardDTO: CardDTO) {
//        cardDao.updateCard(cardDTO)
//    }

//    override fun deleteCard(cardId: Long) {
//        cardDao.deleteCard(cardId)
//    }
}