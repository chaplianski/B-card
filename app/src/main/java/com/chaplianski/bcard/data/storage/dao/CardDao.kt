package com.chaplianski.bcard.data.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chaplianski.bcard.data.storage.modeldto.CardDTO

@Dao
abstract class CardDao {

    @Query("SELECT * FROM cards")
    abstract fun getAllCards(): List<CardDTO>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertCard(cardDTO: CardDTO)

    @Query("DELETE FROM cards WHERE id = :cardId")
    abstract fun deleteCard(cardId: Long)

    fun updateCard(cardDTO: CardDTO){
        val cardId = cardDTO.id
        deleteCard(cardId)
        cardDTO.id = cardId
        insertCard(cardDTO)
    }


}