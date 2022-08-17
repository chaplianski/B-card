package com.chaplianski.bcard.data.storage.dao

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

    @Query ("SELECT * FROM cards WHERE cards.id=:cardId")
    abstract fun getCard(cardId: Long): CardDTO

    @Update (onConflict = OnConflictStrategy.IGNORE)
    abstract  fun updateCard(cardDTO: CardDTO)

    @Query("SELECT * FROM users WHERE users.email=:email AND users.password=:password")
    abstract fun checkUser(email: String, password: String): UserDTO

    fun checkCurrentUser(userDTO: UserDTO): Long{
        val email = userDTO.email
        val password = userDTO.password
        var user = UserDTO(-1, email, password)
        try {
            user = checkUser(email, password)
        } catch (e: Exception){

        }
        return user.id
    }

}