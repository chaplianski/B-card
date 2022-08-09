package com.chaplianski.bcard.data.storage.dao

import androidx.room.*
import com.chaplianski.bcard.data.storage.modeldto.CardDTO

@Dao
abstract class CardDao {

    @Query("SELECT * FROM cards")
    abstract fun getAllCards(): List<CardDTO>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertCard(cardDTO: CardDTO)

    @Query("DELETE FROM cards WHERE cards.id = :cardId")
    abstract fun deleteCard(cardId: Long)

//    fun updateCard(cardDTO: CardDTO){
//        val cardId = cardDTO.id
//        deleteCard(cardId)
//        cardDTO.id = cardId
//        insertCard(cardDTO)
//    }

    @Query ("SELECT * FROM cards WHERE cards.id=:cardId")
    abstract fun getCard(cardId: Long): CardDTO

    @Update (onConflict = OnConflictStrategy.IGNORE)
    abstract  fun updateCard(cardDTO: CardDTO)


}