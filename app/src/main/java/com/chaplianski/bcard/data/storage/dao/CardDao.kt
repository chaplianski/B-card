package com.chaplianski.bcard.data.storage.dao

import android.util.Log
import androidx.room.*
import com.chaplianski.bcard.data.storage.modeldto.CardDTO
import com.chaplianski.bcard.data.storage.modeldto.UserDTO

@Dao
abstract class CardDao {

    @Query("SELECT * FROM cards")
    abstract fun getAllCards(): List<CardDTO>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertCard(cardDTO: CardDTO)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertUser(userDTO: UserDTO): Long

    @Query("DELETE FROM cards WHERE cards.id = :cardId")
    abstract fun deleteCard(cardId: Long)

//    fun updateCard(cardDTO: CardDTO){
//        val cardId = cardDTO.id
//        deleteCard(cardId)
//        cardDTO.id = cardId
//        insertCard(cardDTO)
//    }

    @Query("SELECT * FROM cards WHERE cards.id=:cardId")
    abstract fun getCard(cardId: Long): CardDTO

    @Update(onConflict = OnConflictStrategy.IGNORE)
    abstract fun updateCard(cardDTO: CardDTO)

    @Query("SELECT EXISTS (SELECT * FROM users WHERE users.email=:email AND users.password=:password)")
    abstract fun checkUser(email: String, password: String): Boolean

    @Query("SELECT * FROM users WHERE users.email=:email AND users.password=:password")
    abstract fun getUserId(email: String, password: String): UserDTO

    fun checkCurrentUser(userDTO: UserDTO): Long {
        val email = userDTO.email
        val password = userDTO.password
        val isExist = checkUser(email, password)
        return if (isExist) getUserId(email, password).id
        else -1
    }

}